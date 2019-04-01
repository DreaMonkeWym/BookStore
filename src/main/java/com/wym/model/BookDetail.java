package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BookDetail implements Serializable {

    private String bookid;
    private String typeid;
    private String avatar;
    private String descri;
    private String isbn;
    private String price;
    private String quantity;
    private String publicationtime;
    private String author;
    private String soldout;
    private String glance;
    private String bookname;

}