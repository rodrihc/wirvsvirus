import com.google.common.collect.Lists;
import crawler.controller.TwitterAnalyzerController;
import crawler.controller.cosmos.Tweet;
import crawler.controller.cosmos.TweetDaoController;
import utils.JsonParser;

import java.util.List;
import java.util.Map;

public class Application {

    private static final List<String> TERMS = Lists.newArrayList("ibuprofen");

    public static void main(String[] args) {

        final TwitterAnalyzerController twitterAnalyzerController = new TwitterAnalyzerController();
        final TweetDaoController tweetDaoController = TweetDaoController.getInstance();

        try {

            // "\"de\"") && !langElement.toString().equals("\"en\"")
            twitterAnalyzerController.doConnection(TERMS);
            final Map<String, Integer> tweets = twitterAnalyzerController.pollTerms(1000, TERMS);

            String jsonTweetsList = JsonParser.toJson(tweets);
            System.out.println(jsonTweetsList);

          /*  List<Topic> topics = topicController.getTopics();
            for (Topic topic : topics) {
                System.out.println(topic);
            }*/


        } catch (final Exception e) {
            twitterAnalyzerController.stop();
            e.printStackTrace();

        }
    }

}
