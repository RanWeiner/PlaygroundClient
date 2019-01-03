package com.example.ran.ratingplayground_client.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserTO implements Serializable {
    private String email;
    private String playground;
    private String username;
    private String avatar;
    private String role;
    private long points;
    private int code;


    public UserTO(){

    }

    public UserTO(String email, String playground, String username, String avatar, String role, long points){
        this.email = email;
        this.playground = playground;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlayground() {
        return playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    // Decodes business json into business model object
    public static UserTO fromJson(JSONObject jsonObject) {
        UserTO u = new UserTO();

        // Deserialize json into object fields
        try {
            u.email = jsonObject.getString("email");
            u.playground = jsonObject.getString("playground");
            u.role = jsonObject.getString("role");
            u.avatar = jsonObject.getString("avatar");
            u.username = jsonObject.getString("username");
            u.points = jsonObject.getLong("points");
            u.code = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return u;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", this.email);
            jsonObject.put("playground", this.playground);
            jsonObject.put("username", this.username);
            jsonObject.put("role", this.role);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
}
