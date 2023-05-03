package tn.insat.redditkafka;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;


public class RedditCommentsPoller {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final KafkaProducer<String, String> producer;
    private static final int POLLING_INTERVAL = 1000;
    private int lastTimestamp = 0;

    public RedditCommentsPoller(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
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
                lastTimestamp = maxTimestamp;

                for (RedditComment comment : filteredComments) {
                    producer.send(new ProducerRecord<String, String>("reddit-new-comments", comment.data.body));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, POLLING_INTERVAL, TimeUnit.MILLISECONDS); // Poll every 1 minute, you can adjust this value as needed
    }

    public void stop() {
        scheduler.shutdown();
    }

}
