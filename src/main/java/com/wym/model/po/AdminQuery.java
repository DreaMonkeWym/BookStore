package com.wym.model.po;

import com.wym.model.BookDetail;
import lombok.Data;

import java.util.List;

/**
 * Created by wym on 2019-03-25 17:11
 */

@Data
public class AdminQuery {

    String typeName;
    List<AdminBook> adminBookList;
}
