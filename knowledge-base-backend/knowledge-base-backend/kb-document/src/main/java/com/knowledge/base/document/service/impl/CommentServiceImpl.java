package com.knowledge.base.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.document.dto.CommentCreateDTO;
import com.knowledge.base.document.dto.CommentQueryDTO;
import com.knowledge.base.document.entity.Comment;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.entity.Like;
import com.knowledge.base.document.mapper.CommentMapper;
import com.knowledge.base.document.mapper.DocumentMapper;
import com.knowledge.base.document.mapper.LikeMapper;
import com.knowledge.base.document.service.CommentService;
import com.knowledge.base.document.vo.CommentVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论 Service 实现类
 *
 * <p>按照阿里巴巴 Java 开发规范设计，实现评论相关业务逻辑</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建评论
     *
     * @param commentCreateDTO 创建 DTO
     * @return 评论 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentCreateDTO commentCreateDTO) {
        log.info("创建评论：documentId={}, parentId={}", commentCreateDTO.getDocumentId(), commentCreateDTO.getParentId());

        // 检查父评论是否存在
        Long rootId = null;
        if (commentCreateDTO.getParentId() != null && commentCreateDTO.getParentId() > 0) {
            Comment parentComment = commentMapper.selectById(commentCreateDTO.getParentId());
            if (parentComment == null) {
                throw new BusinessException("父评论不存在");
            }
            if (!parentComment.getDocumentId().equals(commentCreateDTO.getDocumentId())) {
                throw new BusinessException("父评论不属于该文档");
            }
            rootId = parentComment.getRootId() != null ? parentComment.getRootId() : parentComment.getId();
            // 更新父评论的回复数
//            jdbcTemplate.update(
//                    "UPDATE kb_comment SET reply_count = reply_count + 1 WHERE id = ?", commentCreateDTO.getParentId()
//            );
            // ✅ 替换 jdbcTemplate：使用 MP LambdaUpdateWrapper
            commentMapper.update(null, new LambdaUpdateWrapper<Comment>()
                    .eq(Comment::getId, commentCreateDTO.getParentId())
                    .setSql("reply_count = reply_count + 1")  // 原子自增，防并发
            );
        }

        // TODO: 从上下文获取当前用户信息
        Long userId = 1L;
        String userName = "测试用户";
        String userAvatar = "/avatar/default.png";

        // 构建评论实体
        Comment comment = new Comment();
        comment.setId(SnowflakeIdGenerator.getInstance().nextId());
        comment.setDocumentId(commentCreateDTO.getDocumentId());
        comment.setParentId(commentCreateDTO.getParentId() != null ? commentCreateDTO.getParentId() : 0L);
        comment.setRootId(rootId);
        comment.setContent(commentCreateDTO.getContent());
        comment.setCommenterId(userId);
        comment.setCommenterName(userName);
        comment.setReplyToUserId(commentCreateDTO.getReplyToUserId());
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setReplyCount(0);

        // 保存评论
        int count = commentMapper.insert(comment);
        if (count <= 0) {
            throw new BusinessException("创建评论失败");
        }

        // 更新文档的评论数
//        jdbcTemplate.update(
//                "UPDATE kb_document SET comment_count = comment_count + 1 WHERE id = ?", commentCreateDTO.getDocumentId()
//        );
        // ✅ 替换 jdbcTemplate：更新文档评论数（假设你有 DocumentMapper）
        documentMapper.update(null, new LambdaUpdateWrapper<Document>()
                .eq(Document::getId, commentCreateDTO.getDocumentId())
                .setSql("comment_count = comment_count + 1")
        );

        return comment.getId();
    }

    /**
     * 删除评论
     *
     * @param commentId 评论 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteComment(Long commentId) {
        log.info("删除评论：commentId={}", commentId);

        if (commentId == null) {
            throw new BusinessException("评论 ID 不能为空");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // TODO: 检查权限，只有评论作者或管理员可以删除

        // 检查是否有子评论
        Long childCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, commentId)
        );
        if (childCount > 0) {
            throw new BusinessException("该评论下有回复，不能删除");
        }

        // 删除评论
        int count = commentMapper.deleteById(commentId);
        if (count <= 0) {
            throw new BusinessException("删除评论失败");
        }
        // 更新父评论的回复数
//        if (comment.getParentId() != null && comment.getParentId() > 0) {
//            jdbcTemplate.update(
//                    "UPDATE kb_comment SET reply_count = reply_count - 1 WHERE id = ?",
//                    comment.getParentId()
//            );
//        }
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            commentMapper.update(null, new LambdaUpdateWrapper<Comment>()
                    .eq(Comment::getId, comment.getParentId())
                    .setSql("reply_count = GREATEST(reply_count - 1, 0)")
            );
        }

        // 更新文档的评论数
//        if (count > 0) {
//            jdbcTemplate.update(
//                    "UPDATE kb_document SET comment_count = comment_count - 1 WHERE id = ?",
//                    comment.getDocumentId()
//            );
//        }
        documentMapper.update(null, new LambdaUpdateWrapper<Document>()
                .eq(Document::getId, comment.getDocumentId())
                .setSql("comment_count = GREATEST(comment_count - 1, 0)")
        );

        return true;
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeComment(Long commentId) {
        log.info("点赞评论：commentId={}", commentId);

        if (commentId == null) {
            throw new BusinessException("评论 ID 不能为空");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // TODO: 从上下文获取当前用户ID
        Long userId = 1L;

        // 检查是否已点赞
//        Integer count = jdbcTemplate.queryForObject(
//                "SELECT COUNT(*) FROM tb_like WHERE target_id = ? AND user_id = ? AND target_type = 2",
//                Integer.class,
//                commentId, userId
//        );
        // 检查是否已点赞（应用层快速拦截）
        Long existCount = likeMapper.selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getTargetId, commentId)
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, 2)
        );

        if (existCount != null && existCount > 0) {
            throw new BusinessException("已经点赞过了");
        }

        // 添加点赞记录
//        jdbcTemplate.update(
//                "INSERT INTO tb_like (id, target_id, user_id, target_type, created_at) VALUES (?, ?, ?, 2, NOW())",
//                SnowflakeIdGenerator.getInstance().nextId(), commentId, userId
//        );
        Like likeRecord = new Like();
        likeRecord.setId(SnowflakeIdGenerator.getInstance().nextId());
        likeRecord.setTargetId(commentId);
        likeRecord.setUserId(userId);
        likeRecord.setTargetType(2);
        likeRecord.setCreatedAt(LocalDateTime.now());
        int count = likeMapper.insert(likeRecord);
        if (count <= 0) {
            throw new BusinessException("点赞失败");
        }

        // 更新评论点赞数
//        jdbcTemplate.update(
//                "UPDATE tb_comment SET like_count = like_count + 1 WHERE id = ?",
//                commentId
//        );
        // 更新评论点赞数（原子自增）
        commentMapper.update(null, new LambdaUpdateWrapper<Comment>()
                .eq(Comment::getId, commentId)
                .setSql("like_count = like_count + 1")
        );
        return true;
    }

    /**
     * 取消点赞评论
     *
     * @param commentId 评论 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeComment(Long commentId) {
        log.info("取消点赞评论：commentId={}", commentId);

        if (commentId == null) {
            throw new BusinessException("评论 ID 不能为空");
        }

        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // TODO: 从上下文获取当前用户ID
        Long userId = 1L;

        // 删除点赞记录
//        int count = jdbcTemplate.update(
//                "DELETE FROM tb_like WHERE target_id = ? AND user_id = ? AND target_type = 2",
//                commentId, userId
//        );
        int deletedCount = likeMapper.delete(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getTargetId, commentId)
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, 2)
        );
        // 仅当确实删除了点赞记录时，才更新计数
        if (deletedCount > 0) {
//            jdbcTemplate.update(
//                    "UPDATE tb_comment SET like_count = like_count - 1 WHERE id = ?",
//                    commentId
//            );
            commentMapper.update(null, new LambdaUpdateWrapper<Comment>()
                    .eq(Comment::getId, commentId)
                    .setSql("like_count = GREATEST(like_count - 1, 0)")
            );
        }

        return deletedCount > 0;
    }

    /**
     * 分页查询文档评论
     *
     * @param documentId      文档 ID
     * @param commentQueryDTO 查询 DTO
     * @return 分页结果
     */
    @Override
    public PageResult<CommentVO> pageDocumentComments(Long documentId, CommentQueryDTO commentQueryDTO) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getDocumentId, documentId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getStatus, 1);


        // 排序
        if (StringUtils.hasText(commentQueryDTO.getSortBy())) {
            if ("like_count".equals(commentQueryDTO.getSortBy())) {
                queryWrapper.orderByDesc(Comment::getLikeCount);
            } else {
                boolean isAsc = "asc".equals(commentQueryDTO.getSortOrder());
                if (isAsc) {
                    queryWrapper.orderByAsc(Comment::getCreatedAt);
                } else {
                    queryWrapper.orderByDesc(Comment::getCreatedAt);
                }
            }
        } else {
            queryWrapper.orderByDesc(Comment::getCreatedAt);
        }

        // 分页查询
        Page<Comment> page = new Page<>(commentQueryDTO.getCurrent(), commentQueryDTO.getSize());
        IPage<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

        // 转换为 VO 并加载子评论
        IPage<CommentVO> voPage = commentPage.convert(comment -> {
            CommentVO commentVO = convertToVO(comment);
            // TODO: 设置是否已点赞
            commentVO.setIsLiked(false);
            // 加载子评论
            commentVO.setReplies(getCommentReplies(comment.getId()));
            return commentVO;
        });
        return PageResult.<CommentVO>builder()
                .records(voPage.getRecords())
                .total(voPage.getTotal())
                .current(voPage.getCurrent())
                .size(voPage.getSize())
                .build();
    }

    /**
     * 获取评论回复列表
     *
     * @param parentCommentId 父评论 ID
     * @return 回复列表
     */
    @Override
    public List<CommentVO> getCommentReplies(Long parentCommentId) {
        if (parentCommentId == null || parentCommentId <= 0) {
            return Lists.newArrayList();
        }

        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, parentCommentId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreatedAt)
        );

        return comments.stream()
                .map(comment -> {
                    CommentVO vo = convertToVO(comment);
                    // TODO: 设置是否已点赞
                    vo.setIsLiked(false);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为 VO
     *
     * @param comment 评论实体
     * @return 评论 VO
     */
    private CommentVO convertToVO(Comment comment) {
        return CommentVO.builder()
                .id(comment.getId())
                .documentId(comment.getDocumentId())
                .parentId(comment.getParentId())
                .rootId(comment.getRootId())
                .content(comment.getContent())
                .commenterId(comment.getCommenterId())
                .commenterName(comment.getCommenterName())
                .commenterAvatar(comment.getCommenterAvatar())
                .replyToUserId(comment.getReplyToUserId())
                .replyToUserName(comment.getReplyToUserName())
                .status(comment.getStatus())
                .likeCount(comment.getLikeCount() != null ? comment.getLikeCount() : 0)
                .replyCount(comment.getReplyCount() != null ? comment.getReplyCount() : 0)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
