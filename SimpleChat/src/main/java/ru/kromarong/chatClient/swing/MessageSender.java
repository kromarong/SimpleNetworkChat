package ru.kromarong.chatClient.swing;

public interface MessageSender {

    void submitMessage(Message msg);

    void userConnected(String username);

    void userDisconnected(String username);
}
