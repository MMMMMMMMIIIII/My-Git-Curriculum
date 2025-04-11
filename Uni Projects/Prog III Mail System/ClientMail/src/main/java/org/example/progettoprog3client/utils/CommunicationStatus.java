package org.example.progettoprog3client.utils;

public enum CommunicationStatus {
    SUCCESS,
    FAILURE,
    NULL,
    Error,
    ALREADY_LOGGED;

    // Metodo per ottenere l'Enum dalla stringa
    public static CommunicationStatus fromString(String str) {
        for (CommunicationStatus response : CommunicationStatus.values()) {
            if (response.name().equalsIgnoreCase(str)) {
                return response;
            }
        }
        throw new IllegalArgumentException("Invalid response: " + str);
    }
}
