package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class User {
    private String userName;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataInputStream disFromGraphic;
    private DataOutputStream dos;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDisFromGraphic(DataInputStream disFromGraphic) {
        this.disFromGraphic = disFromGraphic;
    }

    public DataInputStream getDisFromGraphic() {
        return disFromGraphic;
    }
}
