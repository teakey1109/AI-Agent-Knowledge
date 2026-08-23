package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.document.dto.TagCreateDTO;
import com.knowledge.base.document.dto.TagQueryDTO;
import com.knowledge.base.document.dto.TagUpdateDTO;
import com.knowledge.base.document.entity.Tag;
import com.knowledge.base.document.vo.TagVO;

import java.util.List;

/**
 * 标签 Service 接口
 *
 * @author fangAndlu
 */
public interface TagService extends IService<Tag> {

    /**
     * 创建标签
     *
     * @param tagCreateDTO 创建 DTO
     * @return 标签 ID
     */
    Long createTag(TagCreateDTO tagCreateDTO);

    /**
     * 更新标签
     *
     * @param tagCreateDTO 更新 DTO
     * @return 是否成功
     */
    Boolean updateTag(TagUpdateDTO tagCreateDTO);

    /**
     * 删除标签
     *
     * @param tagId 标签 ID
     * @return 是否成功
     */
    Boolean deleteTag(Long tagId);

    /**
     * 获取标签详情
     *
     * @param tagId 标签 ID
     * @return 标签 VO
     */
    TagVO getTagDetail(Long tagId);

    /**
     * 分页查询标签
     *
     * @param tagQueryDTO 查询 DTO
     * @return 分页结果
     */
    PageResult<TagVO> pageTags(TagQueryDTO tagQueryDTO);

    /**
     * 获取热门标签
     *
     * @param limit 数量限制
     * @return 标签列表
     */
    List<TagVO> getHotTags(Integer limit);

    /**
     * 根据分类获取标签
     *
     * @param categoryId 分类 ID
     * @return 标签列表
     */
    List<TagVO> getTagsByCategory(Long categoryId);

    /**
     * 批量创建标签
     *
     * @param tagNames 标签名称列表
     * @return 标签 ID 列表
     */
    List<Long> batchCreateTags(List<String> tagNames);
}
