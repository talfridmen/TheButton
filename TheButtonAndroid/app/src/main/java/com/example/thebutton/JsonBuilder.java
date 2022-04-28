package com.example.thebutton;

import java.util.ArrayList;

public class JsonBuilder {
    ArrayList<String[]> items;
    private static final String VALUE_SEPERATOR = ", ";

    public JsonBuilder() {
        items = new ArrayList<>();
    }

    public JsonBuilder addItem(String key, String value) {
        items.add(new String[]{key, "\"" + value + "\""});
        return this;
    }

    public JsonBuilder addItem(String key, double value) {
        items.add(new String[] {key, Double.toString(value)});
        return this;
    }

    public String build() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        for (String[] item : items) {
            json.append(getKeyValueInJsonFormat(item[0], item[1]));
            if(item != items.get(items.size() - 1)) {
                json.append(VALUE_SEPERATOR);
            }
        }
        json.append("}");
        return json.toString();
    }

    private String getKeyValueInJsonFormat(String key, String value) {
        return "\"" + key + "\": " + value;
    }
}
