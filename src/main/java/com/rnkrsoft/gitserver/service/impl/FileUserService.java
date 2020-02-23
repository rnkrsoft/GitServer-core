package com.rnkrsoft.gitserver.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.utils.PasswordUtils;
import com.rnkrsoft.gitserver.entity.UserEntity;
import com.rnkrsoft.gitserver.service.UserService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileUserService implements UserService {
    GitServer gitServer;
    Gson gson;

    public FileUserService(GitServer gitServer) {
        this.gitServer = gitServer;
        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    @Override
    public void registerUser(String username, String email, String passwordSha1) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordSha1);
        String json = gson.toJson(user);
        File usersHome = new File(gitServer.getRepositoriesHome(), ".users");
        File userFile = new File(usersHome, username + ".json");
        if (!usersHome.exists()) {
            if (!usersHome.mkdirs()) {
            }
        }
        try {
            FileUtils.write(userFile, json, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(String username, String email, String password) {
        File usersHome = new File(gitServer.getRepositoriesHome(), ".users");
        File userFile = new File(usersHome, username + ".json");
        if (!usersHome.exists()) {
            if (!usersHome.mkdirs()) {

            }
        }
        UserEntity user = null;
        try {
            String json = FileUtils.readFileToString(userFile, Charset.forName("UTF-8"));
            user = gson.fromJson(json, UserEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user == null) {
            //fixme
        }

        user.setEmail(email);
        user.setEmail(PasswordUtils.generateSha1(password));
        String json = gson.toJson(user);
        try {
            FileUtils.write(userFile, json, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(String username) {
        File usersHome = new File(gitServer.getRepositoriesHome(), ".users");
        File userFile = new File(usersHome, username + ".json");
        userFile.delete();
    }

    @Override
    public List<UserEntity> listUsers() {
        File usersHome = new File(gitServer.getRepositoriesHome(), ".users");
        File[] usersFile = usersHome.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toUpperCase().endsWith(".json");
            }
        });
        //fixme
        return new ArrayList();
    }

    @Override
    public boolean hasUser(String username) {
        File usersHome = new File(gitServer.getRepositoriesHome(), ".users");
        File userFile = new File(usersHome, username + ".json");
        if (!usersHome.exists()) {
            if (!usersHome.mkdirs()) {

            }
        }
        return userFile.exists();
    }

    @Override
    public boolean hasAuthority(String username, String passwordSha1) {
        File usersHome = new File(gitServer.getRepositoriesHome(), ".users");
        File userFile = new File(usersHome, username + ".json");
        if (!usersHome.exists()) {
            if (!usersHome.mkdirs()) {

            }
        }
        UserEntity user = null;
        try {
            String json = FileUtils.readFileToString(userFile, Charset.forName("UTF-8"));
            user = gson.fromJson(json, UserEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(passwordSha1);
    }
}
