package com.knowledge.base.foundation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.PageParam;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.foundation.dto.NotificationDTO;
import com.knowledge.base.foundation.dto.NotificationQueryDTO;
import com.knowledge.base.foundation.entity.Notification;
import com.knowledge.base.foundation.mapper.NotificationMapper;
import com.knowledge.base.foundation.service.NotificationService;
import com.knowledge.base.foundation.vo.NotificationVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    /**
     * 发送系统通知给指定用户。
     * <p>
     * 【注解含义】
     * @Transactional(rollbackFor = Exception.class)：开启数据库事务。
     * 确保通知记录的插入操作具有原子性。当方法执行过程中抛出任何异常（包括受检异常和非受检异常）时，
     * 自动回滚整个事务，防止产生脏数据。
     * <p>
     * 【设计优点】
     * 1. 严谨的入参校验：在入库前对 userId、通知类型、标题和内容进行非空校验，从源头拦截非法请求，避免无效数据落库。
     * 2. 优雅的属性转换：使用 BeanUtils.copyProperties 将 DTO 转换为实体类，减少冗余的 setter 代码，提升代码整洁度。
     * 3. 默认值兜底处理：对未读状态（isRead）进行防御性判空并赋予默认值 0，保证数据库字段约束的完整性。
     * 4. 结果状态强校验：通过判断 insert 方法的返回值（影响行数）来确认是否真正写入成功，失败则主动抛出业务异常。
     *
     * @param notificationDTO 通知数据传输对象（必须包含有效的 userId、通知类型、标题和内容）
     * @return 包含新生成的通知 ID 的统一响应结果
     * @throws BusinessException 当必填参数为空或数据库插入失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> sendNotification(NotificationDTO notificationDTO) {
        // 记录请求入口日志，便于后续链路追踪和问题排查
        log.info("发送通知：userId={}, type={}", notificationDTO.getUserId(), notificationDTO.getNotificationType());

        // 1. 严格的参数校验，防止脏数据入库
        if (notificationDTO.getUserId() == null) {
            throw new BusinessException("接收用户ID不能为空");
        }
        if (!StringUtils.hasText(notificationDTO.getNotificationType())) {
            throw new BusinessException("通知类型不能为空");
        }
        if (!StringUtils.hasText(notificationDTO.getTitle())) {
            throw new BusinessException("通知标题不能为空");
        }
        if (!StringUtils.hasText(notificationDTO.getContent())) {
            throw new BusinessException("通知内容不能为空");
        }

        // 2. 将 DTO 转换为数据库实体对象
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationDTO, notification);

        // 3. 使用雪花算法生成全局唯一 ID，避免数据库自增 ID 暴露业务量
        notification.setId(SnowflakeIdGenerator.getInstance().nextId());

        // 4. 防御性编程：如果前端未传递阅读状态，默认设置为 0（未读）
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }

        // 5. 自动填充创建时间
        notification.setCreateTime(LocalDateTime.now());

        // 6. 执行数据库插入操作
        int count = notificationMapper.insert(notification);

        // 7. 强校验插入结果，若影响行数小于等于 0，说明写入失败，主动抛出异常触发事务回滚
        if (count <= 0) {
            throw new BusinessException("发送通知失败");
        }

        // 记录成功日志，包含生成的唯一 ID，方便客服或用户反馈问题时快速定位
        log.info("通知发送成功：notificationId={}", notification.getId());

        // 返回包含新通知 ID 的成功响应
        return Result.success(notification.getId());
    }

    /**
     * 分页查询通知
     *
     * @param current 当前页
     * @param size    每页大小
     * @param userId  用户 ID
     * @param isRead  是否已读（可选）
     * @return 分页结果
     */
    @Override
    public IPage<Notification> pageNotifications(Long current, Long size, Long userId, Integer isRead) {
        log.info("分页查询通知：current={}, size={}, userId={}, isRead={}", current, size, userId, isRead);

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Notification::getUserId, userId);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }

        wrapper.orderByDesc(Notification::getCreateTime);

        Page<Notification> page = new Page<>(current, size);
        return notificationMapper.selectPage(page, wrapper);
    }

    /**
     * 查询通知列表（基于查询 DTO）
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public Result<IPage<NotificationVO>> getNotifications(NotificationQueryDTO queryDTO) {
        log.info("查询通知列表：userId={}", queryDTO.getUserId());

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getUserId() == null) {
            throw new BusinessException("用户 ID 不能为空");
        }
        wrapper.eq(Notification::getUserId, queryDTO.getUserId());

        if (StringUtils.hasText(queryDTO.getNotificationType())) {
            wrapper.eq(Notification::getNotificationType, queryDTO.getNotificationType());
        }

        if (queryDTO.getIsRead() != null) {
            wrapper.eq(Notification::getIsRead, queryDTO.getIsRead());
        }

        if (StringUtils.hasText(queryDTO.getStartTime())) {
            wrapper.ge(Notification::getCreateTime, queryDTO.getStartTime());
        }
        if (StringUtils.hasText(queryDTO.getEndTime())) {
            wrapper.le(Notification::getCreateTime, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(Notification::getCreateTime);

        Page<Notification> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<Notification> notificationPage = notificationMapper.selectPage(page, wrapper);

        IPage<NotificationVO> voPage = notificationPage.convert(notification -> BeanUtil.copyProperties(notification, NotificationVO.class));

        return Result.success(voPage);
    }

    /**
     * 根据 ID 获取通知
     *
     * @param id 通知 ID
     * @return 通知信息
     */
    @Override
    public Notification getNotificationById(Long id) {
        log.info("查询通知详情：id={}", id);
        return notificationMapper.selectById(id);
    }

    /**
     * 发送通知（基于实体对象，内部调用）
     *
     * @param notification 通知实体
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sendNotification(Notification notification) {
        log.info("发送通知：userId={}, title={}", notification.getUserId(), notification.getTitle());

        if (notification.getUserId() == null) {
            throw new BusinessException("接收用户 ID 不能为空");
        }

        notification.setId(SnowflakeIdGenerator.getInstance().nextId());

        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        notification.setCreateTime(LocalDateTime.now());

        int count = notificationMapper.insert(notification);
        return count > 0;
    }

    /**
     * 标记通知为已读
     *
     * @param id 通知 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> markAsRead(Long id) {
        log.info("标记通知已读：notificationId={}", id);

        if (id == null) {
            throw new BusinessException("通知 ID 不能为空");
        }

        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, id)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadTime, LocalDateTime.now());

        int count = notificationMapper.update(null, updateWrapper);
        return Result.success(count > 0);
    }

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户 ID
     * @return 是否成功
     */
    @Override
    public Result<Boolean> markAllAsRead(Long userId) {
        log.info("标记所有通知已读：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户 ID 不能为空");
        }

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadTime, LocalDateTime.now());

        int count = notificationMapper.update(null, updateWrapper);
        log.info("已标记{}条通知为已读", count);
        return Result.success(true);
    }

    /**
     * 删除通知
     *
     * @param id 通知 ID
     * @return 是否成功
     */
    @Override
    public Result<Boolean> deleteNotification(Long id) {
        log.info("删除通知：notificationId={}", id);

        if (id == null) {
            throw new BusinessException("通知 ID 不能为空");
        }

        int count = notificationMapper.deleteById(id);
        return Result.success(count > 0);
    }

    /**
     * 获取未读通知数量
     *
     * @param userId 用户 ID
     * @return 未读数量
     */
    @Override
    public Result<Long> getUnreadCount(Long userId) {
        log.info("获取未读通知数量：userId={}", userId);

        if (userId == null) {
            throw new BusinessException("用户 ID 不能为空");
        }

        Long count = notificationMapper.countUnreadByUserId(userId);
        return Result.success(count);
    }
}
