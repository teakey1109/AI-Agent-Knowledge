package com.knowledge.base.foundation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.foundation.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 通知 Mapper 接口
 *
 * @author fangAndlu
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 统计用户未读通知数量
     *
     * @param userId 用户 ID
     * @return 未读数量
     */
    @Select("SELECT COUNT(*) FROM kb_notification WHERE user_id = #{userId} AND is_read = 0 AND deleted = 0")
    Long countUnreadByUserId(@Param("userId") Long userId);
}
