package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档响应 VO
 *
 * <p>按照阿里巴巴 Java 开发规范设计，用于返回文档信息</p>
 *
 * @author fangAndlu
 */
@Data
@Schema(description = "文档信息响应")
public class DocumentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档 ID
     */
    @Schema(description = "文档 ID")
    private Long id;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题")
    private String title;

    /**
     * 文档摘要
     */
    @Schema(description = "文档摘要")
    private String summary;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 文档类型（1-文章，2-文件）
     */
    @Schema(description = "文档类型")
    private Integer documentType;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 分类 ID
     */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 标签列表
     */
    @Schema(description = "标签列表")
    private String tags;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶")
    private Integer isTop;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Integer isRecommend;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Long viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Long likeCount;

    /**
     * 收藏次数
     */
    @Schema(description = "收藏次数")
    private Long favoriteCount;

    /**
     * 评论次数
     */
    @Schema(description = "评论次数")
    private Long commentCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    /**
     * 作者 ID
     */
    @Schema(description = "作者 ID")
    @Deprecated
    private Long authorId;

    /**
     * 作者名称
     */
    @Schema(description = "作者名称")
    @Deprecated
    private String authorName;

    /**
     * 封面图 URL
     */
    @Schema(description = "封面图 URL")
    private String coverImage;

    /**
     * 来源
     */
    @Schema(description = "来源")
    private Integer source;

    /**
     * 来源 URL
     */
    @Schema(description = "来源 URL")
    private String sourceUrl;

    /**
     * 允许评论
     */
    @Schema(description = "允许评论")
    private Integer allowComment;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
