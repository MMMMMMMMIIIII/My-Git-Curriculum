package org.example.progettoprog3client.models;

import org.example.progettoprog3client.utils.Operation;

public class OverlayModel {
    private static Operation op;

    public static Operation getOp() {
        return op;
    }

    public static void setOp(Operation operation) {
        op = operation;
    }
}
