package com.knowledge.base.document.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Like;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {

}
