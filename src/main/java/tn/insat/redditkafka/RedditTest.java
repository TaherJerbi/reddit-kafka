package tn.insat.redditkafka;

import org.apache.hadoop.util.Time;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class RedditTest {
    public static void main(String[] args) throws IOException {
//        Verifier que le topic est donne en argument
//        if (args.length == 0) {
//            System.out.println("Entrer le nom du topic");
//            return;
//        }
//
//        // Assigner topicName a une variable
//        String topicName = args[0].toString();
//
//        // Creer une instance de proprietes pour acceder aux configurations du producteur
//        Properties props = new Properties();
//
//        // Assigner l'identifiant du serveur kafka
//        props.put("bootstrap.servers", "localhost:9092");
//
//        // Definir un acquittement pour les requetes du producteur
//        props.put("acks", "all");
//
//        // Si la requete echoue, le producteur peut reessayer automatiquemt
//        props.put("retries", 0);
//
//        // Specifier la taille du buffer size dans la config
//        props.put("batch.size", 16384);
//
//        // buffer.memory controle le montant total de memoire disponible au producteur pour le buffering
//        props.put("buffer.memory", 33554432);
//
//        props.put("key.serializer",
//                "org.apache.kafka.common.serialization.StringSerializer");
//
//        props.put("value.serializer",
//                "org.apache.kafka.common.serialization.StringSerializer");
//
//        Producer<String, String> producer = new KafkaProducer
//                <String, String>(props);

        RedditCommentsPoller poller = new RedditCommentsPoller(null, 3000);

        poller.start(10000);

    }
}
