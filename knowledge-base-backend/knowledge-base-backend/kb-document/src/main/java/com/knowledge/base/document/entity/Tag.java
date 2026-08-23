package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import com.knowledge.base.document.enums.TagTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 标签实体
 *
 * @author fangAndlu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_tag")
@Schema(description = "标签实体")
public class Tag extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
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
     * 标签类型：0-SYSTEM，1-用户标签
     */
    @Schema(description = "标签类型")
    private Integer tagType;

    /**
     * 获取标签类型枚举
     *
     * @return 标签类型枚举
     */
    public TagTypeEnum getTagTypeEnum() {
        return TagTypeEnum.getDescFromCode(this.tagType);
    }

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
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

}
