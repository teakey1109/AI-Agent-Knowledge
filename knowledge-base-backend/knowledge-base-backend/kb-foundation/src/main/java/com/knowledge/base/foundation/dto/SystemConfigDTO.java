package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置 DTO
 *
 * <p>按照阿里巴巴 Java 开发规范设计，用于接收系统配置创建/更新请求参数</p>
 *
 * @author fangAndlu
 */
@Data
@Schema(description = "系统配置请求参数")
public class SystemConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置 ID
     */
    @Schema(description = "配置 ID", example = "1234567890123456789")
    private Long id;

    /**
     * 配置键
     */
    @Schema(description = "配置键", required = true, example = "ai.model.name")
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过 100 个字符")
    private String configKey;

    /**
     * 配置值
     */
    @Schema(description = "配置值", required = true, example = "gpt-4")
    @NotBlank(message = "配置值不能为空")
    @Size(max = 1000, message = "配置值长度不能超过 1000 个字符")
    private String configValue;

    /**
     * 配置类型：string/number/boolean/json
     */
    @Schema(description = "配置类型", required = true, example = "string")
    @NotBlank(message = "配置类型不能为空")
    @Size(max = 20, message = "配置类型长度不能超过 20 个字符")
    private String configType;

    /**
     * 配置分类：AI/STORAGE/NOTIFICATION/SECURITY等
     */
    @Schema(description = "配置分类", required = true, example = "AI")
    @NotBlank(message = "配置分类不能为空")
    @Size(max = 50, message = "配置分类长度不能超过 50 个字符")
    private String category;

    /**
     * 配置描述
     */
    @Schema(description = "配置描述", example = "AI 模型名称配置")
    @Size(max = 500, message = "配置描述长度不能超过 500 个字符")
    private String description;

    /**
     * 是否公开：0-私有，1-公开
     */
    @Schema(description = "是否公开", example = "0")
    @NotNull(message = "是否公开不能为空")
    private Integer isPublic;

}
