package com.knowledge.base.document.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标签类型枚举
 *
 * <p>按照阿里巴巴 Java 开发规范设计，定义标签类型</p>
 *
 * @author fangAndlu
 */
@Getter
@AllArgsConstructor
public enum TagTypeEnum {

    /**
     * 系统标签
     */
    SYSTEM(0, "系统标签"),

    /**
     * 用户标签
     */
    USER(1, "用户标签");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值，找不到返回 null
     */
    public static TagTypeEnum getDescFromCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (TagTypeEnum tagTypeEnum : values()) {
            if (tagTypeEnum.getCode().equals(code)) {
                return tagTypeEnum;
            }
        }

        return null;
    }

    /**
     * 根据编码值获取编号
     *
     * @param desc 编码值
     * @return 枚举值
     */
    public static Integer getCodeFromDesc(String desc) {
        if (desc == null || desc.isEmpty()) {
            return null;
        }

        for (TagTypeEnum tagTypeEnum : values()) {
            if (tagTypeEnum.getDesc().equals(desc)) {
                return tagTypeEnum.getCode();
            }
        }

        return null;
    }

    /**
     * 根据编码获取枚举，找不到返回默认值 USER
     *
     * @param code 编码
     * @return 枚举值
     */
    public static TagTypeEnum getDescFromCodeOrDefault(Integer code) {
        TagTypeEnum tagTypeEnum = getDescFromCode(code);
        return tagTypeEnum != null ? tagTypeEnum : USER;
    }
}
