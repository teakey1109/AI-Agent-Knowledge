package com.knowledge.base.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T> 数据类型
 * @author fangAndlu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Long current;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Long size;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Long pages;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long current, Long size, Long total, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setCurrent(current);
        pageResult.setSize(size);
        pageResult.setTotal(total);
        pageResult.setRecords(records);
        pageResult.setPages((total + size - 1) / size);
        return pageResult;
    }

    /**
     * 空分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> empty() {
        // 注意中间的 .<T>。在某些复杂的泛型推断场景下（特别是在静态方法中调用构建器），编译器可能无法自动推断出 T 是什么。
        // 显式写上 .<T> 是为了明确告诉编译器：“我要构建一个类型为 T 的 PageResult”。
        return PageResult.<T>builder()
                .current(1L)
                .size(10L)
                .total(0L)
                .records(Collections.EMPTY_LIST)
                .build();
    }

    /**
     * 判断是否有数据
     */
    public boolean hasData() {
        return this.records != null && !this.records.isEmpty();
    }
}
