package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档审核记录实体
 *
 * @author fangAndlu
 */
@Data
@TableName("kb_document_review")
@Schema(description = "文档审核记录实体")
public class DocumentReview {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "审核记录 ID")
    private Long id;

    /**
     * 文档 ID
     */
    @Schema(description = "文档 ID")
    private Long documentId;

    /**
     * 审核人 ID
     */
    @Schema(description = "审核人 ID")
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    @Schema(description = "审核人姓名")
    private String reviewerName;

    /**
     * 审核结果：1-通过，2-驳回
     */
    @Schema(description = "审核结果")
    private Integer reviewResult;

    /**
     * 审核意见
     */
    @Schema(description = "审核意见")
    private String reviewComment;

    /**
     * 审核前状态
     */
    @Schema(description = "审核前状态")
    private Integer beforeStatus;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime reviewedAt;

    /**
     * 审核轮次
     */
    @Schema(description = "审核轮次")
    private Integer reviewRound;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
