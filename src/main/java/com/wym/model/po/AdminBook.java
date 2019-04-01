package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-03-25 17:51
 */
@Data
public class AdminBook implements Serializable {

    private String bookid;
    private String avatar;
    private String price;
    private String quantity;
    private String bookname;

}
