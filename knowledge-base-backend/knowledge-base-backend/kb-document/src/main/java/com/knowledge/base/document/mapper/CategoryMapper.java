package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
