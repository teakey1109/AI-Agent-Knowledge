package com.knowledge.base.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.document.entity.DocumentVersion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档版本 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface DocumentVersionMapper extends BaseMapper<DocumentVersion> {
}
