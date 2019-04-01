package com.wym.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Admin implements Serializable {

    private String adminname;
    private String adminpassword;
    private String avatar;

}