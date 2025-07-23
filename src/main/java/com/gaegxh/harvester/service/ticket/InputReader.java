package com.gaegxh.harvester.service.ticket;

public interface InputReader {
    String readString(String prompt);
    int readInt(String prompt, int min, int max);
    boolean readBoolean(String prompt);
}