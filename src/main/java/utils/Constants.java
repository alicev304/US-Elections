package utils;

public interface Constants {
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0";
    String SPECIAL_CHARACTER_REGEX = "[^a-zA-Z0-9 ]";
    String HYPHEN_REGEX = "-";
    String POSSESSIVE_REGEX = "'s";
    String NUMBER_REGEX = "[0-9]{5,}";
    String MULTISPACE_REGEX = "[ ]+";
    String LIST_OF_URLS = "output/links.txt";
    String PAGE_RANKS = "output/pagerank.txt";
    String STOPWORDS = "src/main/resources/stopwords.txt";
    String TOKENIZED_CORPUS_DIR_PATH = "output/corpus/";
    String INDEX_FILE = "output/index_uncompressed";
    String K_CLUSTERING = "output/kmeans.txt";
    String A_CLUSTERING = "output/agglomerative.txt";
    String SUCCESS_STATUS = "success";
    String FAILURE_STATUS = "failure";
    String INVALID_PARAMETERS = "Incorrect number or type or parameters found! " +
            "Kindly check API documentation for usage.";
    byte QE_ASSOCIATION = 1;
    byte QE_METRIC = 2;
    byte QE_SCALAR = 3;
    byte QE_ROCCHIO = 4;
    byte SIMPLE_COSINE = 1;
    byte SIMPLE_COSINE_W_PR = 2;
    byte SIMPLE_COSINE_W_HITS = 3;
    byte K_MEANS = 1;
    byte AGGLOMERATIVE = 2;
}
