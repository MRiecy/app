package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AdvertContent {
    @PrimaryKey
    private int id;
    private String content;

    public AdvertContent(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "AdvertContent{" +
                "Id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
