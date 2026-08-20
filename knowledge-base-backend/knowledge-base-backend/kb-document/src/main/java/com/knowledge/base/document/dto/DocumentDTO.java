package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 文档 DTO
 *
 * <p>按照阿里巴巴 Java 开发规范设计，用于接收文档创建/更新请求参数</p>
 *
 * @author fangAndlu
 */
@Data
@Schema(description = "文档信息请求参数")
public class DocumentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档 ID
     */
    @Schema(description = "文档 ID", example = "1234567890123456789")
    private Long id;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题", required = true, example = "Spring Boot 使用指南")
    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题长度不能超过 200 个字符")
    private String title;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要", example = "本文档介绍 Spring Boot 的基本使用方法")
    @Size(max = 500, message = "文档摘要长度不能超过 500 个字符")
    private String summary;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 文档类型（1-文章，2-文件）
     */
    @Schema(description = "文档类型（1-文章，2-文件）", example = "1")
    private Integer documentType;

    /**
     * 分类 ID
     */
    @Schema(description = "分类 ID", example = "1234567890123456789")
    private Long categoryId;

    /**
     * 标签（逗号分隔）
     */
    @Schema(description = "标签（逗号分隔）", example = "Spring Boot,Java,后端")
    @Size(max = 200, message = "标签长度不能超过 200 个字符")
    private String tags;

    /**
     * 状态（0-草稿，1-已发布，2-已归档）
     */
    @Schema(description = "状态（0-草稿，1-已发布，2-已归档）", example = "1")
    private Integer status;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶（0-否，1-是）", example = "0")
    private Integer isTop;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐（0-否，1-是）", example = "0")
    private Integer isRecommend;

    /**
     * 封面图 URL
     */
    @Schema(description = "封面图 URL", example = "https://example.com/cover.jpg")
    @Size(max = 500, message = "封面图 URL 长度不能超过 500 个字符")
    private String coverImage;

    /**
     * 来源（1-原创，2-转载，3-翻译）
     */
    @Schema(description = "来源（1-原创，2-转载，3-翻译）", example = "1")
    private Integer source;

    /**
     * 来源 URL
     */
    @Schema(description = "来源 URL", example = "https://example.com/original-article")
    @Size(max = 500, message = "来源 URL 长度不能超过 500 个字符")
    private String sourceUrl;

    /**
     * 允许评论
     */
    @Schema(description = "允许评论（0-否，1-是）", example = "1")
    private Integer allowComment;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "0")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "这是备注信息")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
