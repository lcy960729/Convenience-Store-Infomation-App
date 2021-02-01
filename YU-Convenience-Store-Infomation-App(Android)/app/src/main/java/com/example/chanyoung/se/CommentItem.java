package com.example.chanyoung.se;

public class CommentItem {
    String bid, id, cwriter, ccontent, cdate;

    CommentItem(String bid, String id, String cwriter, String ccontent, String cdate) {
        this.bid = bid;
        this.id = id;
        this.cwriter = cwriter;
        this.ccontent = ccontent;
        this.cdate = cdate;
    }
}