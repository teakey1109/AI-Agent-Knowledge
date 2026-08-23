package com.knowledge.base.document.controller;

import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.TagCreateDTO;
import com.knowledge.base.document.dto.TagQueryDTO;
import com.knowledge.base.document.dto.TagUpdateDTO;
import com.knowledge.base.document.service.TagService;
import com.knowledge.base.document.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理 Controller
 *
 * @author fangAndlu
 */
@Slf4j
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "标签管理相关接口")
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 创建标签
     */
    @PostMapping
    @Operation(summary = "创建标签", description = "创建新标签")
    public Result<Long> createTag(@Valid @RequestBody TagCreateDTO dto) {
        log.info("创建标签请求：tagName={}", dto.getTagName());

        Long tagId = tagService.createTag(dto);
        return Result.success("创建标签成功", tagId);
    }

    /**
     * 更新标签
     */
    @PutMapping
    @Operation(summary = "更新标签", description = "更新标签信息")
    public Result<Boolean> updateTag(@Valid @RequestBody TagUpdateDTO dto) {
        log.info("更新标签请求：tagId={}", dto.getId());

        Boolean result = tagService.updateTag(dto);
        return Result.success("更新标签成功", result);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    @Operation(summary = "删除标签", description = "删除指定标签")
    public Result<Boolean> deleteTag(
            @Parameter(description = "标签 ID", required = true)
            @PathVariable Long tagId) {
        log.info("删除标签请求：tagId={}", tagId);

        Boolean result = tagService.deleteTag(tagId);
        return Result.success("删除标签成功", result);
    }

    /**
     * 获取标签详情
     */
    @GetMapping("/{tagId}")
    @Operation(summary = "获取标签详情", description = "根据 ID 获取标签详情")
    public Result<TagVO> getTagDetail(
            @Parameter(description = "标签 ID", required = true)
            @PathVariable Long tagId) {
        log.info("获取标签详情请求：tagId={}", tagId);

        TagVO tagVO = tagService.getTagDetail(tagId);
        return Result.success(tagVO);
    }

    /**
     * 分页查询标签
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询标签", description = "分页查询标签列表")
    public Result<PageResult<TagVO>> pageTags(@RequestBody TagQueryDTO dto) {
        log.info("分页查询标签请求：current={}, size={}", dto.getCurrent(), dto.getSize());

        PageResult<TagVO> pageResult = tagService.pageTags(dto);
        return Result.success(pageResult);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门标签", description = "获取使用最多的标签")
    public Result<List<TagVO>> getHotTags(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门标签请求：limit={}", limit);

        List<TagVO> hotTags = tagService.getHotTags(limit);
        return Result.success(hotTags);
    }

    /**
     * 根据分类获取标签
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取标签", description = "获取指定分类下的标签")
    public Result<List<TagVO>> getTagsByCategory(
            @Parameter(description = "分类 ID", required = true)
            @PathVariable Long categoryId) {
        log.info("根据分类获取标签请求：categoryId={}", categoryId);

        List<TagVO> tags = tagService.getTagsByCategory(categoryId);
        return Result.success(tags);
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
    public Result<List<TagVO>> searchTags(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        log.info("搜索标签请求：keyword={}", keyword);

        TagQueryDTO tagQueryDTO = new TagQueryDTO();
        tagQueryDTO.setTagName(keyword);
        tagQueryDTO.setCurrent(1L);
        tagQueryDTO.setSize(20L);

        PageResult<TagVO> pageResult = tagService.pageTags(tagQueryDTO);
        return Result.success(pageResult.getRecords());
    }


}
