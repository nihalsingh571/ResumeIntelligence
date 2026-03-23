import java.util.*;

/**
 * ResumeRanker.java
 * Feature 8: Resume Ranking System.
 *
 * Upgraded: clear() method added; getRankedCandidates() returns a fresh sort
 * each call so the GUI can redisplay after adding more candidates.
 */
public class ResumeRanker {

    // -----------------------------------------------------------------------
    // Inner class: holds all data for one candidate
    // -----------------------------------------------------------------------
    public static class CandidateResult implements Comparable<CandidateResult> {
        private final String       name;
        private final int          aiScore;
        private final double       matchPercent;
        private final List<String> detectedSkills;
        private final List<String> missingSkills;

        public CandidateResult(String name, int aiScore, double matchPercent,
                               List<String> detectedSkills, List<String> missingSkills) {
            this.name           = name;
            this.aiScore        = aiScore;
            this.matchPercent   = matchPercent;
            this.detectedSkills = detectedSkills;
            this.missingSkills  = missingSkills;
        }

        /** Higher score = better rank (descending sort). */
        @Override
        public int compareTo(CandidateResult other) {
            return Integer.compare(other.aiScore, this.aiScore);
        }

        // ---- Getters ----
        public String       getName()           { return name;           }
        public int          getAiScore()        { return aiScore;        }
        public double       getMatchPercent()   { return matchPercent;   }
        public List<String> getDetectedSkills() { return detectedSkills; }
        public List<String> getMissingSkills()  { return missingSkills;  }
    }

    // -----------------------------------------------------------------------
    // Ranker logic
    // -----------------------------------------------------------------------
    private final List<CandidateResult> candidates = new ArrayList<>();

    /** Adds a candidate's result to the ranking pool. */
    public void addCandidate(CandidateResult result) {
        candidates.add(result);
    }

    /**
     * Sorts candidates in descending order of AI score.
     * @return a new sorted list (best candidate first)
     */
    public List<CandidateResult> getRankedCandidates() {
        List<CandidateResult> sorted = new ArrayList<>(candidates);
        Collections.sort(sorted);   // uses Comparable.compareTo
        return sorted;
    }

    /** Prints a formatted ranking table to the console (legacy / debugging). */
    public void printRanking() {
        List<CandidateResult> ranked = getRankedCandidates();
        System.out.println("\n======================================================");
        System.out.println("           RESUME RANKING LEADERBOARD                ");
        System.out.println("======================================================");
        System.out.printf("%-5s %-20s %-12s %-10s%n",
                          "Rank", "Candidate", "AI Score", "Job Match");
        System.out.println("------------------------------------------------------");
        int rank = 1;
        for (CandidateResult c : ranked) {
            System.out.printf("%-5d %-20s %-12s %-10s%n",
                              rank++,
                              c.getName(),
                              c.getAiScore() + "/100",
                              String.format("%.1f%%", c.getMatchPercent()));
        }
        System.out.println("======================================================");
    }

    /** Returns the raw (unsorted) list — used by the GUI for live updates. */
    public List<CandidateResult> getAllCandidates() {
        return Collections.unmodifiableList(candidates);
    }

    /** Clears all candidates (useful for re-use across sessions). */
    public void clear() {
        candidates.clear();
    }

    /** Returns how many candidates have been added. */
    public int size() {
        return candidates.size();
    }
}
