package com.knowledge.base.userauth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.userauth.entity.User;

/**
 * 用户 Service 接口
 *
 * <p>按照阿里巴巴 Java 开发规范设计，提供用户业务逻辑操作</p>
 *
 * @author fangAndlu
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
}
