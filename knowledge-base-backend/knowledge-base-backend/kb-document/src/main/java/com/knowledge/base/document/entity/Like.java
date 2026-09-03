package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞实体
 *
 * @author fangAndlu
 */
@Data
@TableName("kb_like")
public class Like {

    /**
     * 点赞 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 目标 ID（文档或评论）
     */
    private Long targetId;

    /**
     * 目标类型：1-文档，2-评论
     */
    private Integer targetType;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
