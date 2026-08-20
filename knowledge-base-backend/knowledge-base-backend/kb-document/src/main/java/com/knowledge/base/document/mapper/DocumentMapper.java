package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文档 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    /**
     * 增加浏览次数
     *
     * @param documentId 文档 ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("documentId") Long documentId);

    /**
     * 增加点赞次数
     *
     * @param documentId 文档 ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("documentId") Long documentId);

    /**
     * 增加收藏次数
     *
     * @param documentId 文档 ID
     * @return 影响行数
     */
    int incrementFavoriteCount(@Param("documentId") Long documentId);

    /**
     * 增加评论次数
     *
     * @param documentId 文档 ID
     * @return 影响行数
     */
    int incrementCommentCount(@Param("documentId") Long documentId);

}