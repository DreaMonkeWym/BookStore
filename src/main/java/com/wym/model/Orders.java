package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Orders extends OrdersKey implements Serializable {
    private String username;
}