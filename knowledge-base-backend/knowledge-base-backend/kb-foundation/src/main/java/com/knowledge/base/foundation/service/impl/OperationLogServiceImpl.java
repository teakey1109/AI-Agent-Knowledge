package com.knowledge.base.foundation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.foundation.entity.OperationLog;
import com.knowledge.base.foundation.mapper.OperationLogMapper;
import com.knowledge.base.foundation.service.OperationLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 分页查询操作日志
     *
     * @param current       当前页
     * @param size          每页大小
     * @param module        模块（可选）
     * @param operationType 操作类型（可选）
     * @param username      用户名（可选）
     * @param startTime     开始时间（可选）
     * @param endTime       结束时间（可选）
     * @return 分页结果
     */
    @Override
    public IPage<OperationLog> pageLogs(Long current, Long size, String module, String operationType, String username, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("分页查询操作日志：current={}, size={}, module={}, operationType={}, username={}", current, size, module, operationType, username);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }
        if (StringUtils.hasText(username)) {
            wrapper.eq(OperationLog::getUsername, username);
        }
        if (startTime != null) {
            wrapper.ge(OperationLog::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getCreateTime, endTime);
        }

        wrapper.orderByDesc(OperationLog::getCreateTime);

        Page<OperationLog> page = new Page<>(current, size);
        return operationLogMapper.selectPage(page, wrapper);
    }

    /**
     * 根据 ID 获取操作日志
     *
     * @param id 日志ID
     * @return 日志信息
     */
    @Override
    public OperationLog getLogById(Long id) {
        log.info("查询操作日志详情：id={}", id);
        return operationLogMapper.selectById(id);
    }

    /**
     * TODO 获取操作日志统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    @Override
    public Map<String, Object> getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取操作日志统计：startTime={}, endTime={}", startTime, endTime);
        return null;
    }

    /**
     * 删除指定日期之前的操作日志
     *
     * @param beforeDate 截止日期
     * @return 删除数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteLogsBeforeDate(LocalDateTime beforeDate) {
        log.info("删除指定日期前的日志：beforeDate={}", beforeDate);

        if (beforeDate == null) {
            return 0;
        }

        String dateStr = beforeDate.format(FORMATTER);
        int count = operationLogMapper.deleteBeforeDate(dateStr);
        log.info("已删除{}条操作日志", count);
        return count;
    }
}
