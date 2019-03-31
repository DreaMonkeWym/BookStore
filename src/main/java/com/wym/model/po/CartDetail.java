package com.wym.model.po;

import lombok.Data;

/**
 * Created by wym on 2019-03-26 20:01
 */

@Data
public class CartDetail {

    String cartId;
    String bookId;
    String bookName;
    String bookAvatar;
    String bookPrice;
    String bookQuantity;
    String quantity;
}
