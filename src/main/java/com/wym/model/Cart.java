package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Cart implements Serializable {

    private String cartid;
    private String username;
    private String bookid;
    private String quantity;
    private Boolean payment;

}