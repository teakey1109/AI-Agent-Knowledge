package com.knowledge.base.document.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.ResultCode;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.DocumentDTO;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.mapper.DocumentMapper;
import com.knowledge.base.document.service.DocumentService;
import com.knowledge.base.document.vo.DocumentVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 文档 Service 实现类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，实现文档相关业务逻辑</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Resource
    private DocumentMapper documentMapper;

    @Value("${file.upload.path:/data/knowledge-base/uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:104857600}")
    private Long maxFileSize;

    /**
     * 创建文档
     *
     * @param documentDTO 文档信息
     * @return 文档 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocument(DocumentDTO documentDTO) {

        log.info("创建文档：title={}", documentDTO.getTitle());

        // 构建文档实体
        Document document = new Document();
        BeanUtils.copyProperties(documentDTO, document);

        // 生成 ID
        document.setId(SnowflakeIdGenerator.getInstance().nextId());

        // 设置默认值
        if (document.getDocumentType() == null) {
            document.setDocumentType(1);
        }
        if (document.getStatus() == null) {
            document.setStatus(0);
        }
        if (document.getIsTop() == null) {
            document.setIsTop(0);
        }
        if (document.getIsRecommend() == null) {
            document.setIsRecommend(0);
        }
        if (document.getSource() == null) {
            document.setSource(1);
        }
        if (document.getAllowComment() == null) {
            document.setAllowComment(1);
        }
        if (document.getSort() == null) {
            document.setSort(0);
        }
        if (document.getViewCount() == null) {
            document.setViewCount(0L);
        }
        if (document.getLikeCount() == null) {
            document.setLikeCount(0L);
        }
        if (document.getFavoriteCount() == null) {
            document.setFavoriteCount(0L);
        }
        if (document.getCommentCount() == null) {
            document.setCommentCount(0L);
        }

        // TODO: 从上下文中获取当前登录用户
        document.setAuthorId(1L);
        document.setAuthorName("系统管理员");

        // 如果是发布状态，设置发布时间
        if (Objects.equals(document.getStatus(), 1)) {
            document.setPublishTime(LocalDateTime.now());
        }

        // 保存文档
        int count = documentMapper.insert(document);
        if (count <= 0) {
            throw new BusinessException("创建文档失败");
        }

        return document.getId();
    }

    /**
     * 更新文档
     *
     * @param documentDTO 文档信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDocument(DocumentDTO documentDTO) {

        log.info("更新文档：documentId={}", documentDTO.getId());

        if (documentDTO.getId() == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        // 检查文档是否存在
        Document existDocument = documentMapper.selectById(documentDTO.getId());
        if (existDocument == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // 构建更新实体
        Document document = new Document();
        BeanUtil.copyProperties(documentDTO, document);

        // 如果状态从草稿变为发布，设置发布时间
        if (Objects.equals(existDocument.getStatus(), 0) && Objects.equals(documentDTO.getStatus(), 1)) {
            document.setPublishTime(LocalDateTime.now());
        }

        int count = documentMapper.updateById(document);
        return count > 0;
    }

    /**
     * 删除文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDocument(Long documentId) {
        log.info("删除文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        int count = documentMapper.deleteById(documentId);
        return count > 0;
    }

    /**
     * 根据 ID 查询文档
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    @Override
    public DocumentVO getDocumentById(Long documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        return BeanUtil.copyProperties(document, DocumentVO.class);
    }

    /**
     * 浏览文档（增加浏览次数）
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO viewDocument(Long documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // 增加浏览次数
        documentMapper.incrementViewCount(documentId);

        return BeanUtil.copyProperties(document, DocumentVO.class);
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
    @Override
    public IPage<DocumentVO> pageDocuments(Long current, Long size, Long categoryId, String keyword, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<Document> queryWrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            queryWrapper.eq(Document::getCategoryId, categoryId);
        }

        if (status != null) {
            queryWrapper.eq(Document::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(q -> q.like(Document::getTitle, keyword)
                    .or()
                    .like(Document::getSummary, keyword)
                    .or()
                    .like(Document::getContent, keyword)
                    .or()
                    .like(Document::getTags, keyword)
            );
        }

        // 按置顶和排序排序
        queryWrapper.orderByDesc(Document::getIsTop)
                .orderByDesc(Document::getSort)
                .orderByDesc(Document::getPublishTime);

        // 分页查询
        Page<Document> page = new Page<>(current, size);
        IPage<Document> documentPage = documentMapper.selectPage(page, queryWrapper);

        // 转换为 VO
        return documentPage.convert(document ->  BeanUtil.copyProperties(document, DocumentVO.class));
    }

    /**
     * 上传文档文件
     * @param file 前端上传的 MultipartFile 文件对象
     * @return 文件保存后的相对路径（格式：日期目录/唯一文件名.扩展名）
     */
    @Override
    public String uploadDocumentFile(MultipartFile file) {
        // 1. 记录日志：打印上传文件的原始名称，便于后续追踪和排查问题
        log.info("上传文档文件：fileName={}", file.getOriginalFilename());

        // 2. 基础校验：检查文件对象是否为空或文件内容是否为空
        // 注意：实际开发中，MultipartFile 作为方法参数通常不会为 null，但防御性编程建议保留
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 3. 文件大小校验：限制上传文件的最大体积，防止恶意大文件占用服务器资源或导致 OOM
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }

        // 4. 提取文件信息：获取原始文件名及扩展名（如 "pdf", "docx"）
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);

        // 5. 文件类型校验：检查扩展名是否存在且不为空白
        // 防止用户未带扩展名上传，或者恶意构造无后缀文件
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED);
        }

        try {
            // 6. 生成唯一文件名：使用 UUID 重命名，避免文件名冲突，同时防止原始文件名包含特殊字符导致的安全问题
            String fileName = IdUtil.simpleUUID() + "." + extension;

            // 7. 构建按日期分级的存储目录（如：/upload/2026-08-20）
            // 按日期分目录可以有效避免单目录下文件过多导致的文件系统性能下降
            String datePath = LocalDateTime.now().toLocalDate().toString();
            String fullPath = uploadPath + File.separator + datePath;

            // 8. 目录存在性检查：如果目标目录不存在，则递归创建父级和当前目录
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 9. 执行文件保存：将内存中的临时文件转移到服务器指定的物理路径
            File destFile = new File(fullPath, fileName);
            file.transferTo(destFile);

            // 10. 返回相对路径：不返回绝对路径，提高系统安全性和跨环境部署的灵活性
            // 前端或后续业务可通过拼接基础 URL 来访问该文件
            return datePath + File.separator + fileName;

        } catch (Exception e) {
            // 11. 异常处理：捕获文件写入过程中的所有异常（如磁盘满、权限不足、IO错误等）
            // 记录完整的异常堆栈信息，并向上层抛出统一的业务异常
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 点赞文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeDocument(Long documentId) {
        log.info("点赞文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // TODO: 检查用户是否已点赞

        // 增加点赞次数
        int count = documentMapper.incrementLikeCount(documentId);
        return count > 0;
    }

    /**
     * 收藏文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean favoriteDocument(Long documentId) {
        log.info("收藏文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        // 增加收藏次数
        int count = documentMapper.incrementFavoriteCount(documentId);
        return count > 0;
    }

    /**
     * 发布文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishDocument(Long documentId) {
        log.info("发布文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        Document document = new Document();
        document.setId(documentId);
        document.setStatus(1);
        document.setPublishTime(LocalDateTime.now());

        int count = documentMapper.updateById(document);
        return count > 0;
    }

    /**
     * 归档文档
     *
     * @param documentId 文档 ID
     * @return 是否成功
     */
    @Override
    public Boolean archiveDocument(Long documentId) {
        log.info("归档文档：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        Document document = new Document();
        document.setId(documentId);
        document.setStatus(2);

        int count = documentMapper.updateById(document);
        return count > 0;
    }
}
