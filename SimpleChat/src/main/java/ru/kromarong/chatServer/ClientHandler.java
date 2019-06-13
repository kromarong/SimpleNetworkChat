package ru.kromarong.chatServer;

import ru.kromarong.chatClient.swing.PatternList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Future;
import java.util.regex.Matcher;

public class ClientHandler {

    private final Future<?> handleThread;
    private final DataInputStream inp;
    private final DataOutputStream out;
    private final ChatServer server;
    private final String username;
    private final Socket socket;


    public ClientHandler(String username, Socket socket, ChatServer server) throws IOException {
        this.username = username;
        this.socket = socket;
        this.server = server;
        this.inp = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        this.handleThread = server.getThreadService().submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String msg = inp.readUTF();
                    System.out.printf("Message from user %s: %s%n", username, msg);
                    Matcher matcher = PatternList.MESSAGE_PATTERN.matcher(msg);
                    if (matcher.matches()) {
                        String userTo = matcher.group(1);
                        String message = matcher.group(2);
                        server.sendMessage(userTo, username, message);
                    }
                    if (msg.equals("/getUserList")){
                        server.sendUserList(ClientHandler.this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.printf("Client %s disconnected%n", username);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    server.unsubscribeClient(ClientHandler.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //handleThread.start();
    }

//    public void sendMessage(String userFrom, String msg) throws IOException {
//        out.writeUTF(String.format(PatternList.MESSAGE_SEND_PATTERN, userFrom, msg));
//    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

}