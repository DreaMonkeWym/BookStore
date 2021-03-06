package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-03-23 12:17
 */
@Data
public class BookByType implements Serializable {

    private String bookid;
    private String avatar;
    private String price;
    private String publicationtime;
    private String author;
    private String glance;
    private String bookname;

}
