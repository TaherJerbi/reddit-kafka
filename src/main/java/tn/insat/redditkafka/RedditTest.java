package tn.insat.redditkafka;

import java.io.IOException;
import java.util.List;

public class RedditTest {
    public static void main(String[] args) throws IOException {
        RedditCommentsReponse response = RedditCommentsAPI.getComments(1);

        System.out.println(response);

        System.out.println(response.data.modhash);

        List<RedditComment> comments = response.data.children;
        System.out.println(comments.toString());
        for (RedditComment comment : comments) {
            System.out.println("----");
            System.out.println(comment.data.created);
            System.out.println(comment.data.author);
            System.out.println(comment.data.body);
            System.out.println("----");
        }
    }
}
