package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecentView implements Serializable {

    private String username;
    private String typeid;

}