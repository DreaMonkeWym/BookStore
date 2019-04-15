package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-04-14 14:33
 */

@Data
public class ResUser implements Serializable {

    private String username;
    private Boolean isRoot;

}
