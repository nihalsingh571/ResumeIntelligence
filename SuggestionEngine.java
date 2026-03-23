import java.util.*;

/**
 * SuggestionEngine.java
 * Upgrade 3: Resume Keyword Optimizer + Suggestion Engine.
 *
 * Analyses missing skills, experience gaps, project count, and keyword
 * density to provide actionable improvement suggestions.
 */
public class SuggestionEngine {

    /**
     * Generates a list of human-readable improvement suggestions based on
     * the resume analysis results.
     *
     * @param missingSkills   skills in the JD but absent from the resume
     * @param matchPercent    overall skill match percentage
     * @param yearsExp        years of experience extracted from resume
     * @param projectCount    number of projects counted in resume
     * @param keywordDensity  keyword density from TextProcessor
     * @param aiScore         composite AI score
     * @return ordered list of suggestion strings
     */
    public List<String> generateSuggestions(List<String> missingSkills,
                                             double matchPercent,
                                             int yearsExp,
                                             int projectCount,
                                             double keywordDensity,
                                             int aiScore) {
        List<String> suggestions = new ArrayList<>();

        // ---- 1. Missing skill suggestions ----
        if (!missingSkills.isEmpty()) {
            suggestions.add("Add the following missing skills to your resume: "
                            + String.join(", ", missingSkills).toUpperCase() + ".");
            for (String skill : missingSkills) {
                suggestions.add("Consider taking an online course or certification in '"
                                + skill.toUpperCase() + "' to close the skill gap.");
            }
        }

        // ---- 2. Match score suggestions ----
        if (matchPercent < 40) {
            suggestions.add("Your skill match with this job is LOW (" +
                            String.format("%.1f", matchPercent) +
                            "%). Significantly tailor your resume to match the job requirements.");
        } else if (matchPercent < 70) {
            suggestions.add("Your skill match is MODERATE (" +
                            String.format("%.1f", matchPercent) +
                            "%). Add more relevant skills and keywords from the job description.");
        } else {
            suggestions.add("Great skill match (" +
                            String.format("%.1f", matchPercent) +
                            "%)! Highlight these skills prominently in your resume.");
        }

        // ---- 3. Experience suggestions ----
        if (yearsExp == 0) {
            suggestions.add("No experience duration detected. Explicitly state your years of experience "
                            + "(e.g., '2 years of experience in Java development').");
        } else if (yearsExp < 2) {
            suggestions.add("Your experience is under 2 years. Strengthen your profile with more "
                            + "internships, personal projects, or open-source contributions.");
        }

        // ---- 4. Project suggestions ----
        if (projectCount == 0) {
            suggestions.add("No projects detected in your resume. Add at least 2–3 relevant projects "
                            + "with descriptions, technologies used, and your specific contributions.");
        } else if (projectCount < 2) {
            suggestions.add("You have fewer than 2 projects listed. Recruiters value practical experience — "
                            + "consider adding more personal or academic projects.");
        }

        // ---- 5. Keyword density suggestions ----
        if (keywordDensity < 0.02) {
            suggestions.add("Your resume has very low keyword density. Mirror the language used in the "
                            + "job description to improve ATS (Applicant Tracking System) ranking.");
        } else if (keywordDensity < 0.04) {
            suggestions.add("Increase keyword usage from the job description to improve ATS compatibility.");
        }

        // ---- 6. Overall AI score suggestions ----
        if (aiScore < 50) {
            suggestions.add("Overall AI score is below 50. Consider a complete resume overhaul: "
                            + "restructure sections, add quantified achievements, and align with the job.");
        } else if (aiScore < 70) {
            suggestions.add("Good foundation. Improve your score by adding certifications, "
                            + "quantifying achievements (e.g., 'Reduced latency by 30%'), and adding missing skills.");
        } else if (aiScore < 85) {
            suggestions.add("Strong resume. Fine-tune by tailoring your summary statement to the specific role "
                            + "and ensuring all listed technologies have associated project context.");
        } else {
            suggestions.add("Excellent resume! Ensure your LinkedIn profile and GitHub are up to date "
                            + "to complement your strong resume.");
        }

        // ---- 7. General best-practice suggestions ----
        suggestions.add("Use action verbs (e.g., 'Designed', 'Implemented', 'Optimised') "
                        + "to start each bullet point in your experience and project sections.");
        suggestions.add("Quantify your achievements wherever possible "
                        + "(e.g., 'Improved API response time by 40%', 'Led a team of 5').");
        suggestions.add("Keep your resume to 1–2 pages and use a clean, ATS-friendly format.");

        return suggestions;
    }

    /**
     * Highlights important missing keywords from the job description.
     * Prints a formatted section to the console.
     * @param missingSkills list of missing skills
     */
    public void printMissingKeywordOptimizer(List<String> missingSkills) {
        System.out.println("\n+--------------------------------------------+");
        System.out.println("      KEYWORD OPTIMIZER — ADD TO RESUME       ");
        System.out.println("+--------------------------------------------+");
        if (missingSkills.isEmpty()) {
            System.out.println("  Great! No critical keywords are missing.");
        } else {
            System.out.println("  Important keywords missing from your resume:");
            for (String skill : missingSkills) {
                System.out.println("    >> " + skill.toUpperCase());
            }
            System.out.println("\n  Tip: Naturally include these in your Skills section,");
            System.out.println("  project descriptions, or experience bullet points.");
        }
        System.out.println("+--------------------------------------------+");
    }
}
