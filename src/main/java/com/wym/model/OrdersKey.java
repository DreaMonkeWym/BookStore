package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersKey implements Serializable {

    private String orderid;
    private String cartid;

}