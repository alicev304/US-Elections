package utils;

public interface Constants {
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/81.0.4044.122 Safari/537.36";
    String HTML_SCRIPT_TAG_REGEX = "<script.*?>(.*?)</script>";
    String HTML_STYLE_TAG_REGEX = "<style.*?>(.*?)</style>";
    String HTML_COMMENT_REGEX = "<!--(.*?)-->";
    String HTML_TAG_REGEX = "<[/]?.*?>";
    String HTML_NBSP_REGEX = "nbsp";
    String SPECIAL_CHARACTER_REGEX = "[^a-zA-Z0-9 ]";
    String HYPHEN_REGEX = "-";
    String POSSESSIVE_REGEX = "'s";
    String NUMBER_REGEX = "[0-9]{5,}";
    String MULTISPACE_REGEX = "[ ]+";
    String CORPUS_DIR_PATH = "data/project/";
    String TOKENIZED_CORPUS_DIR_PATH = "output/corpus/";
    int MAX_BLOCK_FILES_COUNT = 3900;
    byte QE_ASSOCIATION = 1;
    byte QE_METRIC = 2;
    byte QE_SCALAR = 3;
    byte QE_ROCCHIO = 4;
    String SUCCESS_STATUS = "success";
    String FAILURE_STATUS = "failure";
    String INVALID_PARAMETERS = "Incorrect number or type or parameters found! " +
            "Kindly check API documentation for usage.";
    byte SIMPLE_COSINE = 1;
    byte SIMPLE_COSINE_W_PR = 2;
    byte SIMPLE_COSINE_W_HITS = 3;
}
