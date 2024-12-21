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
}
