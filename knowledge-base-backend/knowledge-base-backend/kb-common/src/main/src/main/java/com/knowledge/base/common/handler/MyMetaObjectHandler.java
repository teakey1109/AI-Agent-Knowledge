package com.knowledge.base.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.knowledge.base.common.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 字段自动填充处理器
 *
 * <p>按照阿里巴巴Java开发规范设计，自动填充创建时间、更新时间等字段</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 获取当前用户 ID
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, userId);
            this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
         log.debug("开始更新填充...");
         this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

         // 获取当前用户 ID
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId != null) {
            this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
        }
    }
}
