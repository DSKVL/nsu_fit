package Server;

public class Config {
    private static final int timeout = 90000;
    private static final int historyDepth = 10;
    private static final boolean saving_log_flag=true;

    public static int historyDepth() {
        return historyDepth;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static boolean isSaving_log_flag() {
        return saving_log_flag;
    }
}
