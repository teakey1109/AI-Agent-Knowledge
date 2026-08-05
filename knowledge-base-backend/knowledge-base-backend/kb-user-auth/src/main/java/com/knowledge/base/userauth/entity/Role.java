package com.knowledge.base.userauth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，存储系统角色信息</p>
 *
 * @author fangAndlu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 角色状态（0-正常，1-禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
