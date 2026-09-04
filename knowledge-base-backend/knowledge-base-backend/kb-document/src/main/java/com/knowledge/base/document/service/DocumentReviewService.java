package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.document.dto.DocumentReviewDTO;
import com.knowledge.base.document.dto.ReviewQueryDTO;
import com.knowledge.base.document.entity.DocumentReview;
import com.knowledge.base.document.vo.DocumentReviewVO;

import java.util.List;

/**
 * 文档审核 Service 接口
 *
 * @author fangAndlu
 */
public interface DocumentReviewService extends IService<DocumentReview> {

    /**
     * 提交审核
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    Boolean submitForReview(Long documentId);

    /**
     * 审核通过
     *
     * @param documentReviewDTO 审核 DTO
     * @return 是否成功
     */
    Boolean approveReview(DocumentReviewDTO documentReviewDTO);

    /**
     * 审核驳回
     *
     * @param documentReviewDTO 审核 DTO
     * @return 是否成功
     */
    Boolean rejectReview(DocumentReviewDTO documentReviewDTO);

    /**
     * 获取待审核文档列表
     *
     * @param reviewQueryDTO 查询 DTO
     * @return 分页结果
     */
    PageResult<DocumentReviewVO> getPendingReviews(ReviewQueryDTO reviewQueryDTO);

    /**
     * 获取文档审核历史
     *
     * @param documentId 文档 ID
     * @return 审核历史列表
     */
    List<DocumentReviewVO> getDocumentReviewHistory(Long documentId);
}
