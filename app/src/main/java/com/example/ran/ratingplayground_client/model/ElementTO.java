package com.example.ran.ratingplayground_client.model;

import com.example.ran.ratingplayground_client.utils.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ElementTO implements Serializable {

    private String playground;
    private String id;
    private double x;
    private double y;
    private String name;
    private Date creationDate;
    private Date expirationDate;
    private String type;
    private Map<String, Object> attributes;
    private String creatorPlayground;
    private String creatorEmail;



    public ElementTO(){

    }

    public ElementTO(String playground, String id, Double x, Double y, String name, Date creationDate,
                     Date expirationDate, String type, Map<String, Object> attributes, String creatorPlayground,
                     String creatorEmail) {
        super();
        this.playground = playground;
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.type = type;
        this.attributes = attributes;
        this.creatorPlayground = creatorPlayground;
        this.creatorEmail = creatorEmail;
    }


    public double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public static ElementTO fromJson(JSONObject json) {
        ElementTO e = new ElementTO();
        try {

            e.playground = json.getString("playground");
            e.id = json.getString("id");
            e.name = json.getString("name");
            e.type = json.getString("type");;

            JSONObject location = json.getJSONObject("location");
            e.x = location.getDouble("x");
            e.y = location.getDouble("y");

            e.creatorPlayground = json.getString("creatorPlayground");
            e.creatorEmail = json.getString("creatorEmail");

        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }

        return e;
    }


    public void setPlayground(String playground) {
        this.playground = playground;
    }



    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getPlayground() {
        return this.playground;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCreatorPlayground() {
        return creatorPlayground;
    }

    public void setCreatorPlayground(String creatorPlayground) {
        this.creatorPlayground = creatorPlayground;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        JSONObject locationObject = new JSONObject();
        try {
            jsonObject.put("name", this.name);
            jsonObject.put("type", this.type);
            jsonObject.put("playground", this.playground);
            locationObject.put("x" , this.x);
            locationObject.put("y" , this.y);
            jsonObject.put("location" , locationObject);
//            jsonObject.put("expirationDate", this.expirationDate.toString());
            jsonObject.put("creatorEmail", this.creatorEmail);
            jsonObject.put("creatorPlayground", this.creatorPlayground);
            jsonObject.put("moreAttributes", this.attributes);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void setLocation(double x, double y) {
        this.x = x ;
        this.y = y;
    }
}
