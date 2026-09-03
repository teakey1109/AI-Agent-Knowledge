package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论创建 DTO
 *
 * @author fangAndlu
 */
@Data
@Schema(description = "评论创建请求")
public class CommentCreateDTO {

    @NotNull(message = "文档 ID 不能为空")
    @Schema(description = "文档 ID")
    private Long documentId;

    @Schema(description = "父评论 ID")
    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "回复给谁（用户 ID）")
    private Long replyToUserId;
}
