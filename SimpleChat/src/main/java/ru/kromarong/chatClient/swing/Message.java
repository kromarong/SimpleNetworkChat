package ru.kromarong.chatClient.swing;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {

    private String userFrom;

    private String userTo;

    private String text;

    private LocalDateTime date;

    public Message(String userFrom, String userTo, String text) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.text = text;
        this.date = LocalDateTime.now();
    }

    public Message(String userFrom, String userTo, String text, LocalDateTime date) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.text = text;
        this.date = date;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern(PatternList.DATE_FORMAT));
    }
}
