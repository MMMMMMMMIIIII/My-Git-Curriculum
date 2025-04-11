package org.example.progettoprog3server.model;

import org.example.progettoprog3server.Utils.CommunicationStatus;
import org.example.progettoprog3server.Utils.Operation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// classe converter per convertire i vari messaggi in oggetti JSON
public class Converter {
    private JSONObject data;

    public Converter(String jsonString){
        data = new JSONObject(jsonString);
    }

    public Converter(CommunicationStatus status){
        data = new JSONObject();

        data.put("status", status.name());
    }

    public Converter(Collection<Mail> listMail, CommunicationStatus status){
        JSONArray array = new JSONArray();
        data = new JSONObject();

        data.put("status", status.name());

        for(Mail tmp : listMail){
            JSONObject mailJson = new JSONObject();
            mailJson.put("id", tmp.getId());
            mailJson.put("from", tmp.getMittente());
            mailJson.put("to", new JSONArray(tmp.getDestinatario()));
            mailJson.put("oggetto", tmp.getOggetto());
            mailJson.put("contenuto", tmp.getContenuto());
            mailJson.put("dataOra", tmp.getDateAndTime());
            mailJson.put("isRead", tmp.getIsRead());

            array.put(mailJson);
        }

        data.put("emails", array);

    }

    public Converter(Mail mail) {
        data = new JSONObject();

        data.put("id", mail.getId());
        data.put("from", mail.getMittente());
        data.put("to", new JSONArray(mail.getDestinatario()));
        data.put("oggetto", mail.getOggetto());
        data.put("contenuto", mail.getContenuto());
        data.put("dataOra", mail.getDateAndTime());
        data.put("isRead", mail.getIsRead());
    }

    public Converter(){}


    public Operation getOperation() {
        String operationStr = data.getString("operation");
        return Operation.fromString(operationStr); // Converte la stringa nell'Enum
    }

    public String getEmail() {
        return data.getString("email");
    }

    public String getFrom(){
        return data.getString("from");
    }

    public List<String> getTo() {
        JSONArray jsonArray = data.getJSONArray("to");
        return jsonArray.toList().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public String getOggetto(){
        return data.getString("oggetto");
    }

    public String getContenuto(){
        return data.getString("contenuto");
    }

    public int getId(){ return data.getInt("id"); }


    // metodo per trasformare un JSONArray di JSONObject in una lista di mail
    public Collection<Mail> getMails(JSONArray jsonMails) {
        Collection<Mail> emails = new ArrayList<>();

        for (int i = 0; i < jsonMails.length(); i++) {
            try {
                JSONObject jsonMail = jsonMails.getJSONObject(i);

                Mail mail = new Mail();
                mail.setId(jsonMail.getInt("id"));
                mail.setOggetto(jsonMail.getString("oggetto"));
                mail.setContenuto(jsonMail.getString("contenuto"));
                mail.setMittente(jsonMail.getString("from"));
                mail.setDateAndTime(jsonMail.getString("dataOra"));
                mail.setIsRead(jsonMail.getBoolean("isRead"));

                // Parsing dei destinatari
                JSONArray destinatariJson = jsonMail.getJSONArray("to");
                List<String> destinatari = new ArrayList<>();
                if (destinatariJson != null) {
                    for (int j = 0; j < destinatariJson.length(); j++) {
                        destinatari.add(destinatariJson.optString(j, ""));
                    }
                }
                mail.setDestinatario(destinatari);

                emails.add(mail);
            } catch (JSONException e) {
                System.err.println("Errore durante la lettura di una mail: " + e.getMessage());
            }
        }

        return emails;
    }

    @Override
    public String toString(){
        return data.toString();
    }

}
