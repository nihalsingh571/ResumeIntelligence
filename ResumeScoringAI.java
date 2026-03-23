import java.util.*;

/**
 * ResumeScoringAI.java
 * Module 7: Mini AI Resume Scoring Model (rule-based, implemented in Core Java).
 *
 * Upgraded Scoring Formula (Feature 6 — 5 factors):
 *   resumeScore =
 *     0.40 * skillMatchScore   (how well skills match JD)
 *   + 0.20 * experienceScore   (years of relevant experience)
 *   + 0.15 * projectScore      (number of projects)
 *   + 0.15 * keywordDensity    (JD keyword density in resume)
 *   + 0.10 * skillDiversity    (breadth of different skills)
 *
 * All sub-scores are normalised to 0–100 before weighting.
 */
public class ResumeScoringAI {

    // ------------------------------------------------------------
    // Scoring weights — must sum to 1.0
    // ------------------------------------------------------------
    private static final double WEIGHT_SKILL_MATCH  = 0.40;
    private static final double WEIGHT_EXPERIENCE   = 0.20;
    private static final double WEIGHT_PROJECTS     = 0.15;
    private static final double WEIGHT_KEYWORD_DENS = 0.15;
    private static final double WEIGHT_DIVERSITY    = 0.10;

    // Normalisation ceilings
    private static final int    MAX_EXPERIENCE_YEARS = 5;   // ≥5 yrs → 100
    private static final int    MAX_PROJECTS         = 4;   // ≥4 projects → 100
    private static final double MAX_KEYWORD_DENSITY  = 0.05; // ≥5% density → 100
    private static final int    MAX_DIVERSITY_SKILLS = 15;  // ≥15 unique skills → 100

    // -----------------------------------------------------------------------
    // Individual sub-score calculators
    // -----------------------------------------------------------------------

    private double calcSkillMatchScore(double matchPercent) {
        return Math.min(matchPercent, 100.0);
    }

    private double calcExperienceScore(int yearsOfExperience) {
        return Math.min(((double) yearsOfExperience / MAX_EXPERIENCE_YEARS) * 100.0, 100.0);
    }

    private double calcProjectScore(int projectCount) {
        return Math.min(((double) projectCount / MAX_PROJECTS) * 100.0, 100.0);
    }

    private double calcKeywordScore(double density) {
        return Math.min((density / MAX_KEYWORD_DENSITY) * 100.0, 100.0);
    }

    /**
     * Feature 6: Skill diversity sub-score.
     * Rewards candidates who demonstrate breadth across multiple technology areas.
     *
     * @param detectedSkillCount number of distinct skills found in the resume
     * @return 0–100 diversity score
     */
    private double calcDiversityScore(int detectedSkillCount) {
        return Math.min(((double) detectedSkillCount / MAX_DIVERSITY_SKILLS) * 100.0, 100.0);
    }

    // -----------------------------------------------------------------------
    // Overloaded computeScore — backwards-compatible versions
    // -----------------------------------------------------------------------

    /**
     * Original 4-factor signature (kept for ResumeRanker / console path compatibility).
     * Diversity score is estimated as 0 (neutral impact).
     */
    public int computeScore(double matchPercent, int yearsExp,
                            int projectCount, double keywordDensity) {
        return computeScore(matchPercent, yearsExp, projectCount, keywordDensity, 0);
    }

    /**
     * Full 5-factor scoring (Feature 6).
     *
     * @param matchPercent    skill match percentage (Module 5 output)
     * @param yearsExp        years of experience parsed from resume
     * @param projectCount    number of projects found in resume
     * @param keywordDensity  keyword density from TextProcessor
     * @param detectedSkills  number of distinct skills detected (for diversity)
     * @return final composite score (integer, 0–100)
     */
    public int computeScore(double matchPercent, int yearsExp,
                            int projectCount, double keywordDensity,
                            int detectedSkills) {

        double total =
            WEIGHT_SKILL_MATCH  * calcSkillMatchScore(matchPercent)
          + WEIGHT_EXPERIENCE   * calcExperienceScore(yearsExp)
          + WEIGHT_PROJECTS     * calcProjectScore(projectCount)
          + WEIGHT_KEYWORD_DENS * calcKeywordScore(keywordDensity)
          + WEIGHT_DIVERSITY    * calcDiversityScore(detectedSkills);

        return (int) Math.round(total);
    }

    // -----------------------------------------------------------------------
    // Score breakdown (for display)
    // -----------------------------------------------------------------------

    /** 4-factor breakdown (legacy). */
    public String getScoreBreakdown(double matchPercent, int yearsExp,
                                    int projectCount, double keywordDensity) {
        return getScoreBreakdown(matchPercent, yearsExp, projectCount, keywordDensity, 0);
    }

    /** Full 5-factor breakdown. */
    public String getScoreBreakdown(double matchPercent, int yearsExp,
                                    int projectCount, double keywordDensity,
                                    int detectedSkills) {
        return String.format(
            "  Skill Match   : %.1f / 100  (weight 40%%)\n"
          + "  Experience    : %.1f / 100  (weight 20%%)\n"
          + "  Projects      : %.1f / 100  (weight 15%%)\n"
          + "  Keyword Dens  : %.1f / 100  (weight 15%%)\n"
          + "  Skill Diver.  : %.1f / 100  (weight 10%%)",
            calcSkillMatchScore(matchPercent),
            calcExperienceScore(yearsExp),
            calcProjectScore(projectCount),
            calcKeywordScore(keywordDensity),
            calcDiversityScore(detectedSkills));
    }

    // -----------------------------------------------------------------------
    // Resume metadata extraction helpers (unchanged from v1)
    // -----------------------------------------------------------------------

    /**
     * Extracts approximate years of experience from raw resume text.
     * Looks for patterns such as "3 years", "2+ years".
     */
    public int extractExperienceYears(String rawResumeText) {
        if (rawResumeText == null) return 0;
        String[] words = rawResumeText.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String clean = words[i].replaceAll("[^0-9]", "");
            if (!clean.isEmpty() && words[i + 1].startsWith("year")) {
                try {
                    return Integer.parseInt(clean);
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    /**
     * Estimates project count by scanning for project section bullet points.
     */
    public int extractProjectCount(String rawResumeText) {
        if (rawResumeText == null) return 0;
        String[] lines = rawResumeText.split("\n");
        boolean inProjects = false;
        int count = 0;
        for (String line : lines) {
            String trimmed = line.trim().toLowerCase();
            if (trimmed.startsWith("project")) {
                inProjects = true;
                continue;
            }
            if (inProjects && !trimmed.isEmpty() && !trimmed.startsWith("-")
                    && trimmed.contains(":") && !trimmed.startsWith("- ")) {
                inProjects = false;
            }
            if (inProjects && trimmed.startsWith("-")) {
                count++;
            }
        }
        return Math.max(count, 0);
    }
}
