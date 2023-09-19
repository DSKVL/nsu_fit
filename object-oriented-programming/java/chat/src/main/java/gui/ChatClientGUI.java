package Graphic;

import Client.ChatClient;
import Server.ChatHistoryManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClientGUI extends JFrame {
    private final JButton clientButton;
    private JTextField textField1, textField2, textField3;
    private final JTextArea messagesTextArea;
    private final JTextField textInputField;
    String text = "";
    TextContainer textContainer = new TextContainer();
    private final ChatHistoryManager chatHistoryManager;

    public ChatClientGUI() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание кнопки
        JButton serverButton = new JButton("start the server!");
        clientButton = new JButton("join a server!");
        serverButton.setVisible(false);

        messagesTextArea = new JTextArea(10,19);
        messagesTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(messagesTextArea);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        textInputField = new JTextField();
        textInputField.setPreferredSize(new Dimension(227, 50));

        serverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //clientButton.setVisible(false);//FIXME здесь кнопка вторая про другой режим удаляется слишком рано (чек)

                // Создание панели для ввода текста
                JPanel panel = new JPanel();
                panel.add(new JLabel("write the server's port"));
                textField1 = new JTextField(10);
                panel.add(textField1);

                // Создание диалогового окна для ввода текста
                int result = JOptionPane.showConfirmDialog(ChatClientGUI.this, panel, "creating the server", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // Создание нового экземпляра класса А и вызов его метода
                    //A a = new A();
                    //a.printText(textField1.getText());

                    //new Server(textField1.getText());
                }
            }
        });

        chatHistoryManager = new ChatHistoryManager();
        clientButton.addActionListener(e -> {
            // Создание панели для ввода текста
            JPanel panel = new JPanel();
            panel.add(new JLabel("userName"));
            textField1 = new JTextField(10);
            panel.add(textField1);
            panel.add(new JLabel("server ip"));
            textField2 = new JTextField(10);
            panel.add(textField2);
            panel.add(new JLabel("server port"));
            textField3 = new JTextField(10);
            panel.add(textField3);

            // Создание диалогового окна для ввода текста
            int result = JOptionPane.showConfirmDialog(ChatClientGUI.this, panel, "joining the server", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                clientButton.setVisible(false);
                Thread daemonThread = new Thread(new Runnable() {
                    public void run() {

                        ChatClient chatClient = new ChatClient(
                                textField1.getText(),
                                textField2.getText(),
                                Integer.parseInt(textField3.getText()),
                                messagesTextArea, true,
                                textContainer, chatHistoryManager);
                        chatClient.startClient();
                        messagesTextArea.append("подключение успешно!\n");

                    }
                });
                daemonThread.setDaemon(true);
                daemonThread.start();
//                    clientButton.setVisible(false);
//                    String name=textField1.getText();
//                    getContentPane().setName(name);
            }
        });
        textInputField.addActionListener(e -> {
            text = textInputField.getText(); // получение введенного текста
            textInputField.setText(""); // очистка поля ввода
            textContainer.setText(text);
            if(text.equals("/quit"))
                textInputField.setEditable(false);
        });

        // Добавление компонентов на панель
        JPanel panel = new JPanel();
        //panel.add(serverButton);
        clientButton.setPreferredSize(new Dimension(150, 50));
        panel.add(clientButton);
        messagesTextArea.append("окно чата:\n  ожидаю подключения...\n");
        panel.add(scrollPane);
        panel.add(textInputField);

        // Добавление панели на окно
        getContentPane().add(panel);

        // Настройка размеров окна и его видимости
        setSize(300, 400);
        setVisible(true);

        //while (true)
        //System.out.println(text);
    }

    public static void main(String[] args) {
        new ChatClientGUI();
    }

    public void setNewTextToTextArea(String text) {

    }
}