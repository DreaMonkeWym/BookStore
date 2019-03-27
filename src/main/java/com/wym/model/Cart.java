package com.wym.model;

import lombok.Data;

@Data
public class Cart {

    private String cartid;
    private String username;
    private String bookid;
    private String quantity;
    private Boolean payment;

}