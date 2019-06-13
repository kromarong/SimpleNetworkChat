package ru.kromarong.chatServer.auth;

import ru.kromarong.chatServer.ConnectToDB;

import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    public AuthServiceImpl() {

    }

    @Override
    public boolean authUser(String username, String password) throws SQLException {
        return ConnectToDB.findUser(username, password);
    }

}

