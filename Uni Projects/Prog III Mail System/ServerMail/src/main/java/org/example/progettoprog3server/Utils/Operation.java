package org.example.progettoprog3server.Utils;

public enum Operation {
    LOGIN,
    MAIL,
    UPDATE,
    DELETE,
    LOGOUT,
    READ,
    REPLY,
    REPLY_ALL,
    FORWARD;

    public static Operation fromString(String str) {
        for (Operation op : Operation.values()) {
            if (op.name().equalsIgnoreCase(str)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Invalid operation: " + str);
    }
}
