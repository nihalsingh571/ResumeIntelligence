import java.util.*;

/**
 * JobAnalyzer.java
 * Module 4: Job Description Analyzer.
 * Upgraded: Added analyzeText(String) for direct GUI text input (no file I/O).
 */
public class JobAnalyzer {

    private final ResumeReader   reader;
    private final TextProcessor  processor;
    private final SkillExtractor extractor;

    // Parsed data from the job description
    private String       rawText;
    private List<String> tokens;
    private List<String> requiredSkills;

    public JobAnalyzer() {
        this.reader    = new ResumeReader();
        this.processor = new TextProcessor();
        this.extractor = new SkillExtractor();
    }

    /**
     * Upgraded constructor: accepts an externally loaded extractor (with dynamic skills).
     */
    public JobAnalyzer(SkillExtractor extractor) {
        this.reader    = new ResumeReader();
        this.processor = new TextProcessor();
        this.extractor = extractor;
    }

    // -----------------------------------------------------------------------
    // Analysis entry points
    // -----------------------------------------------------------------------

    /**
     * Loads and analyzes a job description from a file.
     * @param filePath path to the job description text file
     * @return true if the file was loaded successfully
     */
    public boolean analyze(String filePath) {
        rawText = reader.readFile(filePath);
        if (rawText == null || rawText.isEmpty()) {
            System.out.println("[ERROR] Job description is empty or could not be read.");
            return false;
        }
        processRawText();
        return true;
    }

    /**
     * NEW (Feature 2 / GUI support): Analyzes a job description provided directly
     * as a String (e.g., pasted into the GUI text area). No file I/O required.
     *
     * @param rawJobText raw job description text
     * @return true if the text was non-empty and processed successfully
     */
    public boolean analyzeText(String rawJobText) {
        if (rawJobText == null || rawJobText.trim().isEmpty()) {
            System.out.println("[ERROR] Job description text is empty.");
            return false;
        }
        rawText = rawJobText.trim();
        processRawText();
        return true;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void processRawText() {
        tokens         = processor.preprocess(rawText);
        requiredSkills = extractor.extractSkills(tokens);
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    public String       getRawText()        { return rawText;        }
    public List<String> getTokens()         { return tokens;         }
    public List<String> getRequiredSkills() { return requiredSkills; }

    /**
     * Attempts to extract required years of experience from the job description.
     * Looks for patterns like "2+ years", "3 years experience", etc.
     * Returns 0 if not found.
     */
    public int extractRequiredExperience() {
        if (rawText == null) return 0;
        String[] words = rawText.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String w = words[i].replaceAll("[^0-9]", "");
            if (!w.isEmpty() && words[i + 1].startsWith("year")) {
                try {
                    return Integer.parseInt(w);
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    /**
     * Feature 10: Counts the frequency of each known skill keyword in the JD tokens.
     * Used by the Skill Demand Analyzer panel.
     *
     * @return sorted map (by frequency, descending) of skill → mention count
     */
    public Map<String, Integer> getSkillDemandMap() {
        if (tokens == null) return new LinkedHashMap<>();

        Map<String, Integer> freq = new HashMap<>();
        for (String token : tokens) {
            for (String skill : requiredSkills) {
                if (token.equals(skill)) {
                    freq.put(skill, freq.getOrDefault(skill, 0) + 1);
                }
            }
        }

        // Sort by frequency descending
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());

        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : entries) {
            sorted.put(e.getKey(), e.getValue());
        }
        return sorted;
    }
}
