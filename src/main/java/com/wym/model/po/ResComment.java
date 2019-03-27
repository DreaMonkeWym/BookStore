package com.wym.model.po;

import com.wym.model.User;
import lombok.Data;

/**
 * Created by wym on 2019-03-23 13:18
 */

@Data
public class ResComment {

    private String content;
    private User user;

}
