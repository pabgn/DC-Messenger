package com.example.pablo.app2;

import com.orm.SugarRecord;

public class FavoriteDB extends SugarRecord<FavoriteDB> {
    String username;
    String cid;
    String message;
    String team;
    public FavoriteDB(){
    }

    public FavoriteDB(String username, String message, String team, String cid) {
        super();
        this.username = username;
        this.message = message;
        this.team = team;
        this.cid = cid;
    }

}