package com.example.portraitjava1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonChange{

    protected JSONObject StringTOJson(String string) {
        try {
            return new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String JsonGetValue(JSONObject json,String jsonKey) {
        try {
            return json.getString(jsonKey);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected JSONArray changeJsonArray(String string) {
        try {
            return new JSONArray(string);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
