package com.rnkrsoft.gitserver.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.entity.PermissionEntity;
import com.rnkrsoft.gitserver.service.PermissionService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class FilePermissionService implements PermissionService {
    GitServer gitServer;
    Gson gson;

    public FilePermissionService(GitServer gitServer) {
        this.gitServer = gitServer;
        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }
    @Override
    public boolean hasPermission(String repositoryName, String username, String operate) {
        File permissionsHome = new File(gitServer.getRepositoriesHome(), ".permissions");
        if (!permissionsHome.exists()) {
            if (!permissionsHome.mkdirs()) {

            }
        }
        File repositoryDir = new File(permissionsHome, repositoryName);
        if (!repositoryDir.exists()) {
            if (!repositoryDir.mkdirs()) {

            }
        }
        File permissionFile = new File(repositoryDir, username + ".json");
        if (!permissionFile.exists()){
            return false;
        }
        PermissionEntity permission = null;
        try {
            String json = FileUtils.readFileToString(permissionFile, Charset.forName("UTF-8"));
            permission = gson.fromJson(json, PermissionEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (permission == null) {
            return false;
        }
        return permission.getOperates().contains(operate);
    }

    @Override
    public void grantPermission(String repositoryName, String username, String ... operates) {
        File permissionsHome = new File(gitServer.getRepositoriesHome(), ".permissions");
        if (!permissionsHome.exists()) {
            if (!permissionsHome.mkdirs()) {

            }
        }
        File repositoryDir = new File(permissionsHome, repositoryName);
        if (!repositoryDir.exists()) {
            if (!repositoryDir.mkdirs()) {

            }
        }
        File permissionFile = new File(repositoryDir, username + ".json");
        PermissionEntity permission = new PermissionEntity();
        permission.setUsername(username);
        permission.setRepositoryName(repositoryName);
        if(operates.length == 1 && "*".equals(operates[0])){
            permission.getOperates().addAll(Arrays.asList("pull", "push"));
        }else{
            permission.getOperates().addAll(Arrays.asList(operates));
        }
        String json = gson.toJson(permission);
        try {
            FileUtils.write(permissionFile, json, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void revokePermission(String repositoryName, String username, String operate) {
        File permissionsHome = new File(gitServer.getRepositoriesHome(), ".permissions");
        if (!permissionsHome.exists()) {
            if (!permissionsHome.mkdirs()) {

            }
        }
        File repositoryDir = new File(permissionsHome, repositoryName);
        if (!repositoryDir.exists()) {
            if (!repositoryDir.mkdirs()) {

            }
        }
        File permissionFile = new File(repositoryDir, username + ".json");
        PermissionEntity permission = null;
        try {
            String json = FileUtils.readFileToString(permissionFile, Charset.forName("UTF-8"));
            permission = gson.fromJson(json, PermissionEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (permission == null) {
            return;
        }
        if("*".equals(operate)){
            permission.getOperates().clear();
        }else{
            permission.getOperates().remove(operate);
        }
        String json = gson.toJson(permission);
        try {
            FileUtils.write(permissionFile, json, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PermissionEntity> listPermissions(String repositoryName) {
        return null;
    }
}
