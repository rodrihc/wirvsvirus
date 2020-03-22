package crawler.controller.cosmos;

import lombok.NonNull;

import java.util.List;

public class TweetDaoController {

    public static TweetDaoController getInstance() {

        if (tweetDaoController == null) {
            tweetDaoController = new TweetDaoController(TweetDaoFactory.getTweetDao());
        }
        return tweetDaoController;
    }

    private static TweetDaoController tweetDaoController;

    private final TweetDao tweetDao;

    public TweetDaoController(TweetDao tweetDao) {
        this.tweetDao = tweetDao;
    }

    public Tweet persist(@NonNull final Tweet tweet) {

        return tweetDao.createTweet(tweet);
    }

    public Tweet persist(@NonNull String id, @NonNull String lang, @NonNull String text) {

        final Tweet tweet = new Tweet();
        tweet.setId(id);
        tweet.setLang(lang);
        tweet.setText(text);

        return tweetDao.createTweet(tweet);
    }

    public boolean deleteTweet(@NonNull String id) {
        return tweetDao.deleteTweet(id);
    }

    public Tweet getTweet(@NonNull String id) {
        return tweetDao.readTweet(id);
    }

    public List<Tweet> getTweet() {
        return tweetDao.readTweet();
    }

    public Tweet updateTweet(@NonNull String id, String lang, String text) {
        return tweetDao.updateTweet(id, lang, text);
    }
}
