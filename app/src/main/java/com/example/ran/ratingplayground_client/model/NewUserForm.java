package com.example.ran.ratingplayground_client.model;

public class NewUserForm {
    private String email;
    private String username;
    private String avatarImagePath;
    private String role;

    public NewUserForm(String email , String username , String avatarImagePath , String role){
        this.email = email;
        this.username = username;
        this.avatarImagePath = avatarImagePath;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarImagePath() {
        return avatarImagePath;
    }

    public void setAvatarImagePath(String avatarImagePath) {
        this.avatarImagePath = avatarImagePath;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
