package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-04-02 17:48
 */
@Data
public class OrderDetail implements Serializable {

    private String cartId;
    private String bookId;
    private String bookName;
    private String avatar;
    private String price;
    private String quantity;
    private String amount;
}
