package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档版本实体
 *
 * @author fangAndlu
 */
@Data
@TableName("kb_document_version")
@Schema(description = "文档版本实体")
public class DocumentVersion {

    /**
     * 版本 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "版本 ID")
    private Long id;

    /**
     * 文档 ID
     */
    @Schema(description = "文档 ID")
    private Long documentId;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题")
    private String title;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要")
    private String summary;

    /**
     * 版本变更说明
     */
    @Schema(description = "版本变更说明")
    private String changeDescription;

    /**
     * 变更大小（字节）
     */
    @Schema(description = "变更大小")
    private Long changeSize;

    /**
     * 操作人 ID
     */
    @Schema(description = "操作人 ID")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
