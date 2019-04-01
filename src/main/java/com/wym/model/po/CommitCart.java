package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wym on 2019-04-01 19:26
 */
@Data
public class CommitCart implements Serializable {

    String username;
    List<CartDetail> cartDetailList;

}
