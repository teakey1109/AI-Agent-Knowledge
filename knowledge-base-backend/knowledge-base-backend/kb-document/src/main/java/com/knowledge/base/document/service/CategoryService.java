package com.knowledge.base.document.service;

import com.knowledge.base.document.dto.CategoryDTO;
import com.knowledge.base.document.vo.CategoryVO;

import java.util.List;

/**
 * 分类 Service 接口
 *
 * <p>提供文档分类相关业务逻辑</p>
 *
 * @author fangAndlu
 */
public interface CategoryService {

    /**
     * 创建分类
     *
     * @param categoryDTO 分类信息
     * @return 分类 ID
     */
    Long createCategory(CategoryDTO categoryDTO);

    /**
     * 更新分类
     *
     * @param categoryDTO 分类信息
     * @return 是否成功
     */
    Boolean updateCategory(CategoryDTO categoryDTO);

    /**
     * 删除分类
     *
     * @param categoryId 分类 ID
     * @return 是否成功
     */
    Boolean deleteCategory(Long categoryId);

    /**
     * 根据 ID 查询分类
     *
     * @param categoryId 分类 ID
     * @return 分类信息
     */
    CategoryVO getCategoryById(Long categoryId);

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取子分类
     *
     * @param parentId 父分类 ID
     * @return 子分类列表
     */
    List<CategoryVO> getChildren(Long parentId);

    /**
     * 移动分类
     *
     * @param categoryId   分类 ID
     * @param newParentId 新父分类 ID
     * @return 是否成功
     */
    Boolean moveCategory(Long categoryId, Long newParentId);

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    List<CategoryVO> getAllCategories();
}
