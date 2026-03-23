import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

/**
 * ResumeAnalyzerGUI.java
 * Feature 1: Full Swing GUI for the AI Resume Intelligence System.
 *
 * Architecture:
 *   JFrame
 *   ├── Header panel (gradient title bar)
 *   ├── Split pane
 *   │   ├── LEFT  — Upload buttons + Job Description text area
 *   │   └── RIGHT — JTabbedPane with 5 result tabs
 *   └── Status bar (self-learning notifications)
 *
 * All backend modules are used through this class.
 */
public class ResumeAnalyzerGUI extends JFrame {

    // ====================================================================
    // Constants — colour palette
    // ====================================================================
    private static final Color BG_DARK     = new Color(18, 20, 30);
    private static final Color BG_PANEL    = new Color(28, 32, 48);
    private static final Color BG_CARD     = new Color(36, 41, 61);
    private static final Color ACCENT_BLUE = new Color(64, 156, 255);
    private static final Color ACCENT_TEAL = new Color(0, 210, 190);
    private static final Color ACCENT_GOLD = new Color(255, 196, 0);
    private static final Color TEXT_MAIN   = new Color(220, 225, 240);
    private static final Color TEXT_DIM    = new Color(140, 150, 175);
    private static final Color SUCCESS_GRN = new Color(50, 210, 120);
    private static final Color WARN_AMBER  = new Color(255, 165, 0);
    private static final Color DANGER_RED  = new Color(255, 75, 75);

    // ====================================================================
    // Backend modules
    // ====================================================================
    private SkillLearner           skillLearner;
    private SkillExtractor         skillExtractor;
    private TextProcessor          textProcessor;
    private SimilarityCalculator   simCalc;
    private ResumeScoringAI        scoringAI;
    private InterviewQuestionGenerator iqGen;
    private SuggestionEngine       sugEng;
    private ResumeRanker           ranker;
    private FraudDetector          fraudDetector;

    // ====================================================================
    // UI components
    // ====================================================================
    // Left panel
    private JLabel      resumePathLabel;
    private JTextArea   jobDescArea;

    // Right — tabs
    private JTabbedPane tabbedPane;
    private JTextArea   analysisArea;   // Tab 0 – Analysis
    private JTable      rankingTable;   // Tab 1 – Ranking
    private JTextArea   interviewArea;  // Tab 2 – Interview Q
    private JTextArea   demandArea;     // Tab 3 – Skill Demand
    private JTextArea   suggestArea;    // Tab 4 – Suggestions

    // Status bar
    private JLabel statusBar;

    // State
    private File   selectedSingleResume = null;
    private List<File> selectedMultiResumes = new ArrayList<>();

    // ====================================================================
    // Constructor
    // ====================================================================
    public ResumeAnalyzerGUI() {
        initBackend();
        initUI();
    }

    // --------------------------------------------------------------------
    // Backend initialisation
    // --------------------------------------------------------------------
    private void initBackend() {
        // Determine path to skills.txt (same directory as running class)
        String skillsPath = "skills.txt";

        skillLearner   = new SkillLearner(skillsPath);
        List<String> skills = skillLearner.loadSkills();

        skillExtractor = new SkillExtractor(skills);
        textProcessor  = new TextProcessor();
        simCalc        = new SimilarityCalculator();
        scoringAI      = new ResumeScoringAI();
        iqGen          = new InterviewQuestionGenerator();
        sugEng         = new SuggestionEngine();
        ranker         = new ResumeRanker();
        fraudDetector  = new FraudDetector();
    }

    // --------------------------------------------------------------------
    // UI construction
    // --------------------------------------------------------------------
    private void initUI() {
        setTitle("AI Resume Intelligence System  v2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildMainArea(),  BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------------ header
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 60, 130),
                                                     getWidth(), 0, new Color(10, 140, 130));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel title = new JLabel("⚡  AI Resume Intelligence System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Self-Learning • Semantic Matching • Fraud Detection");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(200, 230, 255));

        JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 3));
        textBox.setOpaque(false);
        textBox.add(title);
        textBox.add(subtitle);
        header.add(textBox, BorderLayout.WEST);

        JLabel badge = new JLabel("Core Java  |  Swing  |  v2.0");
        badge.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        badge.setForeground(new Color(180, 220, 255));
        badge.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    // ------------------------------------------------------------------ main split
    private JSplitPane buildMainArea() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(370);
        split.setBackground(BG_DARK);
        split.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        split.setDividerSize(6);
        return split;
    }

    // ------------------------------------------------------------------ LEFT
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PANEL);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 14, 14, 14));

        // ── Section: Upload single resume ──────────────────────────────────
        panel.add(sectionLabel("📄  Upload Resume"));
        panel.add(Box.createVerticalStrut(6));

        JButton uploadBtn = accentButton("Upload Resume (.txt)", ACCENT_BLUE);
        uploadBtn.setAlignmentX(LEFT_ALIGNMENT);
        uploadBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        uploadBtn.addActionListener(e -> chooseSingleResume());
        panel.add(uploadBtn);

        panel.add(Box.createVerticalStrut(8));
        resumePathLabel = new JLabel("No file selected");
        resumePathLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        resumePathLabel.setForeground(TEXT_DIM);
        resumePathLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(resumePathLabel);

        // ── Section: Upload multiple resumes ──────────────────────────────
        panel.add(Box.createVerticalStrut(16));
        panel.add(sectionLabel("📂  Multiple Resumes (Ranking)"));
        panel.add(Box.createVerticalStrut(6));

        JButton multiBtn = accentButton("Upload Multiple Resumes", ACCENT_TEAL);
        multiBtn.setAlignmentX(LEFT_ALIGNMENT);
        multiBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        multiBtn.addActionListener(e -> chooseMultipleResumes());
        panel.add(multiBtn);

        // ── Section: Job Description ───────────────────────────────────────
        panel.add(Box.createVerticalStrut(18));
        panel.add(sectionLabel("💼  Job Description"));
        panel.add(Box.createVerticalStrut(6));

        jobDescArea = new JTextArea(10, 1);
        jobDescArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jobDescArea.setBackground(BG_CARD);
        jobDescArea.setForeground(TEXT_MAIN);
        jobDescArea.setCaretColor(ACCENT_BLUE);
        jobDescArea.setLineWrap(true);
        jobDescArea.setWrapStyleWord(true);
        jobDescArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        jobDescArea.setText("Paste job description here...");
        jobDescArea.setForeground(TEXT_DIM);
        jobDescArea.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (jobDescArea.getText().equals("Paste job description here...")) {
                    jobDescArea.setText("");
                    jobDescArea.setForeground(TEXT_MAIN);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (jobDescArea.getText().trim().isEmpty()) {
                    jobDescArea.setText("Paste job description here...");
                    jobDescArea.setForeground(TEXT_DIM);
                }
            }
        });

        JScrollPane jdScroll = new JScrollPane(jobDescArea);
        jdScroll.setAlignmentX(LEFT_ALIGNMENT);
        jdScroll.setBorder(BorderFactory.createLineBorder(BG_CARD));
        panel.add(jdScroll);

        // ── Analyze button ─────────────────────────────────────────────────
        panel.add(Box.createVerticalStrut(18));
        JButton analyzeBtn = accentButton("🔍  Analyze Resume", ACCENT_GOLD);
        analyzeBtn.setForeground(BG_DARK);
        analyzeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        analyzeBtn.setAlignmentX(LEFT_ALIGNMENT);
        analyzeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        analyzeBtn.addActionListener(e -> runAnalysis());
        panel.add(analyzeBtn);

        return panel;
    }

    // ------------------------------------------------------------------ RIGHT tabbed
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(BG_PANEL);
        tabbedPane.setForeground(TEXT_MAIN);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 0: Analysis
        analysisArea = resultTextArea();
        tabbedPane.addTab("📊 Analysis",    scrollOf(analysisArea));

        // Tab 1: Ranking
        rankingTable = new JTable(new DefaultTableModel(
            new Object[]{"Rank", "Candidate", "AI Score", "Match %", "Status"}, 0));
        styleTable(rankingTable);
        JScrollPane rankScroll = new JScrollPane(rankingTable);
        rankScroll.getViewport().setBackground(BG_CARD);
        tabbedPane.addTab("🏆 Ranking",    rankScroll);

        // Tab 2: Interview Q
        interviewArea = resultTextArea();
        tabbedPane.addTab("🎤 Interview",  scrollOf(interviewArea));

        // Tab 3: Skill Demand
        demandArea = resultTextArea();
        tabbedPane.addTab("📈 Skill Demand", scrollOf(demandArea));

        // Tab 4: Suggestions
        suggestArea = resultTextArea();
        tabbedPane.addTab("💡 Suggestions", scrollOf(suggestArea));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------ Status bar
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(12, 14, 22));
        bar.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        bar.setPreferredSize(new Dimension(0, 28));

        statusBar = new JLabel("Ready  —  Skill database loaded  (" + skillExtractor.getDatabaseSize() + " skills)");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(TEXT_DIM);
        bar.add(statusBar, BorderLayout.WEST);

        JLabel ver = new JLabel("AI Resume Intelligence System  v2.0  |  Core Java + Swing");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(80, 90, 120));
        ver.setHorizontalAlignment(SwingConstants.RIGHT);
        bar.add(ver, BorderLayout.EAST);

        return bar;
    }

    // ====================================================================
    // Actions
    // ====================================================================

    // ------------------------------------------------------------------ choose single
    private void chooseSingleResume() {
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle("Select Resume File");
        fc.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedSingleResume = fc.getSelectedFile();
            resumePathLabel.setText("✓  " + selectedSingleResume.getName());
            resumePathLabel.setForeground(SUCCESS_GRN);
            setStatus("Resume loaded: " + selectedSingleResume.getName());
        }
    }

    // ------------------------------------------------------------------ choose multiple
    private void chooseMultipleResumes() {
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle("Select Multiple Resume Files");
        fc.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        fc.setMultiSelectionEnabled(true);
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();
            selectedMultiResumes.clear();
            selectedMultiResumes.addAll(Arrays.asList(files));
            setStatus("Loaded " + files.length + " resumes for ranking.");
        }
    }

    // ------------------------------------------------------------------ main analysis
    private void runAnalysis() {
        // Validate inputs
        String jdText = jobDescArea.getText().trim();
        if (jdText.isEmpty() || jdText.equals("Paste job description here...")) {
            showError("Please enter a job description.");
            return;
        }
        if (selectedSingleResume == null) {
            showError("Please upload a resume file first.");
            return;
        }

        setStatus("Analyzing…");

        // ── Step 1: Analyze job description ─────────────────────────────────
        JobAnalyzer jobAnalyzer = new JobAnalyzer(skillExtractor);
        jobAnalyzer.analyzeText(jdText);
        List<String> requiredSkills = jobAnalyzer.getRequiredSkills();

        // ── Step 2: Read resume ──────────────────────────────────────────────
        String rawResume = readFileContent(selectedSingleResume);
        if (rawResume == null) {
            showError("Cannot read resume file.");
            return;
        }

        // ── Step 3: Preprocess + extract skills ─────────────────────────────
        List<String> tokens         = textProcessor.preprocess(rawResume);
        List<String> detectedSkills = skillExtractor.extractSkills(tokens);

        // ── Step 4: Self-learning (Feature 4) ───────────────────────────────
        skillLearner.trackTokens(tokens);
        skillLearner.trackTokens(textProcessor.preprocess(jdText));
        List<String> newSkills = skillLearner.learnAndUpdate();
        for (String ns : newSkills) {
            skillExtractor.addSkill(ns);
        }

        // ── Step 5: Matching ─────────────────────────────────────────────────
        double matchScore     = simCalc.calculateMatchScore(detectedSkills, requiredSkills);
        int    matchedCount   = simCalc.countMatched(detectedSkills, requiredSkills);
        List<String> missing  = simCalc.findMissingSkills(detectedSkills, requiredSkills);
        List<String> extra    = simCalc.findExtraSkills(detectedSkills, requiredSkills);

        // ── Step 6: Scoring ─────────────────────────────────────────────────
        int    yearsExp    = scoringAI.extractExperienceYears(rawResume);
        int    projCount   = scoringAI.extractProjectCount(rawResume);
        Set<String> jdKw   = new HashSet<>(requiredSkills);
        double kwDensity   = textProcessor.calculateKeywordDensity(tokens, jdKw);
        int    aiScore     = scoringAI.computeScore(matchScore, yearsExp, projCount,
                                                     kwDensity, detectedSkills.size());
        String breakdown   = scoringAI.getScoreBreakdown(matchScore, yearsExp, projCount,
                                                          kwDensity, detectedSkills.size());

        // ── Step 7: Fraud detection (Feature 7) ─────────────────────────────
        List<String> fraudWarnings = fraudDetector.detect(
                detectedSkills.size(), projCount, yearsExp, matchScore);

        // ── Step 8: Interview questions (Feature 9) ──────────────────────────
        Map<String, String> iqMap = iqGen.generateForAll(detectedSkills);

        // ── Step 9: Suggestions ──────────────────────────────────────────────
        List<String> suggestions = sugEng.generateSuggestions(
                missing, matchScore, yearsExp, projCount, kwDensity, aiScore);

        // ── Step 10: Skill demand (Feature 10) ──────────────────────────────
        Map<String, Integer> demandMap = jobAnalyzer.getSkillDemandMap();

        // ── Update all tabs ─────────────────────────────────────────────────
        populateAnalysisTab(matchScore, matchedCount, requiredSkills.size(),
                             detectedSkills, missing, extra, aiScore, breakdown,
                             yearsExp, projCount, fraudWarnings);

        populateRankingTab(rawResume, requiredSkills, detectedSkills, missing,
                           matchScore, aiScore);

        populateInterviewTab(iqMap);
        populateDemandTab(demandMap, requiredSkills.size());
        populateSuggestTab(suggestions);

        // ── Status bar newskills notification ───────────────────────────────
        if (!newSkills.isEmpty()) {
            setStatus("✨  New skills learned: " + String.join(", ", newSkills)
                    + "  |  Skill DB now: " + skillExtractor.getDatabaseSize());
        } else {
            setStatus("Analysis complete  |  AI Score: " + aiScore + "/100  |  Match: "
                    + String.format("%.1f", matchScore) + "%");
        }

        tabbedPane.setSelectedIndex(0);
    }

    // ====================================================================
    // Tab population helpers
    // ====================================================================

    // ------------------------------------------------------------------ Analysis tab
    private void populateAnalysisTab(double matchScore, int matchedCount, int totalRequired,
                                      List<String> detected, List<String> missing,
                                      List<String> extra, int aiScore, String breakdown,
                                      int yearsExp, int projCount, List<String> fraudWarnings) {
        StringBuilder sb = new StringBuilder();

        sb.append("══════════════════════════════════════════════════\n");
        sb.append("          AI RESUME ANALYSIS REPORT\n");
        sb.append("══════════════════════════════════════════════════\n\n");

        sb.append("  Resume File  : ").append(selectedSingleResume.getName()).append("\n");
        sb.append("  Experience   : ").append(yearsExp).append(" year(s) detected\n");
        sb.append("  Projects     : ").append(projCount).append(" detected\n\n");

        // Match score bar
        sb.append("──────────────────────────────────────────────────\n");
        sb.append(String.format("  MATCH SCORE : %.1f%%  (%d / %d skills)\n",
                matchScore, matchedCount, totalRequired));
        sb.append("  ").append(progressBar(matchScore, 40)).append("\n\n");

        // AI Score
        sb.append("──────────────────────────────────────────────────\n");
        sb.append("  AI RESUME SCORE : ").append(aiScore).append(" / 100  ")
          .append(scoreLabel(aiScore)).append("\n");
        sb.append("  ").append(progressBar(aiScore, 40)).append("\n\n");
        sb.append(breakdown).append("\n\n");

        // Fraud warnings
        if (!fraudWarnings.isEmpty()) {
            sb.append("──────────────────────────────────────────────────\n");
            sb.append("  FRAUD / EXAGGERATION ALERTS\n");
            sb.append("──────────────────────────────────────────────────\n");
            for (String w : fraudWarnings) sb.append("  ").append(w).append("\n");
            sb.append("\n");
        }

        // Detected skills
        sb.append("──────────────────────────────────────────────────\n");
        sb.append("  DETECTED SKILLS (").append(detected.size()).append(" found)\n");
        sb.append("──────────────────────────────────────────────────\n");
        appendBullets(sb, detected);

        // Missing skills
        sb.append("\n──────────────────────────────────────────────────\n");
        sb.append("  MISSING SKILLS (").append(missing.size()).append(" gaps)\n");
        sb.append("──────────────────────────────────────────────────\n");
        if (missing.isEmpty()) {
            sb.append("  ✓  No missing skills! Perfect match.\n");
        } else {
            appendBullets(sb, missing);
        }

        // Bonus skills
        if (!extra.isEmpty()) {
            sb.append("\n──────────────────────────────────────────────────\n");
            sb.append("  BONUS SKILLS (present but not required)\n");
            sb.append("──────────────────────────────────────────────────\n");
            appendBullets(sb, extra);
        }

        analysisArea.setText(sb.toString());
        analysisArea.setCaretPosition(0);
    }

    // ------------------------------------------------------------------ Ranking tab
    private void populateRankingTab(String primaryRaw, List<String> requiredSkills,
                                     List<String> primaryDetected, List<String> primaryMissing,
                                     double primaryMatch, int primaryScore) {
        ranker.clear();

        // Add primary resume
        String primaryName = extractName(primaryRaw, selectedSingleResume.getName());
        ranker.addCandidate(new ResumeRanker.CandidateResult(
                primaryName, primaryScore, primaryMatch, primaryDetected, primaryMissing));

        // Add multi-resumes if any
        for (File f : selectedMultiResumes) {
            if (f.equals(selectedSingleResume)) continue;
            String raw = readFileContent(f);
            if (raw == null) continue;

            List<String> tokens = textProcessor.preprocess(raw);
            List<String> skills = skillExtractor.extractSkills(tokens);
            List<String> miss   = simCalc.findMissingSkills(skills, requiredSkills);
            double match        = simCalc.calculateMatchScore(skills, requiredSkills);
            int exp             = scoringAI.extractExperienceYears(raw);
            int proj            = scoringAI.extractProjectCount(raw);
            Set<String> jdKw    = new HashSet<>(requiredSkills);
            double kd           = textProcessor.calculateKeywordDensity(tokens, jdKw);
            int score           = scoringAI.computeScore(match, exp, proj, kd, skills.size());
            String name         = extractName(raw, f.getName());
            ranker.addCandidate(new ResumeRanker.CandidateResult(name, score, match, skills, miss));
        }

        // Populate table
        DefaultTableModel model = (DefaultTableModel) rankingTable.getModel();
        model.setRowCount(0);
        List<ResumeRanker.CandidateResult> ranked = ranker.getRankedCandidates();
        int rank = 1;
        for (ResumeRanker.CandidateResult cr : ranked) {
            model.addRow(new Object[]{
                rank++,
                cr.getName(),
                cr.getAiScore() + " / 100",
                String.format("%.1f%%", cr.getMatchPercent()),
                scoreLabel(cr.getAiScore())
            });
        }
    }

    // ------------------------------------------------------------------ Interview tab
    private void populateInterviewTab(Map<String, String> iqMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════\n");
        sb.append("         INTERVIEW QUESTION GENERATOR\n");
        sb.append("══════════════════════════════════════════════════\n\n");

        if (iqMap.isEmpty()) {
            sb.append("  No skills detected — upload a resume and run analysis.\n");
        } else {
            for (Map.Entry<String, String> e : iqMap.entrySet()) {
                sb.append("  ┌─ Skill : ").append(e.getKey().toUpperCase()).append("\n");
                sb.append("  │  Q     : ").append(e.getValue()).append("\n");
                sb.append("  └───────────────────────────────────────\n\n");
            }
        }
        interviewArea.setText(sb.toString());
        interviewArea.setCaretPosition(0);
    }

    // ------------------------------------------------------------------ Demand tab
    private void populateDemandTab(Map<String, Integer> demandMap, int totalRequired) {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════\n");
        sb.append("         SKILL DEMAND ANALYZER (from JD)\n");
        sb.append("══════════════════════════════════════════════════\n\n");
        sb.append(String.format("  Total required skills detected: %d\n\n", totalRequired));

        if (demandMap.isEmpty()) {
            sb.append("  No skill frequency data available.\n");
            sb.append("  Tip: A richer job description yields better demand analysis.\n");
        } else {
            sb.append("  ── Most Demanded Skills ──────────────────────\n\n");
            for (Map.Entry<String, Integer> e : demandMap.entrySet()) {
                String bar = miniBar(e.getValue(), 10);
                sb.append(String.format("  %-18s %s  [%d mention(s)]\n",
                        e.getKey().toUpperCase(), bar, e.getValue()));
            }
        }
        demandArea.setText(sb.toString());
        demandArea.setCaretPosition(0);
    }

    // ------------------------------------------------------------------ Suggestions tab
    private void populateSuggestTab(List<String> suggestions) {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════════════════\n");
        sb.append("        RESUME IMPROVEMENT SUGGESTIONS\n");
        sb.append("══════════════════════════════════════════════════\n\n");
        int i = 1;
        for (String s : suggestions) {
            sb.append("  ").append(i++).append(". ").append(s).append("\n\n");
        }
        suggestArea.setText(sb.toString());
        suggestArea.setCaretPosition(0);
    }

    // ====================================================================
    // UI factory helpers
    // ====================================================================

    private JTextArea resultTextArea() {
        JTextArea ta = new JTextArea();
        ta.setFont(new Font("Consolas", Font.PLAIN, 13));
        ta.setBackground(BG_CARD);
        ta.setForeground(TEXT_MAIN);
        ta.setCaretColor(ACCENT_BLUE);
        ta.setEditable(false);
        ta.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    private JScrollPane scrollOf(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(null);
        return sp;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ACCENT_BLUE);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(orig.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(orig);
            }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(new Color(50, 55, 80));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(64, 80, 140));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(ACCENT_BLUE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_BLUE));
    }

    // ====================================================================
    // Text / display utilities
    // ====================================================================

    private void appendBullets(StringBuilder sb, List<String> items) {
        if (items.isEmpty()) { sb.append("  (none)\n"); return; }
        for (String s : items) {
            sb.append("  •  ").append(s.toUpperCase()).append("\n");
        }
    }

    private String progressBar(double percent, int width) {
        int filled = (int) ((percent / 100.0) * width);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append(String.format("]  %.1f%%", percent));
        return bar.toString();
    }

    private String miniBar(int value, int scale) {
        int len = Math.min(value * 2, scale);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < scale; i++) bar.append(i < len ? "▓" : "░");
        bar.append("]");
        return bar.toString();
    }

    private String scoreLabel(int score) {
        if (score >= 85) return "★ Excellent";
        if (score >= 70) return "✔ Good";
        if (score >= 55) return "~ Average";
        return "✗ Needs Work";
    }

    private String extractName(String rawText, String fallback) {
        if (rawText == null) return fallback;
        for (String line : rawText.split("\n")) {
            String t = line.trim();
            if (t.toLowerCase().startsWith("name:")) {
                String name = t.substring(5).trim();
                if (!name.isEmpty()) return name;
            }
        }
        return fallback.replace(".txt", "");
    }

    private String readFileContent(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (IOException e) {
            return null;
        }
        return sb.toString().trim();
    }

    private void setStatus(String msg) {
        statusBar.setText(msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
