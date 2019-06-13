package ru.kromarong.chatClient.swing;

import javax.swing.*;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.regex.Matcher;

public class Network implements Closeable {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private final MessageSender messageSender;
    private final Thread receiver;
    private Log log;

    private String username;
    private final String hostName;
    private final int port;

    public Network(String hostName, int port, MessageSender messageSender) {
        this.hostName = hostName;
        this.port = port;
        this.messageSender = messageSender;
        this.receiver = createReceiverThread();
    }

    private Thread createReceiverThread() {
        return new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = in.readUTF();
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("New message " + text);
                        Matcher private_matcher = PatternList.MESSAGE_PATTERN.matcher(text);
                        Matcher broadcast_matcher = PatternList.BROADCAST_MESSAGE_PATTERN.matcher(text);
                        if (private_matcher.matches()) {

                            privateMessageProcessing(private_matcher);

                        } else if (broadcast_matcher.matches()){

                            broadcastMessageProcessing(broadcast_matcher);

                        } else if (text.startsWith("/usercon")){
                            private_matcher = PatternList.USER_CON_PATTERN.matcher(text);
                            if (private_matcher.matches()) {
                                messageSender.userConnected(private_matcher.group(1));
                            }
                        } else if (text.startsWith("/userdis")){
                            private_matcher = PatternList.USER_DIS_PATTERN.matcher(text);
                            if (private_matcher.matches()) {
                                messageSender.userDisconnected(private_matcher.group(1));
                            }

                        } else if (text.startsWith("/userupd")){
                            private_matcher = PatternList.USER_UPD_PATTERN.matcher(text);
                            if (private_matcher.matches()) {
                                if (!private_matcher.group(1).equals(username)) {
                                    messageSender.userConnected(private_matcher.group(1));
                                }
                            }
                        }
                    });
                }catch (SocketException e){
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void broadcastMessageProcessing(Matcher broadcast_matcher) {
        Message msg = new Message(broadcast_matcher.group(1), username,
                broadcast_matcher.group(2));
        log.writeLog(msg);
        messageSender.submitMessage(msg);
    }

    private void privateMessageProcessing(Matcher private_matcher) {
        if (private_matcher.group(1).equals(username)){
            return;
        } else {
            Message msg = new Message(private_matcher.group(1) + " шепчет:", username,
                    private_matcher.group(2));
            log.writeLog(msg);
            messageSender.submitMessage(msg);
        }
    }

    public void sendMessageToUser(Message message) {
        sendMessage(String.format(PatternList.MESSAGE_SEND_PATTERN, message.getUserTo(), message.getText()));
        log.writeLog(message);
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authorize(String username, String password) throws IOException {
        socket = new Socket(hostName, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        out.writeUTF(String.format(PatternList.AUTH_STRING, username, password));
        String response = in.readUTF();
        if (response.equals("/auth successful")) {
            this.username = username;
            this.log = new Log(username);
            receiver.start();
        } else {
            throw new AuthException();
        }
    }

    public void registration(String username, String password) throws IOException {
        socket = new Socket(hostName, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        out.writeUTF(String.format(PatternList.REG_STRING, username, password));
        String response = in.readUTF();
        if (response.equals("/reg successful")) {
            this.username = username;
            receiver.start();
        } else {
            throw new RegistrationException();
        }
    }

    public String getUsername() {
        return username;
    }

    public void getUserList() throws IOException {
        out.writeUTF("/getUserList");
    }

    public List<Message> getLog(){
        return log.readLog();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        receiver.interrupt();
        try {
            receiver.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
