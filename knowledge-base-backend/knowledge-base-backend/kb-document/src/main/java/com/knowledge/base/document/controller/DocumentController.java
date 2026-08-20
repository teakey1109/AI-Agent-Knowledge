package com.knowledge.base.document.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.DocumentDTO;
import com.knowledge.base.document.service.DocumentService;
import com.knowledge.base.document.vo.DocumentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档 Controller
 *
 * <p>按照阿里巴巴 Java 开发规范设计，提供文档管理相关接口</p>
 *
 * @author fangAndlu
 */
@Slf4j
@RestController
@RequestMapping("/documents")
@Tag(name = "文档管理", description = "文档信息管理接口")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    /**
     * 创建文档
     *
     * @param documentDTO 文档信息
     * @return 文档 ID
     */
    @PostMapping
    @Operation(summary = "创建文档", description = "创建新文档")
    public Result<Long> createDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        log.info("创建文档请求：title={}", documentDTO.getTitle());

        Long documentId = documentService.createDocument(documentDTO);
        return Result.success("创建文档成功", documentId);
    }

    /**
     * 更新文档
     *
     * @param documentDTO 文档信息
     * @return 是否成功
     */
    @PutMapping
    @Operation(summary = "更新文档", description = "更新文档信息")
    public Result<Boolean> updateDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        log.info("更新文档请求：documentId={}", documentDTO.getId());

        Boolean success = documentService.updateDocument(documentDTO);
        return Result.success("更新文档成功", success);
    }

    /**
     * 删除文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @DeleteMapping("/{documentId}")
    @Operation(summary = "删除文档", description = "根据文档 ID 删除文档")
    public Result<Boolean> deleteDocument(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        log.info("删除文档请求：documentId={}", documentId);

        Boolean success = documentService.deleteDocument(documentId);
        return Result.success("删除文档成功", success);
    }

    /**
     * 根据 ID 查询文档
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    @GetMapping("/{documentId}")
    @Operation(summary = "查询文档", description = "根据文档 ID 查询文档详情")
    public Result<DocumentVO> getDocumentById(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        log.info("查询文档请求：documentId={}", documentId);

        DocumentVO documentVO = documentService.getDocumentById(documentId);
        return Result.success(documentVO);
    }

    /**
     * 浏览文档（增加浏览次数）
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    @GetMapping("/{documentId}/view")
    @Operation(summary = "浏览文档", description = "浏览文档并增加浏览次数")
    public Result<DocumentVO> viewDocument(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        DocumentVO documentVO = documentService.viewDocument(documentId);
        return Result.success(documentVO);
    }

    /**
     * 分页查询文档列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param categoryId 分类 ID
     * @param keyword    搜索关键词
     * @param status     状态
     * @return 文档分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询文档", description = "分页查询文档列表")
    public Result<IPage<DocumentVO>> pageDocuments(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询文档请求：current={}, size={}, categoryId={}, keyword={}, status={}",
                current, size, categoryId, keyword, status);

        IPage<DocumentVO> page = documentService.pageDocuments(current, size, categoryId, keyword, status);
        return Result.success(page);
    }

    /**
     * 上传文档文件
     *
     * @param file 文件
     * @return 文件路径
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文档文件", description = "上传文档文件并返回文件路径")
    public Result<String> uploadDocumentFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file) {
        log.info("上传文档文件请求：fileName={}", file.getOriginalFilename());

        String filePath = documentService.uploadDocumentFile(file);
        return Result.success("上传文件成功", filePath);
    }

    /**
     * 点赞文档
     * 在 RESTful 规范中：
     * RequestBody 通常用于创建或更新一个完整的资源。
     * 比如创建一篇文档，你需要把标题、内容、分类等一堆字段打包成一个 JSON 对象传给后端。
     * PathVariable 用于对某个特定资源执行一个动作。
     * 点赞（Like）、收藏（Collect）、关注（Follow）这类操作，本质上是针对“文档”这个资源的一个行为（Action）。
     * 它的核心参数只有一个，那就是“你要点赞哪个文档”（即 documentId）。
     * 既然只有一个 ID，且它已经在 URL 路径里了，就没必要再画蛇添足地构造一个 JSON 对象。
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @PostMapping("/{documentId}/like")
    @Operation(summary = "点赞文档", description = "用户点赞文档")
    public Result<Boolean> likeDocument(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        log.info("点赞文档请求：documentId={}", documentId);

        Boolean success = documentService.likeDocument(documentId);
        return Result.success("点赞成功", success);
    }

    /**
     * 收藏文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @PostMapping("/{documentId}/favorite")
    @Operation(summary = "收藏文档", description = "用户收藏文档")
    public Result<Boolean> favoriteDocument(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        log.info("收藏文档请求：documentId={}", documentId);

        Boolean success = documentService.favoriteDocument(documentId);
        return Result.success("收藏成功", success);
    }

    /**
     * 发布文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @PutMapping("/{documentId}/publish")
    @Operation(summary = "发布文档", description = "发布文档")
    public Result<Boolean> publishDocument(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        log.info("发布文档请求：documentId={}", documentId);

        Boolean success = documentService.publishDocument(documentId);
        return Result.success("发布成功", success);
    }

    /**
     * 归档文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @PutMapping("/{documentId}/archive")
    @Operation(summary = "归档文档", description = "归档文档")
    public Result<Boolean> archiveDocument(
            @Parameter(description = "文档 ID", required = true)
            @PathVariable Long documentId) {
        log.info("归档文档请求：documentId={}", documentId);

        Boolean success = documentService.archiveDocument(documentId);
        return Result.success("归档成功", success);
    }
}
