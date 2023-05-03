package tn.insat.redditkafka;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;

public class RedditCommentsPoller {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Producer<String, String> producer;
    private int POLLING_INTERVAL = 1000;
    private long lastTimestamp = Instant.now().toEpochMilli();
    private String topicName = "reddit-new-comments";


    public RedditCommentsPoller() {
        this.producer = null;
    }

    public RedditCommentsPoller(Producer<String, String> producer) {
        this.producer = producer;
    }

    public RedditCommentsPoller(Producer<String, String> producer, int pollingInterval) {
        this.producer = producer;
        this.POLLING_INTERVAL = pollingInterval;
    }

    public RedditCommentsPoller(Producer<String, String> producer, int pollingInterval, long lastTimestamp) {
        this.producer = producer;
        this.POLLING_INTERVAL = pollingInterval;
        this.lastTimestamp = lastTimestamp;
    }

    public RedditCommentsPoller(Producer<String, String> producer, int pollingInterval, long lastTimestamp, String topicName) {
        this.producer = producer;
        this.POLLING_INTERVAL = pollingInterval;
        this.lastTimestamp = lastTimestamp;
        this.topicName = topicName;
    }

    private void poll() {
        System.out.println("Polling...");
        try {
            RedditCommentsReponse commentsResponse = RedditCommentsAPI.getComments(10); // or any other limit you prefer
            
            List<RedditComment> filteredComments = new ArrayList<RedditComment>();
            int maxTimestamp = 0;
            for (RedditComment comment : commentsResponse.data.children) {
                if (comment.data.created > lastTimestamp) {
                    filteredComments.add(comment);
                }
                if (comment.data.created > maxTimestamp) {
                    maxTimestamp = (int) Math.floor(comment.data.created);
                }
            }
            System.out.println("------------------------------------------------------------");
            System.out.println("Got " + filteredComments.size() + " new comments");
            lastTimestamp = maxTimestamp;
            System.out.println("Last timestamp: " + lastTimestamp);

            if (producer == null)
                System.out.println("No producer, not sending to Kafka");

            for (RedditComment comment : filteredComments) {
                // comment_id, comment_parent_id, comment_body and subreddit

                String line = comment.data.id + "," + comment.data.parent_id + ",\"" + comment.data.body + "\"," + comment.data.subreddit + "," + comment.data.created;

                if (producer != null) {
                    producer.send(new ProducerRecord<String, String>(topicName, line));
                    System.out.println("Sent to Kafka ✅");
                }
                System.out.println("> " + line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(int maxTime) {
        // poll for new comments every minute
        // until maxTime ms have passed
        // if maxTime is 0, then poll indefinitely
        final Runnable poller = new Runnable() {
            public void run() {
                poll();
            }
        };

        scheduler.scheduleAtFixedRate(poller, 0, POLLING_INTERVAL, TimeUnit.MILLISECONDS);

        if (maxTime == 0) {
            return;
        }
        scheduler.schedule(new Runnable() {
            public void run() {
                scheduler.shutdown();
            }
        }, maxTime, TimeUnit.MILLISECONDS);
    }

    public void start() {
        start(0);
    }

    public void stop() {
        scheduler.shutdown();
    }

}
