package demo.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.google.genai.Client;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import com.google.genai.types.GenerateContentResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Query {
    public static void main(String[] args) {

        // elasticsearch local
        String serverUrl = "http://localhost:9200";
        String APIkey = "";

        // Elasticsearch client + Google ai client
        try (ElasticsearchClient elasticsearchClient =
                 ElasticsearchClient.of(e -> e.host(serverUrl).apiKey(APIkey));
             Client googleClient = new Client()) {

            // embedding the query
            EmbedContentResponse resp = googleClient.models.embedContent("gemini-embedding-001", "question here",
                EmbedContentConfig.builder().build());

            List<Float> queryEmbedding = resp.embeddings()
                .orElseThrow(() -> new RuntimeException("couldn't get embeddings"))
                .getFirst().values()
                .orElseThrow(() -> new RuntimeException("couldn't get embeddings"));


            // knn semantic query
            SearchResponse<VectorData> esResult = elasticsearchClient
                .search(s -> s
                        .index("my-index")
                        .query(q -> q
                            .knn(k -> k
                                .queryVector(queryEmbedding)
                                .field("vector")
                                .k(3)
                                .similarity(0.6F)
                            )
                        )
                    , VectorData.class);

            // construct prompt (query + context)

            // Merging the documents into a single string
            String documents = esResult.hits().hits().stream()
                .map(Hit::source)
                .map(VectorData::text)
                .collect(Collectors.joining(System.lineSeparator()));

            String prompt = """
                                PROMPT SETUP 
                                
                                DOCUMENTS:
                                """ + documents
                            + """
                                QUESTION:
                                """ + "question here";

            GenerateContentResponse response =
                googleClient.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null);
            System.out.println(response.text().toString());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
