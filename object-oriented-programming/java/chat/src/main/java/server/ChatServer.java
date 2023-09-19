package Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

//TODO показывыает все сообщения, которые отправили в чат с момента подключения + некоторое число, отправленных до (чек(идет из конфига))
//TODO клиент отображает такие события как: подключение нового человека в чат и уход человека из чата. Сервер должен корректно понимать ситуацию отключения клиента от чата (по таймауту) (чек)
//TODO сервер должен логгировать все события, которые происходят на его стороне (чек)

public class ChatServer extends Thread {
    private final int port;
    private Map<Integer, User> clients = new HashMap<>();
    private Socket socket;
    private int num;
    private ChatHistoryManager chatHistoryManager = new ChatHistoryManager();

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.createServer();
    }

    public ChatServer() {
        port = 7453;
    }

    public void setSocket(int num, Socket socket, Map<Integer, User> serverClients, ChatHistoryManager inputChatHistoryManager) {
        this.num = num;
        this.socket = socket;
        this.clients = serverClients;
        this.chatHistoryManager = inputChatHistoryManager;

        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        String TEMPL_CONN = "The client '%s' closed the connection";
        try {
            // Определяем входной и выходной потоки сокета
            // для обмена данными с клиентом
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            DataInputStream dis = new DataInputStream(sin);
            DataOutputStream dos = new DataOutputStream(sout);

            clients.get(num).setDis(dis);
            clients.get(num).setDos(dos);
            String userName = clients.get(num).getUserName();
            String line;

            label:
            while (true) {
                if (dis.available() > 0) {
                    // Ожидание сообщения от клиента
                    line = dis.readUTF();
                    String TEMPL_MSG = "The client '%s' sent: ";
                    System.out.println(
                            String.format(TEMPL_MSG, clients.get(num).getUserName()) + line);
                    chatHistoryManager.addToHistory(String.format(TEMPL_MSG, clients.get(num).getUserName()) + line);
                    // Отсылаем клиентам эту самую строку текста
                    for (Map.Entry<Integer, User> entry : clients.entrySet()) {
                        entry.getValue().getDos().writeUTF(userName + ": " + line);
                        dos.flush();
                    }
                    switch (line) {
                        case "/quit":
                            // завершаем соединение
                            socket.close();
                            String leftUserName = clients.get(num).getUserName();
                            System.out.printf((TEMPL_CONN) + "%n", leftUserName);
                            clients.remove(num);
                            if (Config.isSaving_log_flag())
                                chatHistoryManager.addToLog(String.format(TEMPL_CONN, leftUserName) + "\n now several counts of users: " + clients.size() + "\n");
                            for (Map.Entry<Integer, User> entry : clients.entrySet()) {
                                entry.getValue().getDos().writeUTF(userName + " left the server");
                                dos.flush();
                            }
                            break label;
                        case "/usersList":
                            int clientsCount = clients.size();
                            dos.writeUTF(String.valueOf(clientsCount));
                            dos.flush();
                            if (Config.isSaving_log_flag())
                                chatHistoryManager.addToLog(userName + "\n asked for /userList\n");
                            for (Map.Entry<Integer, User> entry : clients.entrySet()) {
                                dos.writeUTF("Id: " + entry.getKey() + ", userName: " + entry.getValue().getUserName());
                                dos.flush();
                            }
                            break;
                        case "/showHistory":
                            dos.writeUTF(chatHistoryManager.getLastMessages());
                            dos.flush();
                            if (Config.isSaving_log_flag())
                                chatHistoryManager.addToLog(userName + "\n asked for /showHistory\n");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(this.clients.get(this.num).getUserName() + "send: " + "Exception : " + e);

            System.out.printf((TEMPL_CONN) + "%n", clients.get(num).getUserName());
            clients.remove(num);
            for (Map.Entry<Integer, User> entry : clients.entrySet()) {
                try {
                    entry.getValue().getDos().writeUTF(clients.get(num).getUserName() + " left the server");
                    entry.getValue().getDos().flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            this.clients.remove(this.num);
        }
    }

    //---------------------------------------------------------
    public void createServer() {
        chatHistoryManager.deleteHistory();
        chatHistoryManager.deleteLog();
        chatHistoryManager.addToLog("Server started\n");
        clients = new HashMap<>();
        ServerSocket srvSocket = null;
        int countOfShowingMessages = 5;
        try {
            int i = 0; // Счётчик подключений
            // Подключение сокета к localhost
            //InetAddress ia;
            //ia = InetAddress.getByName("192.168.1.59");
            srvSocket = new ServerSocket(port);
            System.out.println("""
                    Server started\s

                    """);

            while (true) {
                // ожидание подключения
                Socket socket = srvSocket.accept();
                //получаем юсернейм
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String userName = dis.readUTF();
                System.err.println(userName + " accepted");
                chatHistoryManager.addToLog(userName + " accepted\n\tuser ID: " + i + "\nnow several counts of users: " + i + "\n");

                for (var entry : clients.entrySet()) {
                    try {
                        entry.getValue().getDos().writeUTF(userName + " accepted");
                        entry.getValue().getDos().flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                //добавляем человека в лист юзеров
                User newUser = new User();
                newUser.setUserName(userName);
                clients.put(i, newUser);

                // Стартуем обработку клиента
                // в отдельном потоке
                new ChatServer().setSocket(i++, socket, clients, chatHistoryManager);

                BufferedReader keyboard;
                keyboard = new BufferedReader(new InputStreamReader(System.in));
                String line;
                if (keyboard.ready()) {
                    line = keyboard.readLine();
                    if (line.equals("/stopServer")) {
                        chatHistoryManager.closeHistory();
                        chatHistoryManager.closeLog();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        } finally {
            try {
                if (srvSocket != null) srvSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        chatHistoryManager.addToLog("Server exited 0\n");
        System.exit(0);
    }
}