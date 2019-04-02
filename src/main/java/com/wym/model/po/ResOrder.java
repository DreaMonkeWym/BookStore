package com.wym.model.po;

import lombok.Data;

import java.util.List;

/**
 * Created by wym on 2019-04-02 17:52
 */
@Data
public class ResOrder {

    private String orderId;
    private List<OrderDetail> orderDetailList;

}
