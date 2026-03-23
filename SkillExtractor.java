import java.util.*;

/**
 * SkillExtractor.java
 * Module 3: Skill Extraction Engine.
 * Upgraded: Accepts a dynamic skill list loaded from skills.txt via SkillLearner.
 * Falls back to the hardcoded list when no external list is provided.
 */
public class SkillExtractor {

    // -----------------------------------------------------------------------
    // Internal skill database
    // -----------------------------------------------------------------------
    private final List<String> skillDatabase;

    /**
     * Default constructor — uses the built-in hardcoded skill list.
     * Used when running without an external skills.txt.
     */
    public SkillExtractor() {
        skillDatabase = buildDefaultDatabase();
    }

    /**
     * Dynamic constructor — accepts an externally loaded skill list (from SkillLearner).
     * If the provided list is null or empty, falls back to the hardcoded list.
     *
     * @param externalSkills skill list loaded from skills.txt
     */
    public SkillExtractor(List<String> externalSkills) {
        if (externalSkills == null || externalSkills.isEmpty()) {
            skillDatabase = buildDefaultDatabase();
        } else {
            // Use external list but also ensure it has lowercase entries
            skillDatabase = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            for (String s : externalSkills) {
                String lower = s.trim().toLowerCase();
                if (!lower.isEmpty() && seen.add(lower)) {
                    skillDatabase.add(lower);
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Runtime skill management
    // -----------------------------------------------------------------------

    /**
     * Adds a single skill to the in-memory database at runtime.
     * Used by SkillLearner when a new skill is auto-discovered.
     *
     * @param skill skill token (will be lowercased)
     */
    public void addSkill(String skill) {
        String lower = skill.trim().toLowerCase();
        if (!lower.isEmpty() && !skillDatabase.contains(lower)) {
            skillDatabase.add(lower);
        }
    }

    // -----------------------------------------------------------------------
    // Extraction
    // -----------------------------------------------------------------------

    /**
     * Extracts skills found in the given token list by matching against the skill database.
     * @param tokens preprocessed token list from a resume or job description
     * @return list of matched skills (preserves order of first appearance)
     */
    public List<String> extractSkills(List<String> tokens) {
        List<String> detected = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (String token : tokens) {
            if (skillDatabase.contains(token) && !seen.contains(token)) {
                detected.add(token);
                seen.add(token);
            }
        }
        return detected;
    }

    /**
     * Returns the full skill database.
     * @return unmodifiable view of the skill database
     */
    public List<String> getSkillDatabase() {
        return Collections.unmodifiableList(skillDatabase);
    }

    /**
     * Returns the count of skills in the database.
     */
    public int getDatabaseSize() {
        return skillDatabase.size();
    }

    // -----------------------------------------------------------------------
    // Default hardcoded database (fallback)
    // -----------------------------------------------------------------------
    private List<String> buildDefaultDatabase() {
        List<String> db = new ArrayList<>();

        // Programming Languages
        db.add("java");       db.add("python");     db.add("javascript");
        db.add("typescript"); db.add("c");           db.add("cpp");
        db.add("csharp");     db.add("kotlin");      db.add("go");
        db.add("rust");       db.add("php");         db.add("ruby");
        db.add("swift");

        // Web & Frontend
        db.add("react");   db.add("angular"); db.add("vue");
        db.add("html");    db.add("css");     db.add("nodejs");

        // Backend Frameworks
        db.add("spring");     db.add("springboot"); db.add("django");
        db.add("flask");      db.add("express");

        // Databases
        db.add("sql");        db.add("mysql");      db.add("postgresql");
        db.add("mongodb");    db.add("redis");       db.add("cassandra");
        db.add("oracle");

        // Cloud & DevOps
        db.add("aws");        db.add("azure");      db.add("gcp");
        db.add("docker");     db.add("kubernetes"); db.add("terraform");
        db.add("jenkins");    db.add("git");        db.add("github");
        db.add("cicd");       db.add("linux");

        // Architecture & Concepts
        db.add("microservices"); db.add("restapi"); db.add("rest");
        db.add("graphql");       db.add("kafka");   db.add("rabbitmq");
        db.add("api");

        // Data & ML
        db.add("machinelearning"); db.add("deeplearning");
        db.add("tensorflow");      db.add("pandas"); db.add("numpy");

        // Soft skills / practices
        db.add("agile"); db.add("scrum"); db.add("jira");

        return db;
    }
}
