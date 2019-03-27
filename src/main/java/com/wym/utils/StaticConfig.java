package com.wym.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by wym on 2019-03-20 17:49
 */
@Configuration
@PropertySource(value = "classpath:static-config.properties", encoding = "UTF-8")
@Data
public class StaticConfig {

    @Value("${file.path}")
    private String filePath;

}