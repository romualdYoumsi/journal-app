package com.alc.ymrj.journalapp.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by netserve on 27/06/2018.
 */

@IgnoreExtraProperties
public class Entry {
    public String title;
    public String content;
    public String firebaseKey;
    public Long date;

    public Entry() {
    }

    public Entry(String title, String content, Long date, String firebaseKey) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.firebaseKey = firebaseKey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("firebaseKey", firebaseKey);
        result.put("date", date);

        return result;
    }
}
