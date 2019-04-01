package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-03-26 20:01
 */

@Data
public class CartDetail implements Serializable {

    private String cartId;
    private String bookId;
    private String bookName;
    private String bookAvatar;
    private String bookPrice;
    private String bookQuantity;
    private String quantity;
}
