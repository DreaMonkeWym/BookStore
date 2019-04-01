package com.wym.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-03-24 16:52
 */

@Data
public class BookRecommend implements Serializable {

    private String bookid;
    private String avatar;
    private String bookname;
}
