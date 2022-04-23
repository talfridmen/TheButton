package com.example.thebutton;

import java.util.ArrayList;

public class JsonBuilder {
    ArrayList<String[]> items;
    private static final String VALUE_SEPERATOR = ", ";

    public JsonBuilder() {
        items = new ArrayList<>();
    }

    public void addItem(String key, String value) {
        items.add(new String[]{key, "\"" + value + "\""});
    }

    public void addItem(String key, float value) {
        items.add(new String[] {key, Float.toString(value)});
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
