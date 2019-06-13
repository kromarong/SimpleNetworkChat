package ru.kromarong.chatClient.swing;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;


public class Log {
    private String login;
    private File file;
    private static final String LOG_DIR = "log";


    public Log(String login) {
        this.login = login;
        this.file = createFile(login);
        try{
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createFile(String login) {
        return new File(LOG_DIR, login + "-log.txt");
    }

    public void writeLog(Message msg){
        //TODO выгрузить лог в файл
        try(BufferedWriter wr = new BufferedWriter(new FileWriter(file, true))){
            wr.write(String.format(PatternList.RECORD_TEMPLATE, msg.getDate(), msg.getUserFrom(), msg.getText()));
            wr.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Message> readLog() {
        List<String> log = new ArrayList<>();

        try{
            RandomAccessFile rf = new RandomAccessFile(file, "r");
            int logLength = 100;
            StringBuilder sb = new StringBuilder();
            for(long pointer = file.length()-1; pointer >=0 && log.size() < logLength; pointer--){
                rf.seek(pointer);
                char chr = (char) rf.read();
                if (chr != '\n'){
                    sb.append(chr);
                } else if (sb.length() > 1){
                    log.add(sb.reverse().toString());
                    sb.delete(0, sb.length());
                }
            }
            if (sb.length() > 1){
                log.add(sb.reverse().toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Message> result = log.stream()
                .map(record -> {
                    Matcher matcher = PatternList.RECORD_PATTERN.matcher(record);
                    if (matcher.matches()) {
                        LocalDateTime date = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern(PatternList.DATE_FORMAT));
                        String userFrom = matcher.group(2);
                        String text = matcher.group(3);
                        return new Message(userFrom, null, text, date);
                    }
                    return new Message(null,null,null);
                }).collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }
}

