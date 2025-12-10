package demo.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.google.genai.Client;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmbedData {

    public static void main(String[] args) {

        // Spring AI utility class to read a PDF file page by page
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader("");
        List<Document> docbatch = pdfReader.read();

        // Sending batch of documents to vector store
        // applying tokenizer
        docbatch = new TokenTextSplitter().apply(docbatch);

        // elasticsearch local
        String serverUrl = "http://localhost:9200";
        String APIkey = "";

        // Elasticsearch client + Google ai client
        try (ElasticsearchClient elasticsearchClient =
                 ElasticsearchClient.of(e -> e.host(serverUrl).apiKey(APIkey));
             Client googleClient = new Client()) {

            List<VectorData> docs = new ArrayList<>();

            for (Document document : docbatch) {
                System.out.println("Embedding document: " + document.getMetadata().get("page_number"));
                EmbedContentResponse resp = googleClient.models.embedContent("gemini-embedding-001",
                    document.getText(), EmbedContentConfig.builder().build());

                docs.add(new VectorData(document.getText(),
                    resp.embeddings().orElseThrow(() -> new RuntimeException("couldn't get embeddings"))
                        .getFirst().values().orElseThrow(() -> new RuntimeException("couldn't get embeddings")),
                    document.getMetadata()));
            }

            BulkIngester ingester = BulkIngester.of(b -> b.client(elasticsearchClient)
                // Flush every 50 ms
                .flushInterval(50, TimeUnit.MILLISECONDS)
                // Disable other flushing limits
                .maxSize(-1)
                .maxOperations(-1)
                .maxConcurrentRequests(Integer.MAX_VALUE - 1)
                .globalSettings(g -> g.index("my-index"))
            );

            for (VectorData doc : docs) {
                ingester.add(BulkOperation.of(b -> b
                    .create(c -> c
                        .document(doc)))
                );
            }

            ingester.close();
            elasticsearchClient.indices().refresh();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
