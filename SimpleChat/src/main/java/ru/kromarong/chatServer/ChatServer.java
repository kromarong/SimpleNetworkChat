package ru.kromarong.chatServer;

import ru.kromarong.chatClient.swing.PatternList;
import ru.kromarong.chatServer.auth.AuthService;
import ru.kromarong.chatServer.auth.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;


public class ChatServer {

    private AuthService authService = new AuthServiceImpl();

    private ExecutorService threadService = Executors.newCachedThreadPool();

    private Map<String, ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start(7777);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inp = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                logger.info("New client connected!");

                try {
                    String authMessage = inp.readUTF();
                    Matcher authMatcher = PatternList.AUTH_PATTERN.matcher(authMessage);
                    Matcher regMatcher = PatternList.REG_PATTERN.matcher(authMessage);
                    if (authMatcher.matches()) {
                        String username = authMatcher.group(1);
                        String password = authMatcher.group(2);
                        if (authService.authUser(username, password)) {
                            clientHandlerMap.put(username, new ClientHandler(username, socket, this));
                            out.writeUTF("/auth successful");
                            out.flush();
                            broadcastUserConnected(username);
                            logger.info("Authorization for user {} successful%n", username);
                        } else {
                            logger.error("Authorization for user {} failed%n", username);
                            out.writeUTF("/auth fails");
                            out.flush();
                            socket.close();
                        }
                    } else if (regMatcher.matches()) {
                        String user = regMatcher.group(1);
                        String pwd = regMatcher.group(2);
                        if (ConnectToDB.findUser(user)) {
                            out.writeUTF("/reg failed");
                            out.flush();
                        } else {
                            ConnectToDB.createClient(user, user, pwd);
                            clientHandlerMap.put(user, new ClientHandler(user, socket, this));
                            out.writeUTF("/reg successful");
                            out.flush();
                            broadcastUserConnected(user);
                            logger.info("Registration for user {} successful%n", user);
                        }
                    } else {
                        logger.info("Incorrect authorization message {}%n", authMessage);
                        out.writeUTF("/auth fails");
                        out.flush();
                        socket.close();
                    }

                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadService.shutdown();
        }
    }

    public void sendMessage(String userTo, String userFrom, String msg) throws IOException {
        if (userTo.startsWith("toall")){
            sendMessageToAllUser(userFrom, msg);
        } else if (clientHandlerMap.get(userTo) != null){
            ClientHandler userToClientHandler = clientHandlerMap.get(userTo);
                DataOutputStream out = new DataOutputStream(userToClientHandler.getSocket().getOutputStream());
                out.writeUTF(String.format(PatternList.MESSAGE_SEND_PATTERN, userFrom, msg));
        }else {
            logger.info("User {} not found. Message from {} is lost.%n", userTo, userFrom);
        }

    }

    public void sendMessageToAllUser(String userFrom, String msg) throws IOException {
        for(ClientHandler clientHandler : clientHandlerMap.values()){
            DataOutputStream out = new DataOutputStream(clientHandler.getSocket().getOutputStream());
            out.writeUTF(String.format(PatternList.BROADCAST_MESSAGE_SEND_PATTERN, userFrom, msg));
        }
    }


    public void sendUserList(ClientHandler clientHandler) throws IOException {
        DataOutputStream out = new DataOutputStream(clientHandler.getSocket().getOutputStream());
        for(String key : clientHandlerMap.keySet()){
            out.writeUTF(String.format(PatternList.USER_UPD_STRING, key));
        }
    }

    public void unsubscribeClient(ClientHandler clientHandler) throws IOException {
        clientHandlerMap.remove(clientHandler.getUsername());
        broadcastUserDisconnected(clientHandler.getUsername());
    }

    public void broadcastUserConnected(String username) throws IOException {
        for(ClientHandler clientHandler : clientHandlerMap.values()){
            DataOutputStream out = new DataOutputStream(clientHandler.getSocket().getOutputStream());
            out.writeUTF(String.format(PatternList.USER_CON_STRING, username));
        }
    }

    public void broadcastUserDisconnected(String username) throws IOException {
        for(ClientHandler clientHandler : clientHandlerMap.values()){
            DataOutputStream out = new DataOutputStream(clientHandler.getSocket().getOutputStream());
            out.writeUTF(String.format(PatternList.USER_DIS_STRING, username));
        }
    }

    public ExecutorService getThreadService() {
        return threadService;
    }

}
