package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.DocumentReview;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档审核 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface DocumentReviewMapper extends BaseMapper<DocumentReview> {

}
