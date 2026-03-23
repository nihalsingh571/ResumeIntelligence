import java.io.*;
import java.util.*;

/**
 * SkillLearner.java
 * Feature 3 & 4: Dynamic Skill Database + Self-Learning Skill Detector.
 *
 * Responsibilities:
 *  1. Load the skill database from skills.txt at startup.
 *  2. Track token frequencies across analyzed resumes / JDs.
 *  3. If an unknown token appears >= LEARN_THRESHOLD times → it is added to
 *     skills.txt and reported as a "newly learned skill".
 *
 * Usage:
 *   SkillLearner learner = new SkillLearner("skills.txt");
 *   List<String> skills  = learner.loadSkills();
 *   learner.trackTokens(tokenList);          // call after each analysis
 *   List<String> newOnes = learner.learnAndUpdate(); // returns newly learned skills
 */
public class SkillLearner {

    // -----------------------------------------------------------------
    // Configuration
    // -----------------------------------------------------------------
    /** Number of times an unknown token must appear before it is learned. */
    private static final int LEARN_THRESHOLD = 3;

    /**
     * Known stop-words / noise tokens that should never be added as skills
     * even if they are frequent. Expand as needed.
     */
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "and", "for", "with", "have", "has", "had", "are", "was",
        "were", "you", "your", "our", "this", "that", "from", "will",
        "can", "its", "not", "but", "use", "used", "also", "both",
        "all", "any", "new", "more", "we", "my", "be", "by",
        "of", "to", "in", "is", "it", "on", "or", "an", "as", "at",
        "do", "if", "no", "so", "up", "me", "he", "she",
        "experience", "knowledge", "skills", "work", "team", "year",
        "years", "strong", "good", "ability", "must", "required",
        "candidate", "role", "job", "company", "using", "system",
        "based", "please", "apply", "resume", "cv", "name", "email",
        "phone", "address", "degree", "bachelor", "master", "university",
        "college", "project", "projects", "responsible", "responsibilities"
    ));

    // -----------------------------------------------------------------
    // State
    // -----------------------------------------------------------------
    private final String skillFilePath;

    /** Current known skills (loaded from file). */
    private final List<String> knownSkills = new ArrayList<>();

    /**
     * Frequency map for tokens that are NOT yet in the known skill list.
     * Key = token (lowercase), Value = occurrence count across all analyses.
     */
    private final Map<String, Integer> unknownFrequency = new HashMap<>();

    /** Skills learned and persisted in this session (for UI notification). */
    private final List<String> sessionLearnedSkills = new ArrayList<>();

    // -----------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------

    /**
     * Creates a new SkillLearner backed by the given file path.
     * Does NOT load skills automatically — call {@link #loadSkills()} explicitly.
     *
     * @param skillFilePath path to the skills.txt file
     */
    public SkillLearner(String skillFilePath) {
        this.skillFilePath = skillFilePath;
    }

    // -----------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------

    /**
     * Loads the skill database from the backing file.
     * Each non-empty, non-comment line is treated as one skill (lowercase).
     *
     * @return mutable list of all loaded skills
     */
    public List<String> loadSkills() {
        knownSkills.clear();
        File f = new File(skillFilePath);

        if (!f.exists()) {
            System.out.println("[SkillLearner] skills.txt not found. Starting with empty database.");
            return new ArrayList<>();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String skill = line.trim().toLowerCase();
                if (!skill.isEmpty() && !skill.startsWith("#")) {
                    knownSkills.add(skill);
                }
            }
        } catch (IOException e) {
            System.out.println("[SkillLearner] Error reading skills.txt: " + e.getMessage());
        }

        System.out.println("[SkillLearner] Loaded " + knownSkills.size() + " skills from " + skillFilePath);
        return new ArrayList<>(knownSkills);
    }

    /**
     * Processes a list of tokens (from a resume or JD) and updates the
     * frequency counter for tokens that are NOT already known skills.
     *
     * @param tokens preprocessed token list
     */
    public void trackTokens(List<String> tokens) {
        Set<String> knownSet = new HashSet<>(knownSkills);

        for (String token : tokens) {
            String t = token.toLowerCase().trim();
            // Skip very short tokens, digits-only, and stop words
            if (t.length() < 4) continue;
            if (t.matches("\\d+")) continue;
            if (STOP_WORDS.contains(t)) continue;
            if (knownSet.contains(t)) continue;

            // Count this unknown token
            unknownFrequency.put(t, unknownFrequency.getOrDefault(t, 0) + 1);
        }
    }

    /**
     * Evaluates the frequency map. Any token that has been seen
     * >= LEARN_THRESHOLD times is added to:
     *   - the in-memory skill list
     *   - the skills.txt file
     *   - the session learned-skills list
     *
     * @return list of newly discovered/learned skills (empty if none)
     */
    public List<String> learnAndUpdate() {
        List<String> newlyLearned = new ArrayList<>();
        Set<String> knownSet = new HashSet<>(knownSkills);

        for (Map.Entry<String, Integer> entry : unknownFrequency.entrySet()) {
            String token = entry.getKey();
            int freq     = entry.getValue();

            if (freq >= LEARN_THRESHOLD && !knownSet.contains(token)) {
                newlyLearned.add(token);
                knownSkills.add(token);
                knownSet.add(token);
                sessionLearnedSkills.add(token);
            }
        }

        // Persist to file
        if (!newlyLearned.isEmpty()) {
            persistNewSkills(newlyLearned);
            System.out.println("[SkillLearner] Learned " + newlyLearned.size()
                               + " new skill(s): " + newlyLearned);
        }

        // Remove newly learned tokens from the unknown map so they are not re-processed
        for (String s : newlyLearned) {
            unknownFrequency.remove(s);
        }

        return newlyLearned;
    }

    /**
     * Adds a single skill to the in-memory list AND to skills.txt immediately.
     * Useful for manual additions via the GUI.
     *
     * @param skill skill to add (will be lowercased)
     */
    public void addSkillManually(String skill) {
        String s = skill.trim().toLowerCase();
        if (!s.isEmpty() && !knownSkills.contains(s)) {
            knownSkills.add(s);
            persistNewSkills(Collections.singletonList(s));
        }
    }

    // -----------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------

    /** Returns a copy of the current in-memory skill list. */
    public List<String> getKnownSkills() {
        return new ArrayList<>(knownSkills);
    }

    /** Returns all skills learned automatically in this session. */
    public List<String> getSessionLearnedSkills() {
        return new ArrayList<>(sessionLearnedSkills);
    }

    /** Returns the unknown-token frequency map (for diagnostic display). */
    public Map<String, Integer> getUnknownFrequency() {
        return Collections.unmodifiableMap(unknownFrequency);
    }

    // -----------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------

    /**
     * Appends newly discovered skills to the end of skills.txt.
     */
    private void persistNewSkills(List<String> skills) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(skillFilePath, true))) {
            for (String s : skills) {
                pw.println(s);
            }
        } catch (IOException e) {
            System.out.println("[SkillLearner] Error writing to skills.txt: " + e.getMessage());
        }
    }
}
