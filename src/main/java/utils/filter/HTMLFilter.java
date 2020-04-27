package utils.filter;

import utils.Constants;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLFilter implements IFilter, Constants {

    private HashMap<String, String> filterRegexMap;

    private Pattern[] patterns;

    public HTMLFilter() {
        this.filterRegexMap = new HashMap<>();
        this.patterns = new Pattern[3];
    }

    public void addRegex(String regex, String replaceText) {
        filterRegexMap.put(regex, replaceText);
    }

    @Override
    public String filter(String text) {
        text = this.applyPatterns(text);
        for(String key: filterRegexMap.keySet()) {
            text = text.replaceAll(key, filterRegexMap.get(key));
        }
        return text.trim();
    }

    private String applyPatterns(String text) {
        for(Pattern pattern: patterns) {
            String newText = text;
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                newText = newText.replace(matcher.group(1), "");
            }
            text = newText;
        }
        return text.trim();
    }

    @Override
    public void construct() {
        this.addRegex(HTML_TAG_REGEX, " ");
        this.addRegex(HTML_NBSP_REGEX, "");
        this.addRegex(POSSESSIVE_REGEX, " ");
        this.addRegex(SPECIAL_CHARACTER_REGEX, " ");
        this.addRegex(NUMBER_REGEX, " ");
        this.addRegex(MULTISPACE_REGEX, " ");

        patterns[0] = Pattern.compile(HTML_SCRIPT_TAG_REGEX, Pattern.MULTILINE | Pattern.UNIX_LINES);
        patterns[1] = Pattern.compile(HTML_STYLE_TAG_REGEX, Pattern.MULTILINE | Pattern.UNIX_LINES);
        patterns[2] = Pattern.compile(HTML_COMMENT_REGEX, Pattern.MULTILINE | Pattern.UNIX_LINES);
    }
}
