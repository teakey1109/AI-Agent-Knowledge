package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.TagCreateDTO;
import com.knowledge.base.document.dto.TagQueryDTO;
import com.knowledge.base.document.dto.TagUpdateDTO;
import com.knowledge.base.document.entity.Tag;
import com.knowledge.base.document.mapper.TagMapper;
import com.knowledge.base.document.service.TagService;
import com.knowledge.base.document.vo.TagVO;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签 Service 实现类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，实现标签相关业务逻辑</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;

    /**
     * 创建标签
     *
     * @param tagCreateDTO 创建 DTO
     * @return 标签 ID
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(TagCreateDTO tagCreateDTO) {
        log.info("创建标签：tagName={}", tagCreateDTO.getTagName());

        // 检查标签名称是否已存在
        Tag existTag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getTagName, tagCreateDTO.getTagName()));
        if (existTag != null) {
            throw new BusinessException("标签名称已存在");
        }

        // 生成标签编码
        String tagCode = StringUtils.hasText(tagCreateDTO.getTagName())
                ? generateTagCode(tagCreateDTO.getTagName()) : "TAG_" + System.currentTimeMillis();

        // 构建标签实体
        Tag tag = new Tag();
        tag.setId(SnowflakeIdGenerator.getInstance().nextId());
        tag.setTagName(tagCreateDTO.getTagName());
        tag.setTagCode(tagCode);
        tag.setCategoryId(tagCreateDTO.getCategoryId());
        tag.setTagType(tagCreateDTO.getTagType() != null ? tagCreateDTO.getTagType() : 1);
        tag.setColor(tagCreateDTO.getColor());
        tag.setIcon(tagCreateDTO.getIcon());
        tag.setDocCount(0);
        tag.setStatus(1);

        // 保存标签
        int count = tagMapper.insert(tag);
        if (count <= 0) {
            throw new BusinessException("创建标签失败");
        }

        return tag.getId();
    }

    /**
     * 生成标签编码
     *
     * @param tagName 标签名称
     * @return 标签编码
     */
    private String generateTagCode(String tagName) {
        // 简单的编码生成逻辑
        return "TAG_" + tagName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "") + "_" + System.currentTimeMillis();
    }

    /**
     * 更新标签
     *
     * @param tagUpdateDTO 更新 DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTag(TagUpdateDTO tagUpdateDTO) {
        log.info("更新标签：tagId={}", tagUpdateDTO.getId());

        if (tagUpdateDTO.getId() == null) {
            throw new BusinessException("标签 ID 不能为空");
        }

        // 检查标签是否存在
        Tag existTag = tagMapper.selectById(tagUpdateDTO.getId());
        if (existTag == null) {
            throw new BusinessException("标签不存在");
        }

        // 检查标签名称是否被其他标签使用
        if (StringUtils.hasText(tagUpdateDTO.getTagName())
                && !tagUpdateDTO.getTagName().equals(existTag.getTagName())) {
            Tag tag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                            .eq(Tag::getTagName, tagUpdateDTO.getTagName())
            );
            if (tag != null && !tag.getId().equals(tagUpdateDTO.getId())) {
                throw new BusinessException("标签名称已被使用");
            }
        }

        // 构建更新实体
        Tag tag = new Tag();
        tag.setId(tagUpdateDTO.getId());
        if (StringUtils.hasText(tagUpdateDTO.getTagName())) {
            tag.setTagName(tagUpdateDTO.getTagName());
        }
        if (tagUpdateDTO.getCategoryId() != null) {
            tag.setCategoryId(tagUpdateDTO.getCategoryId());
        }
        if (tagUpdateDTO.getColor() != null) {
            tag.setColor(tagUpdateDTO.getColor());
        }
        if (tagUpdateDTO.getIcon() != null) {
            tag.setIcon(tagUpdateDTO.getIcon());
        }
        if (tagUpdateDTO.getStatus() != null) {
            tag.setStatus(tagUpdateDTO.getStatus());
        }

        int count = tagMapper.updateById(tag);
        return count > 0;
    }

    /**
     * 删除标签
     *
     * @param tagId 标签 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTag(Long tagId) {
        log.info("删除标签：tagId={}", tagId);

        if (tagId == null) {
            throw new BusinessException("标签 ID 不能为空");
        }

        // 检查标签是否存在
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        // 检查是否有关联文档
        if (tag.getDocCount() != null && tag.getDocCount() > 0) {
            throw new BusinessException("该标签下有文档，不能删除");
        }

        // 删除标签
        int count = tagMapper.deleteById(tagId);
        return count > 0;
    }

    /**
     * 获取标签详情
     *
     * @param tagId 标签 ID
     * @return 标签 VO
     */
    @Override
    public TagVO getTagDetail(Long tagId) {
        if (tagId == null) {
            throw new BusinessException("标签 ID 不能为空");
        }

        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        return convertToVO(tag);
    }

    /**
     * 转换为 VO
     *
     * @param tag 标签实体
     * @return 标签 VO
     */
    private TagVO convertToVO(Tag tag) {
        return TagVO.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .tagCode(tag.getTagCode())
                .categoryId(tag.getCategoryId())
                .tagType(tag.getTagType())
                .color(tag.getColor())
                .icon(tag.getIcon())
                .docCount(tag.getDocCount() != null ? tag.getDocCount() : 0)
                .status(tag.getStatus())
                .createdAt(tag.getCreateTime())
                .build();
    }

    /**
     * 分页查询标签
     *
     * @param tagQueryDTO 查询 DTO
     * @return 分页结果
     */
    @Override
    public PageResult<TagVO> pageTags(TagQueryDTO tagQueryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(tagQueryDTO.getTagName())) {
            wrapper.like(Tag::getTagName, tagQueryDTO.getTagName())
                    .or()
                    .like(Tag::getTagCode, tagQueryDTO.getTagName());
        }
        if (tagQueryDTO.getCategoryId() != null) {
            wrapper.eq(Tag::getCategoryId, tagQueryDTO.getCategoryId());
        }
        if (tagQueryDTO.getTagType() != null) {
            wrapper.eq(Tag::getTagType, tagQueryDTO.getTagType());
        }
        wrapper.eq(Tag::getStatus, 1);

        // 分页查询
        Page<Tag> page = new Page<>(tagQueryDTO.getCurrent(), tagQueryDTO.getSize());
        IPage<Tag> tagPage = tagMapper.selectPage(page, wrapper);

        // 转换为VO
        IPage<TagVO> voPage = tagPage.convert(this::convertToVO);

        return PageResult.<TagVO>builder()
                .records(voPage.getRecords())
                .total(voPage.getTotal())
                .current(voPage.getCurrent())
                .size(voPage.getSize())
                .build();
    }

    /**
     * 获取热门标签
     *
     * @param limit 数量限制
     * @return 标签列表
     */
    @Override
    public List<TagVO> getHotTags(Integer limit) {

        int size = limit == null || limit <= 0 ? 10 : Math.min(limit, 50);

        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getStatus, 1)
                        .orderByDesc(Tag::getDocCount)
                        .last("LIMIT " + size)
        );

        return tags.stream()
                .map(this::convertToVO)
                .toList();
    }

    /**
     * 根据分类获取标签
     *
     * @param categoryId 分类 ID
     * @return 标签列表
     */
    @Override
    public List<TagVO> getTagsByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类 ID 不能为空");
        }

        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getCategoryId, categoryId)
                        .eq(Tag::getStatus, 1)
                        .orderByDesc(Tag::getDocCount)
        );

        return tags.stream()
                .map(this::convertToVO)
                .toList();
    }

    /**
     * 批量创建标签
     *
     * @param tagNames 标签名称列表
     * @return 标签 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreateTags(List<String> tagNames) {
        log.info("批量创建标签：tagCount={}", tagNames.size());

        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> tagIds = Lists.newArrayList();

        for (String tagName : tagNames) {
            if (!StringUtils.hasText(tagName)) {
                continue;
            }

            // 检查标签是否已存在
            Tag existTag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                            .eq(Tag::getTagName, tagName.trim())
            );

            Long tagId;
            if (existTag != null) {
                tagId = existTag.getId();
            } else {
                // 创建新标签
                TagCreateDTO tagCreateDTO = new TagCreateDTO();
                tagCreateDTO.setTagName(tagName.trim());
                tagCreateDTO.setTagType(1);

                tagId = createTag(tagCreateDTO);
            }

            tagIds.add(tagId);
        }

        return tagIds;
    }
}
