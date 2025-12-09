package co.elastic;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class SimpleQuestion {

    public static void main(String[] args) {
        Client client = new Client();
        GenerateContentResponse response =
            client.models.generateContent(
                "gemini-2.5-flash",
                "",
                null);
        System.out.println(response.toString());
    }
}
