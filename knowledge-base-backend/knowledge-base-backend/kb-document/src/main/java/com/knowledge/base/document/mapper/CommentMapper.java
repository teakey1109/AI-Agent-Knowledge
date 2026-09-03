package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
