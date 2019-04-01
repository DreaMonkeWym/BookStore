package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BookType implements Serializable {

    private String typeid;
    private String typename;

}