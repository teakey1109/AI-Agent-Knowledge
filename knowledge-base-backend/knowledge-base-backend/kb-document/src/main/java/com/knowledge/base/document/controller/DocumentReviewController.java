package com.knowledge.base.document.controller;

import com.knowledge.base.common.annotation.OperationLog;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.DocumentReviewDTO;
import com.knowledge.base.document.dto.ReviewQueryDTO;
import com.knowledge.base.document.service.DocumentReviewService;
import com.knowledge.base.document.vo.DocumentReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档审核 Controller
 *
 * @author fangAndlu
 */
@Slf4j
@RestController
@RequestMapping("/document-reviews")
@RequiredArgsConstructor
@Tag(name = "文档审核", description = "文档审核相关接口")
public class DocumentReviewController {

    private final DocumentReviewService documentReviewService;

    /**
     * 提交审核
     */
    @PostMapping("/submit/{documentId}")
    @Operation(summary = "提交文档审核", description = "提交文档进行审核")
    @OperationLog(module = "文档审核", operation = "提交审核", description = "提交文档审核")
    public Result<Boolean> submitForReview(@PathVariable Long documentId) {
        Boolean result = documentReviewService.submitForReview(documentId);
        return Result.success(result);
    }

    /**
     * 审核通过
     */
    @PostMapping("/approve")
    @Operation(summary = "审核通过", description = "审核通过文档")
    @OperationLog(module = "文档审核", operation = "审核通过", description = "审核通过文档")
    public Result<Boolean> approveReview(@Valid @RequestBody DocumentReviewDTO documentReviewDTO) {
        Boolean result = documentReviewService.approveReview(documentReviewDTO);
        return Result.success(result);
    }

    /**
     * 审核驳回
     */
    @PostMapping("/reject")
    @Operation(summary = "审核驳回", description = "审核驳回文档")
    @OperationLog(module = "文档审核", operation = "审核驳回", description = "审核驳回文档")
    public Result<Boolean> rejectReview(@Valid @RequestBody DocumentReviewDTO documentReviewDTO) {
        Boolean result = documentReviewService.rejectReview(documentReviewDTO);
        return Result.success(result);
    }

    /**
     * 获取待审核文档列表
     */
    @PostMapping("/pending")
    @Operation(summary = "获取待审核文档", description = "获取待审核文档列表")
    public Result<PageResult<DocumentReviewVO>> getPendingReviews(@RequestBody ReviewQueryDTO reviewQueryDTO) {
        PageResult<DocumentReviewVO> pageResult = documentReviewService.getPendingReviews(reviewQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取文档审核历史
     */
    @GetMapping("/history/{documentId}")
    @Operation(summary = "获取审核历史", description = "获取文档的审核历史")
    public Result<List<DocumentReviewVO>> getDocumentReviewHistory(@PathVariable Long documentId) {
        List<DocumentReviewVO> history = documentReviewService.getDocumentReviewHistory(documentId);
        return Result.success(history);
    }
}


