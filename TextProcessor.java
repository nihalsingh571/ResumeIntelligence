import java.util.*;

/**
 * TextProcessor.java
 * Module 2: NLP Text Preprocessing Engine (implemented manually in Core Java).
 * Steps: lowercase → remove punctuation/special chars → tokenize.
 */
public class TextProcessor {

    /**
     * Full preprocessing pipeline.
     * Converts text to lowercase, strips non-alphabetic characters, tokenizes.
     * @param rawText the raw input string
     * @return list of cleaned word tokens
     */
    public List<String> preprocess(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return new ArrayList<>();
        }

        // Step 1: Convert to lowercase
        String lower = rawText.toLowerCase();

        // Step 2: Remove punctuation and special characters (keep letters, digits, spaces)
        String cleaned = lower.replaceAll("[^a-z0-9\\s]", " ");

        // Step 3: Collapse multiple spaces
        String normalized = cleaned.replaceAll("\\s+", " ").trim();

        // Step 4: Tokenize by splitting on whitespace
        String[] parts = normalized.split("\\s+");

        List<String> tokens = new ArrayList<>();
        for (String token : parts) {
            if (!token.isEmpty() && token.length() > 1) { // filter single-char noise
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     * Joins a token list back into a readable space-separated string.
     * Useful for display and debugging.
     * @param tokens list of tokens
     * @return single string of space-separated tokens
     */
    public String tokensToString(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (String t : tokens) {
            sb.append(t).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Calculates keyword density: how many times target keywords
     * appear as a proportion of total token count.
     * @param tokens  preprocessed token list from resume
     * @param keywords set of keywords to look for
     * @return density as a value between 0.0 and 1.0
     */
    public double calculateKeywordDensity(List<String> tokens, Set<String> keywords) {
        if (tokens == null || tokens.isEmpty()) return 0.0;
        int count = 0;
        for (String token : tokens) {
            if (keywords.contains(token)) {
                count++;
            }
        }
        return (double) count / tokens.size();
    }
}
