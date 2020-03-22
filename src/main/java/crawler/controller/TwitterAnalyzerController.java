package crawler.controller;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import crawler.controller.cosmos.Tweet;
import crawler.controller.cosmos.TweetDaoController;
import crawler.controller.cosmos.TweetDaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class TwitterAnalyzerController {

    private static final List<String> HASHTAGS = Lists.newArrayList("#covid", "#covid_19", "#coronavirus", "#CoronavirusPandemic", "#covid_19DE", "#COVID19outbreak");

    private final com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();

    private final Logger logger = LoggerFactory.getLogger(TwitterAnalyzerController.class.getName());

    private Client hosebirdClient;

    private final BlockingDeque<String> msgQueue = new LinkedBlockingDeque<>(1000);

    private final Authentication hosebirdAuth = new OAuth1(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET,
            Constants.TOKEN, Constants.SECRET);

    private final StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();


    private final TweetDaoController tweetDaoController = new TweetDaoController(TweetDaoFactory.getTweetDao());
    /**
     * Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth)
     */
    private final Hosts hosebirdHosts = new HttpHosts(com.twitter.hbc.core.Constants.STREAM_HOST);

    private static final String HOSEBIRD_CLIENT_NAME = "Hosebird-Client-01";  // optional: mainly for the logs

    /**
     * @param numberOfTweets
     * @return List<String> the list of tweets in json format
     * @throws InterruptedException
     */
    public Map<String, Integer> pollTerms(final int numberOfTweets, final List<String> terms) {

        final List<Tweet> collectedTweets = new ArrayList();
        int tweetsPolled = 0;
        int tweetsAccepted = 0;

        //terms.add("fake");

        for (final String term : terms) {

            final Tweet tweet1 = tweetDaoController.getTweet(term);
            if (tweet1 == null) {
                continue;
            }

            getWeights(tweet1.getText(), terms);
            collectedTweets.add(tweet1);
        }


        logger.info("Number of tweets: " + numberOfTweets);
        logger.info("tweetsPolled: " + tweetsPolled);
        logger.info("tweetsAccepted: " + tweetsAccepted);

        final Map<String, Integer> results = new HashMap<>();
        results.put("tweets_put: ", tweetsAccepted);
        return results;
    }

    /**
     * @param numberOfTweets
     * @param hashtags
     * @param lang
     * @return
     */
    public Map<String, Integer> putHashtags(final int numberOfTweets, final List<String> hashtags, final String lang) {

        int tweetsAccepted = 0;
        doConnection(hashtags);

        while (!hosebirdClient.isDone() && numberOfTweets > tweetsAccepted) {

            try {

                final String strTweet = msgQueue.poll(5, TimeUnit.SECONDS);
                final Tweet tweet = extractText(strTweet, lang);

                if (tweet == null) {
                    continue;
                }

                final Tweet tweetDb = tweetDaoController.persist(tweet);
                logger.info(tweetDb.getText());
                tweetsAccepted++;


            } catch (InterruptedException e) {
                e.printStackTrace();
                hosebirdClient.stop();
            }
        }

        final Map<String, Integer> results = new HashMap<>();
        results.put("tweets_put: ", tweetsAccepted);
        return results;

    }

    public Tweet extractText(final String strTweet, final String lang) {

        final JsonParser jsonParser = new JsonParser();
        JsonObject jsonTweet = jsonParser.parse(strTweet).getAsJsonObject();

        JsonElement textElement = jsonTweet.get("text");
        JsonElement langElement = jsonTweet.get("lang");

        //   if (textElement == null || textElement.toString().startsWith("\"RT @") || langElement == null || !langElement.toString().equals(lang)) {
        if (textElement == null || langElement == null || !langElement.toString().equals(lang)) {
            return null;
        }

        final JsonElement strIdElem = jsonTweet.get("id_str");

        final Tweet tweetObj = new Tweet();
        tweetObj.setId(strIdElem.getAsString());
        tweetObj.setText(textElement.getAsString().replace("\\", "").replace("\\n", ""));
        tweetObj.setLang(langElement.getAsString().replace("\\", ""));

        return tweetObj;
    }

    public Integer getWeights(final String text, final List<String> terms) {

        if (text == null || terms == null || terms.size() == 0) {
            return 0;
        }

        int count = 0;
        for (final String term : terms) {
            if (!text.toLowerCase().contains(term)) {
                count++;
            }
        }

        return count / terms.size();
    }

    public Integer extractUserFollowersInTweet(final String tweet) {

        final JsonElement user = jsonParser.parse(tweet)
                .getAsJsonObject()
                .get("user");

        return user != null ? user
                .getAsJsonObject()
                .get("followers_count")
                .getAsInt() : 0;

    }

    /**
     * Build the client and starts connection
     */
    public void doConnection(final List<String> terms) {

        hosebirdEndpoint.trackTerms(terms);

        final ClientBuilder builder = new ClientBuilder()
                .name(HOSEBIRD_CLIENT_NAME)
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        // no timeout (1000 days).
        builder.socketTimeout(60 * 60 * 24 * 1000);

        hosebirdClient = builder.build();
        hosebirdClient.connect();
        logger.info("End of application");
    }

    /**
     * stops the client
     */
    public void stop() {

        if (hosebirdClient != null) {
            hosebirdClient.stop();

        }
    }
}
