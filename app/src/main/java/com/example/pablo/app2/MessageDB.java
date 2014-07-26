package com.example.pablo.app2;

import com.orm.SugarRecord;

public class MessageDB extends SugarRecord<MessageDB> {
    String username;
    String tousername;
    String cid;
    String message;
    String team;
    public MessageDB(){
    }

    public MessageDB(String username, String to, String message, String team, String cid) {
        super();
        this.username = username;
        this.tousername = to;
        this.message = message;
        this.team = team;
        this.cid = cid;
    }

}