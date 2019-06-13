package ru.kromarong.chatClient.swing;

import java.util.regex.Pattern;

public class PatternList {
    public static final String AUTH_STRING = "/auth %s %s";
    public static final String REG_STRING = "/reg %s %s";
    public static final String MESSAGE_SEND_PATTERN = "/w %s %s";
    public static final String BROADCAST_MESSAGE_SEND_PATTERN = "/e %s %s";
    public static final String USER_CON_STRING = ("/usercon %s");
    public static final String USER_DIS_STRING = ("/userdis %s");
    public static final String USER_UPD_STRING = ("/userupd %s");
    public static final String RECORD_TEMPLATE = "%s|%s|%s";
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final Pattern MESSAGE_PATTERN = Pattern.compile("^/w (\\w+) (.+)", Pattern.MULTILINE);
    public static final Pattern BROADCAST_MESSAGE_PATTERN = Pattern.compile("^/e (\\w+) (.+)", Pattern.MULTILINE);
    public static final Pattern USER_CON_PATTERN = Pattern.compile("^/usercon (.+)$");
    public static final Pattern USER_DIS_PATTERN = Pattern.compile("^/userdis (.+)$");
    public static final Pattern USER_UPD_PATTERN = Pattern.compile("^/userupd (.+)$");
    public static final Pattern AUTH_PATTERN = Pattern.compile("^/auth (\\w+) (\\w+)$");
    public static final Pattern REG_PATTERN = Pattern.compile("^/reg (\\w+) (\\w+)$");
    public static final Pattern RECORD_PATTERN = Pattern.compile("(\\S+\\s\\S+)\\|(\\w+)\\|(.*)");

}
