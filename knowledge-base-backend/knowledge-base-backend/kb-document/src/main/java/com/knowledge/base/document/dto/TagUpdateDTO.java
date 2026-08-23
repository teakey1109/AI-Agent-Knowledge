package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签更新 DTO
 *
 * @author fangAndlu
 */
@Data
@Schema(description = "标签更新请求")
public class TagUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签 ID
     */
    @Schema(description = "标签 ID")
    @NotNull(message = "标签 ID 不能为空")
    private Long id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    @Size(max = 50, message = "标签名称长度不能超过 50 个字符")
    private String tagName;

    /**
     * 标签编码
     */
    @Schema(description = "标签编码")
    @Size(max = 50, message = "标签编码长度不能超过 50 个字符")
    private String tagCode;

    /**
     * 所属分类 ID
     */
    @Schema(description = "所属分类 ID")
    private Long categoryId;

    /**
     * 颜色
     */
    @Schema(description = "颜色")
    @Size(max = 20, message = "颜色值长度不能超过 20 个字符")
    private String color;

    /**
     * 图标
     */
    @Schema(description = "图标")
    @Size(max = 50, message = "图标值长度不能超过 50 个字符")
    private String icon;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}
