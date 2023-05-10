package tn.insat.redditkafka;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.time.Instant;
import java.util.Properties;

public class RedditTest {
    static Producer<String, String> producer = null;
    static String topicName = null;
    static int POLLING_INTERVAL = 1000;
    static long lastTimestamp = Instant.now().toEpochMilli();
    static boolean withKafka = false;
    static int maxTime = 0;

    static void parseArgs(String[] args) {

        // Verifier que le topic est donne en argument
        // args: [topicName] [pollingInterval] [lastTimestamp] [maxTime] [--with-kafka]
        if (args.length == 0) {
            System.out.println("Usage: RedditTest [topicName] [pollingInterval] [lastTimestamp / now] [maxTime] [--with-kafka]");
            System.out.println("Running with default values (topicName: 'reddit-new-comments', pollingInterval: 1000ms, lastTimestamp: Time.now())");
            return;
        }

        topicName = args[0];

        if (args.length > 1) {
            try {
                POLLING_INTERVAL = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid polling interval, using default (1000ms)");
            }
        }

        if (args.length > 2) {
            try {
                if (args[2].equals("now")) {
                    lastTimestamp = Instant.now().toEpochMilli();
                } else {
                    lastTimestamp = Long.parseLong(args[2]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid last timestamp, using default (Time.now())");
            }
        }

        if (args.length > 3) {
            try {
                maxTime = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid max time, using default (0)");
            }
        }

        if (args.length > 4 && args[4].equals("--with-kafka")) {
            withKafka = true;
        }
    }

    static Producer makeProducer() {
        // Creer une instance de proprietes pour acceder aux configurations du producteur
        Properties props = new Properties();

        // Assigner l'identifiant du serveur kafka
        props.put("bootstrap.servers", "kafka:9092");

        // Definir un acquittement pour les requetes du producteur
        props.put("acks", "all");

        // Si la requete echoue, le producteur peut reessayer automatiquemt
        props.put("retries", 0);

        // Specifier la taille du buffer size dans la config
        props.put("batch.size", 16384);

        // buffer.memory controle le montant total de memoire disponible au producteur pour le buffering
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");


        return new KafkaProducer<String, String>(props);
    }

    public static void main(String[] args) {

        parseArgs(args);
        System.out.println("Running with topicName: " + topicName + ", pollingInterval: " + POLLING_INTERVAL + ", lastTimestamp: " + lastTimestamp + ", maxTime: " + maxTime + ", withKafka: " + withKafka);

        if (withKafka) {
            producer = makeProducer();
        }

        RedditCommentsPoller poller = new RedditCommentsPoller(producer, POLLING_INTERVAL, lastTimestamp, topicName);

        poller.start(maxTime);

    }
}
