package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.OperationLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 操作日志 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 删除指定日期前的日志
     *
     * @param beforeDate 日期字符串
     * @return 删除数量
     */
    @Delete("DELETE FROM kb_operation_log WHERE created_at < #{beforeDate}")
    int deleteBeforeDate(@Param("beforeDate") String beforeDate);
}
