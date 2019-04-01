package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Comment implements Serializable {

    private String commentid;
    private String bookid;
    private String content;
    private String username;

}