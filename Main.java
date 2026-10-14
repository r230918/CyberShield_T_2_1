import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  CYBERSHIELD: HOST-BASED SECURITY LOG ANALYSIS SYSTEM");
        System.out.println("  Using Data Structures and Algorithms (DSA Coursework)");
        System.out.println("===============================================================");
        System.out.print("[INIT] Loading Default Security Dataset (data/security_logs.csv)... ");
        CSVLogManager logManager = new CSVLogManager();
        logManager.loadDefaultLogs("data/security_logs.csv");
        System.out.println("DONE (" + logManager.getLogs().size() + " logs mounted)");

        System.out.print("[INIT] Loading IDS Threat Signatures (Aho-Corasick Engine)... ");
        DetectionEngine detectionEngine = new DetectionEngine();
        System.out.println("DONE (" + detectionEngine.getRules().size() + " active rules)");

        LogStreamProcessor streamProcessor = new LogStreamProcessor(detectionEngine, logManager);
        Scanner scanner = new Scanner(System.in);
        MenuManager menuManager = new MenuManager(scanner, logManager, detectionEngine, streamProcessor);
        menuManager.start();
    }
}
