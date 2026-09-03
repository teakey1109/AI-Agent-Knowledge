package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论实体
 *
 * @author fangAndlu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_comment")
@Schema(description = "评论实体")
public class Comment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 评论 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论 ID")
    private Long id;

    /**
     * 文档 ID
     */
    @Schema(description = "文档 ID")
    private Long documentId;

    /**
     * 父评论 ID
     */
    @Schema(description = "父评论 ID")
    private Long parentId;

    /**
     * 根评论 ID
     */
    @Schema(description = "根评论 ID")
    private Long rootId;

    /**
     * 评论内容
     */
    @Schema(description = "评论内容")
    private String content;

    /**
     * 评论人 ID
     */
    @Schema(description = "评论人 ID")
    private Long commenterId;

    /**
     * 评论人姓名
     */
    @Schema(description = "评论人姓名")
    private String commenterName;

    /**
     * 评论人头像
     */
    @Schema(description = "评论人头像")
    private String commenterAvatar;

    /**
     * 回复给谁（用户 ID）
     */
    @Schema(description = "回复给谁")
    private Long replyToUserId;

    /**
     * 回复给谁（用户姓名）
     */
    @Schema(description = "回复给谁")
    private String replyToUserName;

    /**
     * 状态：0-隐藏，1-正常
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /**
     * 回复数
     */
    @Schema(description = "回复数")
    private Integer replyCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;
}
