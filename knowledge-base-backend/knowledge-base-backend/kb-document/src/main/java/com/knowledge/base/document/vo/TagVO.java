package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签 VO
 *
 * @author fangAndlu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "标签信息")
public class TagVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签 ID
     */
    @Schema(description = "标签 ID")
    private Long id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String tagName;

    /**
     * 标签编码
     */
    @Schema(description = "标签编码")
    private String tagCode;

    /**
     * 所属分类 ID
     */
    @Schema(description = "所属分类 ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 标签类型：0-SYSTEM，1-USER
     */
    @Schema(description = "标签类型")
    private Integer tagType;

    /**
     * 颜色
     */
    @Schema(description = "颜色")
    private String color;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 文档数量
     */
    @Schema(description = "文档数量")
    private Integer docCount;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
