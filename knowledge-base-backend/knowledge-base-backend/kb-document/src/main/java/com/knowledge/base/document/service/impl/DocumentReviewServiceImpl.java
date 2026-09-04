package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.DocumentReviewDTO;
import com.knowledge.base.document.dto.ReviewQueryDTO;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.entity.DocumentReview;
import com.knowledge.base.document.mapper.DocumentMapper;
import com.knowledge.base.document.mapper.DocumentReviewMapper;
import com.knowledge.base.document.service.DocumentReviewService;
import com.knowledge.base.document.vo.DocumentReviewVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档审核 Service 实现类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，实现文档审核相关业务逻辑</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Service
public class DocumentReviewServiceImpl extends ServiceImpl<DocumentReviewMapper, DocumentReview> implements DocumentReviewService {

    @Resource
    private DocumentReviewMapper documentReviewMapper;

    @Resource
    private DocumentMapper documentMapper;

    /**
     * 提交审核
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitForReview(Long documentId) {
        log.info("提交文档审核：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 检查文档状态
        if (document.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的文档才能提交审核");
        }

        // 获取当前审核轮次
        LambdaQueryWrapper<DocumentReview> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.select(DocumentReview::getReviewRound)
                .eq(DocumentReview::getDocumentId, documentId)
                .orderByDesc(DocumentReview::getReviewRound)
                .last("LIMIT 1");

        // 查询单条记录
        DocumentReview maxReview = documentReviewMapper.selectOne(lambdaWrapper);

        // 处理空结果集，安全获取轮次
        int currentRound = (maxReview != null) ? maxReview.getReviewRound() : 0;
        int nextRound = currentRound + 1;

        // 创建审核记录
        DocumentReview review = new DocumentReview();
        review.setId(SnowflakeIdGenerator.getInstance().nextId());
        review.setDocumentId(documentId);
        review.setReviewerId(null); // 待分配
        review.setReviewerName(null);
        review.setReviewResult(null);
        review.setReviewComment(null);
        review.setBeforeStatus(document.getStatus());
        review.setReviewedAt(null);
        review.setReviewRound(nextRound);
        review.setCreatedAt(LocalDateTime.now());

        int count = documentReviewMapper.insert(review);
        if (count <= 0) {
            throw new BusinessException("提交审核失败");
        }

        // 更新文档状态为待审核
        document.setStatus(3); // 3-待审核
        documentMapper.updateById(document);

        return true;
    }

    /**
     * 审核通过
     *
     * @param documentReviewDTO 审核 DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveReview(DocumentReviewDTO documentReviewDTO) {
        log.info("审核通过：reviewId={}", documentReviewDTO.getReviewId());

        if (documentReviewDTO.getReviewId() == null) {
            throw new BusinessException("审核记录 ID 不能为空");
        }

        // 检查审核记录是否存在
        DocumentReview review = documentReviewMapper.selectById(documentReviewDTO.getReviewId());
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }

        if (review.getReviewResult() != null) {
            throw new BusinessException("该记录已审核");
        }

        // TODO: 从上下文获取当前审核人信息
        Long reviewerId = 1L;
        String reviewerName = "审核员";

        // 更新审核记录
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setReviewResult(1); // 1-通过
        review.setReviewComment(documentReviewDTO.getReviewComment());
        review.setReviewedAt(LocalDateTime.now());
        documentReviewMapper.updateById(review);

        // 更新文档状态为已发布
        Document document = documentMapper.selectById(review.getDocumentId());
        if (document != null) {
            document.setStatus(1); // 1-已发布
            documentMapper.updateById(document);
        }

        return true;
    }

    /**
     * 审核驳回
     *
     * @param documentReviewDTO 审核 DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rejectReview(DocumentReviewDTO documentReviewDTO) {
        log.info("审核驳回：reviewId={}", documentReviewDTO.getReviewId());

        if (documentReviewDTO.getReviewId() == null) {
            throw new BusinessException("审核记录ID不能为空");
        }

        if (!StringUtils.hasText(documentReviewDTO.getReviewComment())) {
            throw new BusinessException("驳回意见不能为空");
        }

        // 检查审核记录是否存在
        DocumentReview review = documentReviewMapper.selectById(documentReviewDTO.getReviewId());
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }

        if (review.getReviewResult() != null) {
            throw new BusinessException("该记录已审核");
        }

        // TODO: 从上下文获取当前审核人信息
        Long reviewerId = 1L;
        String reviewerName = "审核员";

        // 更新审核记录
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setReviewResult(2); // 2-驳回
        review.setReviewComment(documentReviewDTO.getReviewComment());
        review.setReviewedAt(LocalDateTime.now());
        documentReviewMapper.updateById(review);

        // 更新文档状态为草稿
        Document document = documentMapper.selectById(review.getDocumentId());
        if (document != null) {
            document.setStatus(0); // 0-草稿
            documentMapper.updateById(document);
        }

        return true;
    }

    /**
     * 获取待审核文档列表
     *
     * @param reviewQueryDTO 查询 DTO
     * @return 分页结果
     */
    @Override
    public PageResult<DocumentReviewVO> getPendingReviews(ReviewQueryDTO reviewQueryDTO) {
        LambdaQueryWrapper<DocumentReview> lambdaQueryWrapper = new LambdaQueryWrapper<DocumentReview>()
                .isNull(DocumentReview::getReviewResult);

        if (reviewQueryDTO.getReviewerId() != null) {
            lambdaQueryWrapper.eq(DocumentReview::getReviewerId, reviewQueryDTO.getReviewerId());
        }

        // 关键词搜索
        if (StringUtils.hasText(reviewQueryDTO.getKeyword())) {
            lambdaQueryWrapper.apply("EXISTS (SELECT 1 FROM kb_document d WHERE d.id = kb_document_review.document_id " +
                    "AND d.title LIKE CONCAT('%', {0}, '%'))", reviewQueryDTO.getKeyword());
        }

        // 排序
        lambdaQueryWrapper.orderByDesc(DocumentReview::getCreatedAt);

        // 2. 分页查询
        Page<DocumentReview> page = new Page<>(reviewQueryDTO.getCurrent(), reviewQueryDTO.getSize());
        IPage<DocumentReview> reviewPage = documentReviewMapper.selectPage(page, lambdaQueryWrapper);

        // 转换为VO
        IPage<DocumentReviewVO> voPage = reviewPage.convert(review -> {
            // 获取文档标题
            Document document = documentMapper.selectById(review.getDocumentId());
            String documentTitle = document != null ? document.getTitle() : "";

            return DocumentReviewVO.builder()
                    .id(review.getId())
                    .documentId(review.getDocumentId())
                    .documentTitle(documentTitle)
                    .reviewerId(review.getReviewerId())
                    .reviewerName(review.getReviewerName())
                    .reviewResult(review.getReviewResult())
                    .reviewComment(review.getReviewComment())
                    .beforeStatus(review.getBeforeStatus())
                    .reviewedAt(review.getReviewedAt())
                    .reviewRound(review.getReviewRound())
                    .createdAt(review.getCreatedAt())
                    .build();
        });

        return PageResult.<DocumentReviewVO>builder()
                .records(voPage.getRecords())
                .total(voPage.getTotal())
                .current(voPage.getCurrent())
                .size(voPage.getSize())
                .build();
    }

    /**
     * 获取文档审核历史
     *
     * @param documentId 文档 ID
     * @return 审核历史列表
     */
    @Override
    public List<DocumentReviewVO> getDocumentReviewHistory(Long documentId) {
        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        List<DocumentReview> reviews = documentReviewMapper.selectList(
                new LambdaQueryWrapper<DocumentReview>()
                        .eq(DocumentReview::getDocumentId, documentId)
                        .orderByDesc(DocumentReview::getReviewRound)
        );

        return reviews.stream()
                .map(review -> {
                    // 获取文档标题
                    Document document = documentMapper.selectById(review.getDocumentId());
                    String documentTitle = document != null ? document.getTitle() : "";

                    return DocumentReviewVO.builder()
                            .id(review.getId())
                            .documentId(review.getDocumentId())
                            .documentTitle(documentTitle)
                            .reviewerId(review.getReviewerId())
                            .reviewerName(review.getReviewerName())
                            .reviewResult(review.getReviewResult())
                            .reviewComment(review.getReviewComment())
                            .beforeStatus(review.getBeforeStatus())
                            .reviewedAt(review.getReviewedAt())
                            .reviewRound(review.getReviewRound())
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
