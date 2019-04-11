package com.wym.model.po;

import com.wym.model.Comment;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wym on 2019-03-23 13:03
 */
@Data
public class ResBookDetail implements Serializable {

    private String avatar;
    private String descri;
    private String price;
    private String publicationtime;
    private String author;
    private String glance;
    private String bookname;
    private String quantity;
    private String typeId;
    private List<ResComment> commentList;

}
