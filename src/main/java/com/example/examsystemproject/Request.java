package com.example.examsystemproject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private RequestType type;
    private final Map<String, String> data = new HashMap<>();

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Map<String, String> getData() {
        return data;
    }
}

