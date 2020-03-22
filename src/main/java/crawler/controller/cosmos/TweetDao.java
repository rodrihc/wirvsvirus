package crawler.controller.cosmos;

import java.util.List;

public interface TweetDao {

    Tweet createTweet(Tweet tweet);

    Tweet readTweet(String id);

    List<Tweet> readTweet();

    Tweet updateTweet(String id, String lang, String text);

    boolean deleteTweet(String id);
}
