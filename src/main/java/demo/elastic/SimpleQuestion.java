package demo.elastic;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import static demo.elastic.Constants.GOOGLE_CHAT_MODEL;

public class SimpleQuestion {

    public static void main(String[] args) {
        Client client = new Client();
        GenerateContentResponse response =
            client.models.generateContent(
                GOOGLE_CHAT_MODEL,
                "",
                null);
        System.out.println(response.text().toString());
    }
}
