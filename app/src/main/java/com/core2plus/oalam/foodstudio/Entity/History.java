package com.core2plus.oalam.foodstudio.Entity;

/**
 * Created by zhakhanyergali on 21.11.17.
 */

public class History {

    private int id;
    private String date;
    private String context;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContext() {
        return context;
    }

    public String getURl() {

        String text = context;
        String[] split = text.split(";");
        String[] arr = new String[2];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[0] != null) {
            //System.out.println(arr[1]);
            return arr[0];
        } else {
            return "Error occured!";
        }
    }

    public String getText() {

        String text = context;
        String[] split = text.split(";");
        String[] arr = new String[2];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[1] != null) {
            //System.out.println(arr[1]);
            return arr[1];
        } else {
            return "Error occured!";
        }

    }

    public String getSuggestedPrice() {

        String text = context;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[2] != null) {
            //System.out.println(arr[1]);
            return arr[2];
        } else {
            return "Error occured!";
        }

    }

    public void setContext(String context) {
        this.context = context;
    }
}
