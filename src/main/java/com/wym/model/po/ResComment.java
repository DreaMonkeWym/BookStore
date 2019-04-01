package com.wym.model.po;

import com.wym.model.User;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wym on 2019-03-23 13:18
 */

@Data
public class ResComment implements Serializable {

    private String content;
    private User user;

}
