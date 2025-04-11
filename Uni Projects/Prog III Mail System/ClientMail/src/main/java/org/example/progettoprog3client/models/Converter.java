package org.example.progettoprog3client.models;
import org.example.progettoprog3client.utils.CommunicationStatus;
import org.example.progettoprog3client.utils.Operation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public class Converter {
    private final JSONObject data;

    public Converter (String jsonString){
        data = new JSONObject(jsonString);
    }

    public Converter(String operation, String mailAddr){
        data = new JSONObject();

        data.put("operation", operation);
        data.put("email", mailAddr);
    }

    public Converter(String operation, String from, Collection<String> to, String oggetto, String contenuto){
        data = new JSONObject();

        data.put("operation", operation);
        data.put("from", from);
        data.put("to", to);
        data.put("oggetto", oggetto);
        data.put("contenuto", contenuto);
    }

    public Converter(String email, int id, Operation operation){
        data = new JSONObject();
        data.put("email", email);
        data.put("id", id);
        data.put("operation", operation);
    }

    public CommunicationStatus getStatus(){
        String status = data.getString("status");
        return CommunicationStatus.fromString(status);
    }

    public JSONArray getJSONArray (){
        return data.getJSONArray("emails");
    }

    @Override
    public String toString(){
        return data.toString();
    }

}
