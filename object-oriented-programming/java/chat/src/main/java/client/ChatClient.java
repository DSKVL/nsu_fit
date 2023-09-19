package Client;

import Graphic.TextContainer;
import Server.ChatHistoryManager;
import Server.Config;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ChatClient {
    String userName;
    private final int serverPort;
    private final String localhost;
    private final boolean graphicFlag;
    private final JTextArea textArea;
    //private BufferedReader keyboard;
    private String text;
    private TextContainer textContainer;
    private String line2;

    public void changeText(String inputText) {
        text = inputText;
    }

    public ChatClient() {
        userName = "just a Potato";
        localhost = "localhost";
        serverPort = 7453;
        graphicFlag = false;
        textArea = null;
        text = null;
    }

    public ChatClient(String inputUserName, String inputLocalhost, int inputServerPort, JTextArea inputTextArea, boolean inputGraphicFlag) {
        userName = inputUserName;
        localhost = inputLocalhost;
        serverPort = inputServerPort;
        if ((!inputGraphicFlag) || (inputTextArea == null)) {
            graphicFlag = false;
            textArea = null;
        } else {
            graphicFlag = inputGraphicFlag;
            textArea = inputTextArea;
        }
    }

    public ChatClient(String inputUserName, String inputLocalhost, int inputServerPort, JTextArea inputTextArea, boolean inputGraphicFlag, TextContainer inputTextContainer, ChatHistoryManager inputChatHistoryManager) {
        userName = inputUserName;
        localhost = inputLocalhost;
        serverPort = inputServerPort;
        if ((!inputGraphicFlag) || (inputTextArea == null) || (inputTextContainer == null) || (inputChatHistoryManager == null)) {
            graphicFlag = false;
            textArea = null;
            text = null;
        } else {
            graphicFlag = inputGraphicFlag;
            textArea = inputTextArea;
            textContainer = inputTextContainer;
        }
    }

    public void startClient() {
        Socket socket = null;
        try {
            System.out.printf("""
                    It's Client.Client side
                    Connecting to the server
                        (IP address: %s:%d)""", localhost, serverPort);


            InetAddress ipAddress = InetAddress.getByName(localhost);
            socket = new Socket(ipAddress, serverPort);

            System.out.println("Client.Client was connected.");
            System.out.println("\tLocalPort = " + socket.getLocalPort()
                    + "\n\tInetAddress.HostAddress = "
                    + socket.getInetAddress().getHostAddress());

            //входной и выходной потоки сокета для обмена сообщениями с сервером
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            //поток для чтения с клавиатуры.
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);

            BufferedReader keyboard = new BufferedReader(inputStreamReader);
            System.out.println("Ready to chat. Type something and press enter... (for example Info)");
            out.writeUTF(userName);
            out.flush();
            appendMessageOnTextArea(userName + " joined the server\n");
            Timer timer = new Timer(Config.getTimeout(), e -> {
                line2 = "/quit";
                try {
                    sendMessageFromString(line2, in, out, false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            timer.setRepeats(false);
            timer.start();
            sendMessageFromString("/showHistory", in, out, false);
            line2 = "";
            if (textContainer != null)
                textContainer.setText("");

            while ((line2 == null) || (!line2.endsWith("/quit"))) {

                if (keyboard.ready() && (!graphicFlag)) {
                    line2 = sendMessage(keyboard, in, out);
                    timer.restart();
                } else if (in.available() > 0) {
                    readNewMessages(in, true);
                } else if ((graphicFlag)) {
                    line2 = sendMessageFromTextContainer(textContainer, in, out, false);
                    timer.restart();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String sendMessage(BufferedReader keyboard, DataInputStream in, DataOutputStream out) throws IOException {
        //ввел строку и нажал Enter
        String line = keyboard.readLine();
        // Отсылаем строку серверу
        out.writeUTF(line);
        // Завершаем поток
        out.flush();
        // Ждем ответа от сервера
        line = readNewMessages(in, true);
        //appendMessageOnTextArea(line);
        return line;
    }

    String sendMessageFromTextContainer(TextContainer inTextContainer, DataInputStream in, DataOutputStream out, boolean setToGraphicFlag) throws IOException {
        String line = null;
        //ввел строку и нажал Enter
        line = textContainer.getText();
        //line = keyboard.readLine();
        // Отсылаем строку серверу
        if (!line.equals("")) {
            out.writeUTF(line);
            // Завершаем поток
            out.flush();
            // Ждем ответа от сервера
            line = readNewMessages(in, true);
            if (setToGraphicFlag)
                appendMessageOnTextArea(line);
            text = "";
            if (textContainer != null)
                inTextContainer.setText(text);
        }
        return line;
    }

    String sendMessageFromString(String text, DataInputStream in, DataOutputStream out, boolean setToGraphicFlag) throws IOException {
        String line = text;
        out.writeUTF(line);
        // Завершаем поток
        out.flush();
        // Ждем ответа от сервера
        line = readNewMessages(in, setToGraphicFlag);
        if (setToGraphicFlag)
            appendMessageOnTextArea(line);
        if (textContainer != null)
            textContainer.setText("");
        return line;
    }

    String readNewMessages(DataInputStream in, boolean setToGraphicFlag) throws IOException {
        String line = null;
        line = in.readUTF();
        switch (line) {
            case "/quit" -> {
                return line;
            }
            case "/usersList" -> {
                line = in.readUTF();
                int clientsCount = Integer.parseInt(line);
                System.out.println("The server sent clients count:" + clientsCount + "\n\t");
                for (int i = 0; i < clientsCount; i++) {
                    line = in.readUTF();
                    System.out.println(line);
                }
                return line;
            }
            case "/showHistory" -> {
                line = in.readUTF();
                System.out.println(line);
                return line;
            }
            default -> {
                System.out.println(line);
                if (setToGraphicFlag)
                    appendMessageOnTextArea(line);
                //in.reset();
                return line;
            }
        }
    }

    public void appendMessageOnTextArea(String text) {
        if (graphicFlag)
            textArea.append(text + "\n");
    }
}
