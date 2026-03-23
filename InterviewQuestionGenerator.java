import java.util.*;

/**
 * InterviewQuestionGenerator.java
 * Upgrade 2: AI Interview Question Generator.
 *
 * Uses a HashMap<String, List<String>> to map skills to multiple
 * interview questions. Returns a random question per skill for variety.
 */
public class InterviewQuestionGenerator {

    // Skill → List of interview questions
    private final Map<String, List<String>> questionBank;
    private final Random random = new Random();

    public InterviewQuestionGenerator() {
        questionBank = new HashMap<>();

        // ---- Java ----
        questionBank.put("java", Arrays.asList(
            "Explain the difference between HashMap and Hashtable in Java.",
            "What is the Java Memory Model and how does garbage collection work?",
            "What are the SOLID principles and how do you apply them in Java?",
            "Explain the difference between an interface and an abstract class.",
            "What is the difference between 'equals()' and '==' in Java?"
        ));

        // ---- Python ----
        questionBank.put("python", Arrays.asList(
            "What are Python decorators and how do they work?",
            "Explain the difference between a list and a tuple in Python.",
            "What is the Global Interpreter Lock (GIL) in Python?",
            "How does Python manage memory?",
            "What are Python generators and when should you use them?"
        ));

        // ---- React ----
        questionBank.put("react", Arrays.asList(
            "What is the Virtual DOM and how does React use it?",
            "Explain the difference between state and props in React.",
            "What are React hooks and why were they introduced?",
            "What is the component lifecycle in React?",
            "How does React handle event delegation?"
        ));

        // ---- Docker ----
        questionBank.put("docker", Arrays.asList(
            "What is containerization and how does Docker implement it?",
            "What is the difference between a Docker image and a Docker container?",
            "Explain the role of a Dockerfile.",
            "What is Docker Compose and when would you use it?",
            "How do Docker volumes differ from bind mounts?"
        ));

        // ---- AWS ----
        questionBank.put("aws", Arrays.asList(
            "Explain the difference between EC2, ECS, and EKS on AWS.",
            "What is an AWS S3 bucket and what are its use cases?",
            "How does AWS IAM work and what best practices should be followed?",
            "What is the difference between horizontal and vertical scaling on AWS?",
            "Explain the AWS VPC and subnets concept."
        ));

        // ---- Kubernetes ----
        questionBank.put("kubernetes", Arrays.asList(
            "What is Kubernetes and what problem does it solve?",
            "Explain the difference between a Pod and a Deployment in Kubernetes.",
            "What is a Kubernetes Service and why is it needed?",
            "How does Kubernetes handle container health checks?",
            "What is Helm and how does it integrate with Kubernetes?"
        ));

        // ---- Microservices ----
        questionBank.put("microservices", Arrays.asList(
            "What are microservices and how do they differ from monoliths?",
            "How do microservices communicate with each other?",
            "What is service discovery in a microservices architecture?",
            "How do you handle data consistency across microservices?",
            "What is the Saga pattern and when is it used in microservices?"
        ));

        // ---- Spring / Spring Boot ----
        questionBank.put("spring", Arrays.asList(
            "What is Dependency Injection and how does Spring implement it?",
            "What is the Spring Bean lifecycle?",
            "Explain the difference between @Component, @Service, and @Repository.",
            "What is Spring Boot auto-configuration?",
            "How does Spring Security handle authentication and authorization?"
        ));
        questionBank.put("springboot", questionBank.get("spring")); // alias

        // ---- SQL ----
        questionBank.put("sql", Arrays.asList(
            "What is the difference between INNER JOIN, LEFT JOIN, and FULL OUTER JOIN?",
            "Explain database normalization and the different normal forms.",
            "What is an index and how does it improve query performance?",
            "What is a transaction and what are ACID properties?",
            "Explain the difference between DELETE, TRUNCATE, and DROP."
        ));

        // ---- MongoDB ----
        questionBank.put("mongodb", Arrays.asList(
            "How does MongoDB differ from a relational database?",
            "What are MongoDB's aggregation pipelines?",
            "When would you choose MongoDB over a relational database?",
            "What is a replica set in MongoDB?",
            "How does MongoDB handle schema design for one-to-many relationships?"
        ));

        // ---- Git ----
        questionBank.put("git", Arrays.asList(
            "What is the difference between 'git rebase' and 'git merge'?",
            "Explain the Git branching workflow (Gitflow).",
            "What does 'git stash' do?",
            "How do you resolve a merge conflict in Git?",
            "What is the difference between 'git fetch' and 'git pull'?"
        ));

        // ---- Agile / Scrum ----
        questionBank.put("agile", Arrays.asList(
            "What are the 12 principles of the Agile Manifesto?",
            "Explain the Scrum ceremonies (Sprint Planning, Daily Standup, etc.).",
            "What is the difference between Scrum and Kanban?",
            "What is 'Definition of Done' in Agile?",
            "How do you estimate story points?"
        ));
        questionBank.put("scrum", questionBank.get("agile")); // alias

        // ---- Default / fallback ----
        // (other skills not in map will get a generic question)
    }

    /**
     * Generates one (random) interview question for a given skill.
     * @param skill the skill keyword (lowercase)
     * @return an interview question string, or a generic prompt if skill not in bank
     */
    public String generateQuestion(String skill) {
        List<String> questions = questionBank.get(skill.toLowerCase());
        if (questions == null || questions.isEmpty()) {
            return "Can you describe your experience with " + skill.toUpperCase()
                   + " and a challenging problem you solved using it?";
        }
        return questions.get(random.nextInt(questions.size()));
    }

    /**
     * Generates questions for all detected skills and returns them as a map.
     * @param skills list of detected skill keywords
     * @return map of skill → question
     */
    public Map<String, String> generateForAll(List<String> skills) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String skill : skills) {
            result.put(skill, generateQuestion(skill));
        }
        return result;
    }

    /**
     * Returns true if the question bank has questions for the given skill.
     */
    public boolean hasQuestion(String skill) {
        return questionBank.containsKey(skill.toLowerCase());
    }
}
