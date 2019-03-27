package com.wym.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by wym on 2019-03-19 20:46
 */
public class MD5Util {

    public static String encryptMD5(String str) {
        return DigestUtils.md5Hex(str);
    }
}
