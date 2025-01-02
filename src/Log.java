import java.util.ArrayList;
import java.util.List;

public class Log {
    private StringBuffer log;

    public Log() {
        this.log = new StringBuffer();
    }

    public void addLogEntry(String event) {
        log.append(event).append("\n");
    }

    public String getLog() {
        return log.toString();
    }

    // New method to return log entries as a list of strings
    public List<String> getLogEntries() {
        List<String> logEntries = new ArrayList<>();
        String[] entries = log.toString().split("\n");
        for (String entry : entries) {
            logEntries.add(entry);
        }
        return logEntries;
    }
}
