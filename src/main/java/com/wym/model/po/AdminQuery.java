package com.wym.model.po;

import com.wym.model.BookDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wym on 2019-03-25 17:11
 */

@Data
public class AdminQuery implements Serializable {

    String typeName;
    List<AdminBook> adminBookList;
}
