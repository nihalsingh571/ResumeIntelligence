import java.util.*;

/**
 * SimilarityCalculator.java
 * Module 5 & 6: Resume-Job Matching Algorithm + Skill Gap Analyzer.
 *
 * Upgraded (Feature 5): Added semantic (root-word) partial matching.
 *   - Exact match   → 1.0 point
 *   - Partial match → 0.5 point (first 4 chars of root shared)
 * The blended score uses both exact and semantic contributions.
 */
public class SimilarityCalculator {

    // Root prefix length used for semantic matching
    private static final int ROOT_LEN = 4;

    // -----------------------------------------------------------------------
    // Primary match score (exact + semantic blend)
    // -----------------------------------------------------------------------

    /**
     * Calculates the blended match score between resume skills and job skills.
     * Uses both exact matching and semantic (root-word) partial matching.
     *
     * @param resumeSkills  skills extracted from the resume
     * @param jobSkills     skills required by the job description
     * @return match percentage (0.0 – 100.0)
     */
    public double calculateMatchScore(List<String> resumeSkills, List<String> jobSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) return 0.0;

        Set<String> resumeSet = new HashSet<>(resumeSkills);
        double totalPoints = 0.0;

        for (String jSkill : jobSkills) {
            if (resumeSet.contains(jSkill)) {
                totalPoints += 1.0; // exact match
            } else {
                // Check for semantic partial match
                for (String rSkill : resumeSkills) {
                    if (isSemanticMatch(jSkill, rSkill)) {
                        totalPoints += 0.5;
                        break; // count at most 0.5 per job skill
                    }
                }
            }
        }
        return Math.min((totalPoints / jobSkills.size()) * 100.0, 100.0);
    }

    /**
     * Feature 5: Pure semantic match score (no exact matching).
     * Useful for display purposes — shows how many skills are partial matches.
     *
     * @param resumeSkills skills in resume
     * @param jobSkills    required skills
     * @return percentage of job skills that have a semantic (root-word) match
     */
    public double semanticMatchScore(List<String> resumeSkills, List<String> jobSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) return 0.0;
        Set<String> resumeSet = new HashSet<>(resumeSkills);
        int semanticOnly = 0;
        for (String jSkill : jobSkills) {
            if (!resumeSet.contains(jSkill)) { // only non-exact ones
                for (String rSkill : resumeSkills) {
                    if (isSemanticMatch(jSkill, rSkill)) {
                        semanticOnly++;
                        break;
                    }
                }
            }
        }
        return ((double) semanticOnly / jobSkills.size()) * 100.0;
    }

    // -----------------------------------------------------------------------
    // Skill gap analysis
    // -----------------------------------------------------------------------

    /**
     * Finds skills present in the job description but missing from the resume.
     * A skill is only reported as "missing" if it has neither an exact NOR a semantic match.
     */
    public List<String> findMissingSkills(List<String> resumeSkills, List<String> jobSkills) {
        Set<String> resumeSet = new HashSet<>(resumeSkills);
        List<String> missing  = new ArrayList<>();

        for (String jSkill : jobSkills) {
            if (resumeSet.contains(jSkill)) continue; // exact match — not missing

            // Check semantic match
            boolean semanticFound = false;
            for (String rSkill : resumeSkills) {
                if (isSemanticMatch(jSkill, rSkill)) {
                    semanticFound = true;
                    break;
                }
            }
            if (!semanticFound) missing.add(jSkill);
        }
        return missing;
    }

    /**
     * Finds skills the candidate has that are NOT required by the job.
     */
    public List<String> findExtraSkills(List<String> resumeSkills, List<String> jobSkills) {
        Set<String> jobSet = new HashSet<>(jobSkills);
        List<String> extra = new ArrayList<>();
        for (String skill : resumeSkills) {
            if (!jobSet.contains(skill)) extra.add(skill);
        }
        return extra;
    }

    /**
     * Returns the count of skills exactly matched between resume and job.
     */
    public int countMatched(List<String> resumeSkills, List<String> jobSkills) {
        Set<String> resumeSet = new HashSet<>(resumeSkills);
        int count = 0;
        for (String skill : jobSkills) {
            if (resumeSet.contains(skill)) count++;
        }
        return count;
    }

    // -----------------------------------------------------------------------
    // Semantic matching helper
    // -----------------------------------------------------------------------

    /**
     * Returns true when two skill tokens share at least ROOT_LEN characters
     * of their prefix — e.g., "spring" and "springboot" both start with "spri".
     *
     * @param a first skill token (already lowercase)
     * @param b second skill token (already lowercase)
     */
    private boolean isSemanticMatch(String a, String b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return true; // exact

        // Root-prefix check: extract up to ROOT_LEN chars
        String rootA = a.length() >= ROOT_LEN ? a.substring(0, ROOT_LEN) : a;
        String rootB = b.length() >= ROOT_LEN ? b.substring(0, ROOT_LEN) : b;

        if (!rootA.equals(rootB)) return false;

        // Extra guard: both must be at least 3 chars long to avoid false positives
        return a.length() >= 3 && b.length() >= 3;
    }
}
