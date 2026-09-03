package com.knowledge.base.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.base.common.result.PageResult;
import com.knowledge.base.document.dto.CommentCreateDTO;
import com.knowledge.base.document.dto.CommentQueryDTO;
import com.knowledge.base.document.entity.Comment;
import com.knowledge.base.document.vo.CommentVO;
import java.util.List;

/**
 * 评论 Service 接口
 *
 * @author fangAndlu
 */
public interface CommentService extends IService<Comment> {

    /**
     * 创建评论
     *
     * @param commentCreateDTO 创建 DTO
     * @return 评论 ID
     */
    Long createComment(CommentCreateDTO commentCreateDTO);

    /**
     * 删除评论
     *
     * @param commentId 评论 ID
     * @return 是否成功
     */
    Boolean deleteComment(Long commentId);

    /**
     * 点赞评论
     *
     * @param commentId 评论 ID
     * @return 是否成功
     */
    Boolean likeComment(Long commentId);

    /**
     * 取消点赞评论
     *
     * @param commentId 评论 ID
     * @return 是否成功
     */
    Boolean unlikeComment(Long commentId);

    /**
     * 分页查询文档评论
     *
     * @param documentId 文档 ID
     * @param commentQueryDTO 查询 DTO
     * @return 分页结果
     */
    PageResult<CommentVO> pageDocumentComments(Long documentId, CommentQueryDTO commentQueryDTO);

    /**
     * 获取评论回复列表
     *
     * @param parentCommentId 父评论 ID
     * @return 回复列表
     */
    List<CommentVO> getCommentReplies(Long parentCommentId);
}
