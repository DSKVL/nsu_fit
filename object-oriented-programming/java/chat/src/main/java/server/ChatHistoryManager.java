package Server;

import java.io.*;

public class ChatHistoryManager {
    private final FileWriter historyWriter;
    private final FileWriter logWriter;
    private BufferedReader historyReader;

    public ChatHistoryManager() {
        try {
            historyWriter = new FileWriter("src/main/resources/LogFile.txt", true);
            logWriter = new FileWriter("src/main/resources/LogFile.txt", true);
            BufferedReader logReader = new BufferedReader(new FileReader("src/main/resources/ChatHistory.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //----------------------------
    public void addToHistory(String text) {
        try {
            historyWriter.write(text + "\n");
            historyWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeHistory() {
        try {
            historyWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteHistory() {
        //closeHistory();
        try {
            FileWriter tmp = new FileWriter("src/main/resources/ChatHistory.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printLastMessages(int linesCount) {
        try {
            historyReader = new BufferedReader(new FileReader("src/info/ChatHistory.txt"));
            String line;
            String lastLines = "";
            String[] buffer = new String[linesCount];
            int index = 0;
            while ((line = historyReader.readLine()) != null) {
                buffer[index % linesCount] = line;
                index++;
            }
            int start = index > linesCount ? (index % linesCount) : 0;
            int end = Math.min(index, linesCount);
            for (int i = start; i < end; i++) {
                lastLines += buffer[i] + "\n";
            }
            historyReader.close();
            System.out.println(lastLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //FIXME вообще некорректно выводит (чек)
    public String getLastMessages() {
        try {
            historyReader = new BufferedReader(new FileReader("src/info/ChatHistory.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        String line = "";
        int counter = 0;
        try {
            while (true) {
                if (!((line = historyReader.readLine()) != null)) break;
                if (counter >= Config.historyDepth()) {
                    sb.delete(0, sb.indexOf("\n") + 1);
                }
                sb.append(line).append("\n");
                counter++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    //----------------------------
    public void addToLog(String text) {
        try {
            logWriter.write(text + "\n");
            logWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeLog() {
        try {
            logWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLog() {
        //closeHistory();
        try {
            FileWriter tmp = new FileWriter("src/main/resources/LogFile.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
