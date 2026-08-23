package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}
