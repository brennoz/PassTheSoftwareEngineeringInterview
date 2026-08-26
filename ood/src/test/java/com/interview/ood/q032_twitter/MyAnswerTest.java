package com.interview.ood.q032_twitter;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyAnswerTest {

    @Test
    void follower_sees_tweet_in_feed_after_follow() {
        User alice = new User("1", "alice");
        User bob   = new User("2", "bob");

        bob.follow(alice);
        Tweet tweet = alice.post("Hello Twitter!");

        List<Tweet> bobFeed = bob.getFeed().getTimeline();
        assertEquals(1, bobFeed.size());
        assertEquals("Hello Twitter!", bobFeed.get(0).displayContent());
    }

    @Test
    void retweet_display_content_contains_source_attribution() {
        User alice = new User("1", "alice");
        User bob   = new User("2", "bob");

        Tweet original = alice.post("Great idea!");
        Retweet rt = bob.retweet(original);

        assertTrue(rt.displayContent().contains("@alice"));
        assertTrue(rt.displayContent().contains("Great idea!"));
    }

    @Test
    void reply_display_content_mentions_parent_author() {
        User alice = new User("1", "alice");
        User bob   = new User("2", "bob");

        Tweet original = alice.post("Hello!");
        Reply reply = bob.reply(original, "Hey back!");

        assertTrue(reply.displayContent().startsWith("@alice"));
        assertTrue(reply.displayContent().contains("Hey back!"));
    }

    @Test
    void tweet_builder_rejects_content_over_280_chars() {
        User alice = new User("1", "alice");
        String longContent = "x".repeat(281);
        assertThrows(IllegalArgumentException.class,
                () -> new TweetBuilder(alice, longContent).build());
    }
}
