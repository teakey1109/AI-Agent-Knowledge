package com.knowledge.base.common.config;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * <p>按照阿里巴巴Java开发规范设计，所有实体类应继承此类</p>
 * <p>包含通用字段：ID、创建时间、更新时间、创建人、更新人、逻辑删除标记</p>
 *
 * <p>设计说明：</p>
 * <ul>
 *   <li>ID：使用雪花算法生成，保证分布式唯一性</li>
 *   <li>逻辑删除：使用@TableLogic注解，MyBatis Plus自动处理</li>
 *   <li>字段自动填充：使用FieldFill，配合MetaObjectHandler实现</li>
 *   <li>不包含version字段：乐观锁不是所有表的必需功能，按需添加</li>
 * </ul>
 *
 * <p>如果需要乐观锁：</p>
 * <ul>
 *   <li>方式1：在具体实体类中添加@Version private Integer version字段</li>
 *   <li>方式2：继承BaseEntityWithVersion类（推荐）</li>
 *   <li>注意：数据库表需要添加version字段（INT，默认值0）</li>
 * </ul>
 *
 * @author fangAndlu
 */

@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID（雪花算法生成）
     */
    private Long id;

    /**
     * 创建时间
     * 仅在插入（新增）数据时触发填充。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 在插入和更新时都会触发填充。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人 ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人 ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     * 执行删除操作时，数据库不会真正删除（DELETE）这条记录，
     * 而是执行 UPDATE 语句将 deleted 字段的值从 0 改为 1。
     */
    @TableLogic
    private Integer deleted;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 插入前自动填充 ID
     */
    public void preInsert() {
        if (this.id == null) {
            this.id = SnowflakeIdGenerator.getInstance().nextId();
        }
    }

}
