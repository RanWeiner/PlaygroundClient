package com.example.ran.ratingplayground_client.model;

import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.Parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ActivityTO {

    private String playground;
    private String id;
    private String elementPlayground;
    private String elementId;
    private String type;
    private String playerPlayground;
    private String playerEmail;
    private Map<String,Object> attributes;

    public ActivityTO() {}

    public ActivityTO(String playground, String elementPlayground, String elementId, String type,
                      String playerPlayground, String playerEmail, Map<String, Object> attributes) {
        this.playground = playground;
        this.elementPlayground = elementPlayground;
        this.elementId = elementId;
        this.type = type;
        this.playerPlayground = playerPlayground;
        this.playerEmail = playerEmail;
        this.attributes = attributes;
    }

    public ActivityTO(String playground,String id, String elementPlayground, String elementId, String type,
                      String playerPlayground, String playerEmail, Map<String, Object> attributes) {
        this.playground = playground;
        this.id = id;
        this.elementPlayground = elementPlayground;
        this.elementId = elementId;
        this.type = type;
        this.playerPlayground = playerPlayground;
        this.playerEmail = playerEmail;
        this.attributes = attributes;
    }

    public String getPlayground() {
        return playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElementPlayground() {
        return elementPlayground;
    }

    public void setElementPlayground(String elementPlayground) {
        this.elementPlayground = elementPlayground;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlayerPlayground() {
        return playerPlayground;
    }

    public void setPlayerPlayground(String playerPlayground) {
        this.playerPlayground = playerPlayground;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        JSONObject attributes = new JSONObject();
        try {
            jsonObject.put("playground", this.playground);
            jsonObject.put("elementPlayground", this.elementPlayground);
            jsonObject.put("elementId", this.elementId);
            jsonObject.put("type", this.type);
            jsonObject.put("playerPlayground" , this.playerPlayground);
            jsonObject.put("playerEmail" , this.playerEmail);

            JSONObject json = new JSONObject(this.attributes);
            jsonObject.put("attributes",json );

//            //just handle post on billaboard
//            if (this.type.equals(AppConstants.BILLBOARD_TYPE)) {
//
////                attributes.put("post" , this.attributes.get("post"));
////                attributes.put("year" , this.attributes.get("year"));
////                attributes.put("user" , this.attributes.get("user"));
//
//            } else {
//
//            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    public static ActivityTO fromJson(JSONObject jsonObject) {
        ActivityTO activityTO = new ActivityTO();

        // Deserialize json into object fields
        try {
            activityTO.setPlayground(jsonObject.getString("playground"));
            activityTO.setId(jsonObject.getString("id"));
            activityTO.setElementPlayground(jsonObject.getString("elementPlayground"));
            activityTO.setElementId(jsonObject.getString("elementId"));
            activityTO.setType(jsonObject.getString("type"));
            activityTO.setPlayerPlayground(jsonObject.getString("playerPlayground"));
            activityTO.setPlayerEmail(jsonObject.getString("playerEmail"));

            activityTO.setAttributes(Parser.toMap(jsonObject.getJSONObject("attributes")));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return activityTO;
    }


}
