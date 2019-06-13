package ru.kromarong.chatServer.auth;

import java.sql.SQLException;

public interface AuthService {
    boolean authUser(String username, String password) throws SQLException, ClassNotFoundException;
}
