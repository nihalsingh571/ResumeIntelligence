import javax.swing.SwingUtilities;
import java.util.*;

/**
 * =============================================================================
 *  AI Resume Intelligence System — Main.java
 *  v2.0 — GUI Edition | Core Java + Swing | No External Libraries
 * =============================================================================
 *
 *  How to compile and run:
 *    javac *.java
 *    java Main
 *
 *  Ensure the following files exist in the same directory:
 *    skills.txt   (auto-generated from SkillLearner if absent)
 *    resume.txt   (sample resume for single analysis)
 *    job.txt      (sample job description)
 *
 *  All analysis is now driven by the GUI.
 *  Legacy console helpers are preserved for reference / viva explanation.
 * =============================================================================
 */
public class Main {

    // ─────────────────────────────────────────────────────────────────────────
    //  Main entry point — launches the Swing GUI
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Use system look-and-feel for native window decorations
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall through to default Swing LAF
        }

        // Launch GUI on the Event Dispatch Thread (Swing thread-safety)
        SwingUtilities.invokeLater(() -> {
            ResumeAnalyzerGUI gui = new ResumeAnalyzerGUI();
            gui.setVisible(true);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Preserved legacy console helpers (not called by GUI — kept for viva)
    // ─────────────────────────────────────────────────────────────────────────

    /** Java-8-compatible String.repeat alternative. */
    public static String rep(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    /**
     * Extracts the candidate name from raw resume text.
     * Looks for a line starting with "Name:".
     */
    public static String extractName(String rawText, String fallback) {
        if (rawText == null) return fallback;
        for (String line : rawText.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.toLowerCase().startsWith("name:")) {
                String name = trimmed.substring(5).trim();
                if (!name.isEmpty()) return name;
            }
        }
        return fallback.replace(".txt", "");
    }
}
