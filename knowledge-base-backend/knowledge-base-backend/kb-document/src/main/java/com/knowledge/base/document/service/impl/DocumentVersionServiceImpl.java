package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.DocumentVersionRestoreDTO;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.entity.DocumentVersion;
import com.knowledge.base.document.mapper.DocumentMapper;
import com.knowledge.base.document.mapper.DocumentVersionMapper;
import com.knowledge.base.document.service.DocumentVersionService;
import com.knowledge.base.document.vo.DocumentVersionVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 文档版本 Service 实现类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，实现文档版本相关业务逻辑</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Service
public class DocumentVersionServiceImpl implements DocumentVersionService {

    @Resource
    private DocumentVersionMapper documentVersionMapper;

    @Resource
    private DocumentMapper documentMapper;

    /**
     * 创建文档版本
     *
     * @param documentId        文档 ID
     * @param changeDescription 变更说明
     * @param userId            用户 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createVersion(Long documentId, String changeDescription, Long userId) {
        log.info("创建文档版本：documentId={}, userId={}", documentId, userId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        // 获取文档信息
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 获取当前最大版本号, 这里存在隐患，总记录数是作为版本来计算的，意味着版本数是递增的
        Long currentVersionCount = documentVersionMapper.selectCount(new LambdaQueryWrapper<DocumentVersion>()
                .eq(DocumentVersion::getDocumentId, documentId));
        int currentVersion = currentVersionCount.intValue();

        // 获取上一个版本以计算变更大小
        long changeSize = 0L;
        DocumentVersion lastVersion = documentVersionMapper.selectOne(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .orderByDesc(DocumentVersion::getVersion)
                        .last("LIMIT 1")
        );
        if (lastVersion != null && lastVersion.getContent() != null) {
            int oldSize = lastVersion.getContent().length();
            int newSize = document.getContent() != null ? document.getContent().length() : 0;
            changeSize = (long) newSize - oldSize;
        }

        // TODO: 获取操作人信息
        String operatorName = "系统用户";

        // 创建版本记录
        DocumentVersion version = new DocumentVersion();
        version.setId(SnowflakeIdGenerator.getInstance().nextId());
        version.setDocumentId(documentId);
        version.setVersion(currentVersion + 1);
        version.setTitle(document.getTitle());
        version.setContent(document.getContent());
        version.setSummary(document.getSummary());
        version.setChangeDescription(changeDescription);
        version.setChangeSize(changeSize);
        version.setOperatorId(userId);
        version.setOperatorName(operatorName);
        version.setCreatedAt(LocalDateTime.now());

        int count = documentVersionMapper.insert(version);
        return count > 0;
    }

    /**
     * 获取文档版本列表
     *
     * @param documentId 文档 ID
     * @param current    当前页
     * @param size       每页大小
     * @return 版本列表
     */
    @Override
    public IPage<DocumentVersionVO> getVersionList(Long documentId, Long current, Long size) {
        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 分页查询版本列表
        Page<DocumentVersion> page = new Page<>(current, size);
        IPage<DocumentVersion> versionIPage = documentVersionMapper.selectPage(page,
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .orderByDesc(DocumentVersion::getVersion));

        // 转换为 VO
        return versionIPage.convert(this::convertToVO);
    }

    /**
     * 获取版本详情
     *
     * @param versionId 版本 ID
     * @return 版本详情
     */
    @Override
    public DocumentVersionVO getVersionDetail(Long versionId) {
        if (versionId == null) {
            throw new BusinessException("版本 ID 不能为空");
        }

        DocumentVersion version = documentVersionMapper.selectById(versionId);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }

        return convertToVO(version);
    }

    /**
     * 恢复版本
     *
     * @param documentId 文档 ID
     * @param restoreDTO 恢复参数
     * @param userId     用户 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreVersion(Long documentId, DocumentVersionRestoreDTO restoreDTO, Long userId) {
        log.info("恢复文档版本：documentId={}, versionId={}, userId={}", documentId, restoreDTO.getVersionId(), userId);

        if (documentId == null) {
            throw new BusinessException("文档 ID 不能为空");
        }

        // 检查文档是否存在
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 检查版本是否存在
        DocumentVersion version = documentVersionMapper.selectById(restoreDTO.getVersionId());
        if (version == null) {
            throw new BusinessException("版本不存在");
        }

        // 检查版本是否属于该文档
        if (!version.getDocumentId().equals(documentId)) {
            throw new BusinessException("版本不属于该文档");
        }

        // 创建当前版本的备份（恢复前）
        createVersion(documentId, "恢复前自动备份", userId);

        // 恢复文档内容
        document.setTitle(version.getTitle());
        document.setContent(version.getContent());
        document.setSummary(version.getSummary());
        documentMapper.updateById(document);

        return true;
    }

    /**
     * 对比版本差异
     *
     * @param versionId1 版本 ID1
     * @param versionId2 版本 ID2
     * @return 差异内容
     */
    @Override
    public String compareVersions(Long versionId1, Long versionId2) {
        if (versionId1 == null || versionId2 == null) {
            throw new BusinessException("版本 ID 不能为空");
        }

        if (versionId1.equals(versionId2)) {
            throw new BusinessException("不能比较相同的版本");
        }

        DocumentVersion version1 = documentVersionMapper.selectById(versionId1);
        DocumentVersion version2 = documentVersionMapper.selectById(versionId2);
        if (version1 == null || version2 == null) {
            throw new BusinessException("版本不存在");
        }

        // 构建差异对比结果
        StringBuilder diff = new StringBuilder();
        diff.append("=== 版本对比 ===\n");
        diff.append(String.format("版本 %d vs 版本 %d\n", version1.getVersion(), version2.getVersion()));
        diff.append("\n");

        // 标题差异
        if (!version1.getTitle().equals(version2.getTitle())) {
            diff.append("【标题差异】\n");
            diff.append(String.format("- 版本%d: %s\n", version1.getVersion(), version1.getTitle()));
            diff.append(String.format("+ 版本%d: %s\n", version2.getVersion(), version2.getTitle()));
            diff.append("\n");
        }

        // TODO 内容差异（这里仅仅比较了字数，实际可以使用 diff 库）
        String content1 = version1.getContent() != null ? version1.getContent() : "";
        String content2 = version2.getContent() != null ? version2.getContent() : "";


        if (!content1.equals(content2)) {
            diff.append("【内容差异】\n");
            diff.append(String.format("版本%d 内容长度: %d 字符\n", version1.getVersion(), content1.length()));
            diff.append(String.format("版本%d 内容长度: %d 字符\n", version2.getVersion(), content2.length()));
            diff.append(String.format("差异大小: %d 字符\n", content2.length() - content1.length()));
        }

        return diff.toString();
    }

    /**
     * 删除版本, 只允许删除文档的最新版本，不允许删除历史中间版本
     *
     * @param versionId 版本 ID
     * @param userId    用户 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteVersion(Long versionId, Long userId) {
        log.info("删除文档版本：versionId={}, userId={}", versionId, userId);

        if (versionId == null) {
            throw new BusinessException("版本 ID 不能为空");
        }

        // 检查版本是否存在
        DocumentVersion version = documentVersionMapper.selectById(versionId);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }

        // 检查是否是最新版本
        Long count = documentVersionMapper.selectCount(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, version.getDocumentId())
                        // 查询是否存在版本号大于当前版本的记录（即：有没有比它更新的版本）
                        .gt(DocumentVersion::getVersion, version.getVersion())
        );

        if (count > 0) {
            throw new BusinessException("不能删除中间版本，只能删除最新版本");
        }

        // 删除版本
        int deleteCount = documentVersionMapper.deleteById(versionId);
        return deleteCount > 0;
    }

    /**
     * 转换为 VO
     *
     * @param documentVersion 版本实体
     * @return 版本 VO
     */
    private DocumentVersionVO convertToVO(DocumentVersion documentVersion) {
        return DocumentVersionVO.builder()
                .id(documentVersion.getId())
                .documentId(documentVersion.getDocumentId())
                .version(documentVersion.getVersion())
                .title(documentVersion.getTitle())
                .changeDescription(documentVersion.getChangeDescription())
                .changeSize(documentVersion.getChangeSize())
                .operatorId(documentVersion.getOperatorId())
                .operatorName(documentVersion.getOperatorName())
                .createdAt(documentVersion.getCreatedAt())
                .build();
    }
}
