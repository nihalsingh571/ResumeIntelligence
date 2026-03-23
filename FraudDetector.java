import java.util.*;

/**
 * FraudDetector.java
 * Feature 7: Resume Fraud / Exaggeration Detection.
 *
 * Applies a set of rule-based heuristics to flag resumes that appear
 * to over-claim skills without matching practical evidence.
 *
 * All rules return warning strings that the GUI can display in red.
 */
public class FraudDetector {

    // -----------------------------------------------------------------------
    // Detection thresholds
    // -----------------------------------------------------------------------

    /** If skillCount > projectCount * SKILL_PROJECT_RATIO → exaggeration warning. */
    private static final int SKILL_PROJECT_RATIO = 3;

    /** Minimum project count for a resume with many skills. */
    private static final int MIN_PROJECTS_FOR_HIGH_SKILLS = 1;

    /** Skill count considered "very high" for a fresher with 0 years experience. */
    private static final int HIGH_SKILL_THRESHOLD = 15;

    /** Match score below this (%) is suspiciously low despite high skill count. */
    private static final double LOW_MATCH_THRESHOLD = 30.0;

    // -----------------------------------------------------------------------
    // Main API
    // -----------------------------------------------------------------------

    /**
     * Runs all fraud-detection rules against the given resume data.
     *
     * @param skillCount    number of skills detected in the resume
     * @param projectCount  number of projects detected in the resume
     * @param yearsExp      years of experience detected in the resume
     * @param matchPercent  job-match score (0–100)
     * @return list of warning messages (empty = no issues detected)
     */
    public List<String> detect(int skillCount, int projectCount,
                                int yearsExp, double matchPercent) {
        List<String> warnings = new ArrayList<>();

        // Rule 1: Many skills, very few projects
        if (skillCount > projectCount * SKILL_PROJECT_RATIO
                && projectCount <= MIN_PROJECTS_FOR_HIGH_SKILLS) {
            warnings.add("⚠ FRAUD ALERT: Resume lists " + skillCount
                + " skills but only " + projectCount
                + " project(s). This may indicate an exaggerated resume.");
        }

        // Rule 2: Zero experience but extremely high skill count
        if (yearsExp == 0 && skillCount > HIGH_SKILL_THRESHOLD) {
            warnings.add("⚠ FRAUD ALERT: No experience detected but "
                + skillCount + " skills listed. "
                + "Consider verifying actual proficiency levels.");
        }

        // Rule 3: High skill count but very low job match (skill inflation)
        if (skillCount >= 10 && matchPercent < LOW_MATCH_THRESHOLD) {
            warnings.add("⚠ FRAUD ALERT: Candidate lists many skills ("
                + skillCount + ") but only matches " 
                + String.format("%.1f", matchPercent)
                + "% of job requirements. Skills may not be relevant.");
        }

        // Rule 4: More projects than experience plausibly allows
        if (yearsExp == 0 && projectCount > 6) {
            warnings.add("⚠ NOTE: " + projectCount
                + " projects listed with 0 years experience. "
                + "Ensure projects are accurately described.");
        }

        return warnings;
    }

    /**
     * Convenience method: returns true if any fraud warnings were generated.
     */
    public boolean isSuspicious(int skillCount, int projectCount,
                                 int yearsExp, double matchPercent) {
        return !detect(skillCount, projectCount, yearsExp, matchPercent).isEmpty();
    }
}
