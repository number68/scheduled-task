package com.cloud.task.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 〈鉴权属性配置〉<br> 
 *
 * @author number68
 * @date 2019/5/20
 * @since 0.1
 */
@Configuration
@Data
public class AuthProperties {
    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;
}
