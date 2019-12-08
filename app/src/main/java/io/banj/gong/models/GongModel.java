package io.banj.gong.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class GongModel {

//    {
//        "id": int,
//        "from": User,
//            "to": User,
//            "createdAt": Unix Timestamp,
//        "nicedAt": Unix Timestamp or null
//    }


    int id;
    String from;
    String to;
    Date createdAt, nicedAt = null;

    public static ArrayList<GongModel> gongs = new ArrayList<>();


    public GongModel(int id, String from, String to, int createdAtInUnix) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
    }

    public static void staticGenerateFakeData() {

        gongs = new ArrayList<>();
        gongs.add(new GongModel(1, "😍SJohanson", "me", 1372339860));
        gongs.add(new GongModel(2, "Coarse🍒Kosher", "me", 1372339860));
        gongs.add(new GongModel(3, "Snapchatthottie123", "me", 1372339860));
        gongs.add(new GongModel(4, "TwitchBiddie🍑", "me", 1372339860));

    }




}
