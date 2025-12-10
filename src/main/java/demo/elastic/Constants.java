package demo.elastic;

public class Constants {

    public static final String INDEX_NAME = "runewars-rulebook-google";
    public static final String GOOGLE_EMBED_MODEL = "gemini-embedding-001";
    public static final String GOOGLE_CHAT_MODEL = "gemini-2.5-flash";
    public static final String PAGE_METADATA = "page_number";
    public static final String PROMPT = """
                                You're assisting with providing the rules of the tabletop game Runewars.
                                Use the information from the DOCUMENTS section to provide accurate answers to the
                                question in the QUESTION section.
                                If unsure, simply state that you don't know.
                                
                                DOCUMENTS:
                                """ + ""
                            +
                                """
                                QUESTION:
                                """ + "";

}
