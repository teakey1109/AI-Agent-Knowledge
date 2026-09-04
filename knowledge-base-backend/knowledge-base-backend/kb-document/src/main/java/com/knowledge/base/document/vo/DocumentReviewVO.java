package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档审核 VO
 *
 * @author fangAndlu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档审核信息")
public class DocumentReviewVO {

    @Schema(description = "审核记录 ID")
    private Long id;

    @Schema(description = "文档 ID")
    private Long documentId;

    @Schema(description = "文档标题")
    private String documentTitle;

    @Schema(description = "审核人 ID")
    private Long reviewerId;

    @Schema(description = "审核人姓名")
    private String reviewerName;

    @Schema(description = "审核结果：1-通过，2-驳回")
    private Integer reviewResult;

    @Schema(description = "审核意见")
    private String reviewComment;

    @Schema(description = "审核前状态")
    private Integer beforeStatus;

    @Schema(description = "审核时间")
    private LocalDateTime reviewedAt;

    @Schema(description = "审核轮次")
    private Integer reviewRound;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
