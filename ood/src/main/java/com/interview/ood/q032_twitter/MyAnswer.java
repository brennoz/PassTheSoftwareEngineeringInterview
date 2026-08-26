package com.interview.ood.q032_twitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

// Entry point — all real classes are package-private below
public class MyAnswer {}

// ─── Tweet hierarchy ─────────────────────────────────────────────────────────

abstract class Tweet {

    private final String  id;
    private final User    author;
    private final Instant createdAt;
    private int           likeCount;

    // TODO 1: implement this constructor — set id=UUID, author, createdAt=Instant.now()
    protected Tweet(User author) {
        this.id        = UUID.randomUUID().toString();
        this.author    = author;
        this.createdAt = Instant.now();
    }

    // TODO 2: implement displayContent() in each subclass
    public abstract String displayContent();

    public void like()   { likeCount++; }
    public void unlike() { if (likeCount > 0) likeCount--; }

    public String  getId()        { return id; }
    public User    getAuthor()    { return author; }
    public Instant getCreatedAt() { return createdAt; }
    public int     getLikeCount() { return likeCount; }
}

class OriginalTweet extends Tweet {
    private String content;
    private List<String> hashtags = new ArrayList<>();
    private String mediaUrl = null;
    // TODO 3: fields: content (String), hashtags (List<String>), mediaUrl (String, nullable)
    //         constructor(User author, String content, List<String> hashtags, String mediaUrl)
    //         displayContent() → return content
    //         getHashtags()

    OriginalTweet(User author, String content, List<String> hashtags, String mediaUrl) {
        super(author);
        // TODO 3
        this.content = content;
        this.hashtags = List.copyOf(hashtags);
        this.mediaUrl = mediaUrl;
    }

    @Override public String displayContent() {
        return this.content;
    }
}

class Retweet extends Tweet {
    private Tweet sourceTweet;
    // TODO 4: field: sourceTweet (Tweet)
    //         constructor(User author, Tweet sourceTweet)
    //         displayContent() → "RT @{sourceAuthor.username}: {sourceTweet.displayContent()}"

    Retweet(User author, Tweet sourceTweet) {
        super(author);
        // TODO 4
        this.sourceTweet = sourceTweet;
    }

    @Override public String displayContent() {
        return "RT @" + sourceTweet.getAuthor().getUsername() + ":" + sourceTweet.displayContent();
    }
}

class Reply extends Tweet {
    private String content;
    private Tweet parentTweet;
    // TODO 5: fields: content (String), parentTweet (Tweet)
    //         constructor(User author, String content, Tweet parentTweet)
    //         displayContent() → "@{parentAuthor.username} {content}"

    Reply(User author, String content, Tweet parentTweet) {
        super(author);
        // TODO 5
        this.content = content;
        this.parentTweet = parentTweet;
    }

    @Override public String displayContent() {
        return "@" + parentTweet.getAuthor().getUsername() + " " + this.content;
    }
}

// ─── Builder ─────────────────────────────────────────────────────────────────

class TweetBuilder {

    private User author;
    private String content;
    private List<String> hashtags = new ArrayList<>();
    private String mediaUrl = null;
    private static final int MAX_CONTENT_SIZE = 280;
    
    // TODO 6: required: author (User), content (String)
    //         optional: hashtags (List<String>, default empty), mediaUrl (nullable)
    //         validate in constructor: content not blank, length <= 280
    //         hashtag(String) → add + return this
    //         media(String)   → set mediaUrl + return this
    //         build()         → new OriginalTweet(author, content, hashtags, mediaUrl)

    TweetBuilder(User author, String content) {
        // TODO 6
        this.author = author;
        this.content = content;
        if (content.isBlank()) {
            throw new IllegalArgumentException("content not present");
        }
        if (!content.isEmpty() && content.length() > MAX_CONTENT_SIZE) {
            throw new IllegalArgumentException("max content length = " + MAX_CONTENT_SIZE);
        }
    }

    TweetBuilder hashtag(String tag) {
        /* TODO 6 */ 
        this.hashtags.add(tag);
        return this;
    }
    TweetBuilder media(String url)   { 
        this.mediaUrl = url;
        return this; 
        /* TODO 6 */ 
    }

    OriginalTweet build() {
        return new OriginalTweet(author, content, hashtags, mediaUrl);
    }
}

// ─── Observer ────────────────────────────────────────────────────────────────

interface FeedObserver {
    void onTweetPosted(Tweet tweet);
}

class Feed implements FeedObserver {

    private static final int MAX_FEED_SIZE = 200;

    // TODO 7: Deque<Tweet> timeline = new LinkedList<>()
    //         onTweetPosted: addFirst(tweet); if size > MAX → removeLast()
    //         getTimeline(): return List.copyOf(timeline)

    private final Deque<Tweet> timeline = new LinkedList<>();

    @Override
    public void onTweetPosted(Tweet tweet) {
        // TODO 7
        timeline.addFirst(tweet);
        if (!timeline.isEmpty() && timeline.size() > 200) {
            timeline.removeLast();
        }
    }

    public List<Tweet> getTimeline() {
        return List.copyOf(timeline);
    }
}

// ─── User ─────────────────────────────────────────────────────────────────────

class User {

    private final String             id;
    private final String             username;
    // TODO 8: CopyOnWriteArrayList<FeedObserver> observers
    //         Feed feed
    private final List<FeedObserver> observers = new CopyOnWriteArrayList<>();
    private final Feed               feed      = new Feed();

    User(String id, String username) {
        this.id       = id;
        this.username = username;
    }

    public void follow(User target) {
        // TODO 9: target.addObserver(this.feed)
        target.addObserver(this.feed);
    }

    public void unfollow(User target) {
        // TODO 10: target.removeObserver(this.feed)
        target.removeObserver(feed);
    }

    public Tweet post(String content) {
        // TODO 11: TweetBuilder → build → notifyObservers → return tweet
        //throw new UnsupportedOperationException("TODO 11");
        Tweet tweet = new TweetBuilder(this, content).build();
        notifyObservers(tweet);
        return tweet;
    }

    public Retweet retweet(Tweet source) {
        // TODO 12: new Retweet(this, source) → notifyObservers → return
        //throw new UnsupportedOperationException("TODO 12");
        Retweet tweet = new Retweet(this, source);
        notifyObservers(tweet);
        return tweet;
    }

    public Reply reply(Tweet parent, String content) {
        // TODO 13: new Reply(this, content, parent) → notifyObservers → return
        //throw new UnsupportedOperationException("TODO 13");
        Reply tweet = new Reply(this, content, parent);
        notifyObservers(tweet);
        return tweet;
    }

    void addObserver(FeedObserver o)    { observers.add(o); }
    void removeObserver(FeedObserver o) { observers.remove(o); }

    private void notifyObservers(Tweet tweet) {
        // TODO 14: observers.forEach(o -> o.onTweetPosted(tweet))
        for (FeedObserver observer : observers) {
            observer.onTweetPosted(tweet);
        }
    }

    public Feed   getFeed()     { return feed; }
    public String getId()       { return id; }
    public String getUsername() { return username; }
}
