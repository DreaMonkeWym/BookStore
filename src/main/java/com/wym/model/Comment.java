package com.wym.model;

import lombok.Data;

@Data
public class Comment {

    private String commentid;
    private String bookid;
    private String content;
    private String username;

}