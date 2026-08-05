package com.knowledge.base.userauth.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，存储系统用户信息</p>
 *
 * @author fangAndlu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户状态（0-正常，1-禁用）
     */
    private Integer status;

    /**
     * 用户类型（0-普通用户，1-管理员）
     */
    private Integer userType;

    /**
     * 备注
     */
    private String remark;
}
