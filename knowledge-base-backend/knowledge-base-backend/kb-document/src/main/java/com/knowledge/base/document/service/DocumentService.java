package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.document.dto.DocumentDTO;
import com.knowledge.base.document.vo.DocumentVO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档 Service 接口
 *
 * <p>按照阿里巴巴 Java 开发规范设计，提供文档业务逻辑操作</p>
 *
 * @author fangAndlu
 */
public interface DocumentService {

    /**
     * 创建文档
     *
     * @param documentDTO 文档信息
     * @return 文档 ID
     */
    Long createDocument(@Valid DocumentDTO documentDTO);

    /**
     * 更新文档
     *
     * @param documentDTO 文档信息
     * @return 是否成功
     */
    Boolean updateDocument(@Valid DocumentDTO documentDTO);

    /**
     * 删除文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    Boolean deleteDocument(Long documentId);

    /**
     * 根据 ID 查询文档
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    DocumentVO getDocumentById(Long documentId);

    /**
     * 浏览文档（增加浏览次数）
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    DocumentVO viewDocument(Long documentId);

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
    IPage<DocumentVO> pageDocuments(Long current, Long size, Long categoryId, String keyword, Integer status);

    /**
     * 上传文档文件
     *
     * @param file 文件
     * @return 文件路径
     */
    String uploadDocumentFile(MultipartFile file);

    /**
     * 点赞文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    Boolean likeDocument(Long documentId);

    /**
     * 收藏文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    Boolean favoriteDocument(Long documentId);

    /**
     * 发布文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    Boolean publishDocument(Long documentId);

    /**
     * 归档文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    Boolean archiveDocument(Long documentId);
}
