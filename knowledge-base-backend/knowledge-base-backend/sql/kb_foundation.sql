CREATE DATABASE IF NOT EXISTS kb_foundation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE kb_foundation;

CREATE TABLE `kb_dict` (
                           `id` BIGINT NOT NULL COMMENT '字典ID',
                           `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
                           `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
                           `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型',
                           `description` VARCHAR(500) COMMENT '描述',
                           `sort` INT DEFAULT 0 COMMENT '排序',
                           `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `create_by` BIGINT COMMENT '创建人ID',
                           `update_by` BIGINT COMMENT '更新人ID',
                           `deleted` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_dict_code` (`dict_code`),
                           KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE `kb_dict_data` (
                                `id` BIGINT NOT NULL COMMENT '数据ID',
                                `dict_id` BIGINT NOT NULL COMMENT '字典ID',
                                `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
                                `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
                                `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
                                `dict_sort` INT DEFAULT 0 COMMENT '排序',
                                `css_class` VARCHAR(100) COMMENT '样式类名',
                                `list_class` VARCHAR(100) COMMENT '列表样式',
                                `is_default` TINYINT DEFAULT 0 COMMENT '是否默认：0-否，1-是',
                                `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_dict_id` (`dict_id`),
                                KEY `idx_dict_code` (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

CREATE TABLE `kb_system_config` (
                                    `id` BIGINT NOT NULL COMMENT '配置ID',
                                    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
                                    `config_value` TEXT COMMENT '配置值',
                                    `config_type` VARCHAR(20) NOT NULL COMMENT '配置类型：string/number/boolean/json',
                                    `category` VARCHAR(50) NOT NULL COMMENT '配置分类：AI/STORAGE/NOTIFICATION/SECURITY等',
                                    `description` VARCHAR(500) COMMENT '配置描述',
                                    `is_public` TINYINT DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `create_by` BIGINT COMMENT '创建人ID',
                                    `update_by` BIGINT COMMENT '更新人ID',
                                    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_config_key` (`config_key`),
                                    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE `kb_notification` (
                                   `id` BIGINT NOT NULL COMMENT '通知ID',
                                   `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
                                   `user_name` VARCHAR(50) COMMENT '用户姓名（冗余字段）',
                                   `notification_type` VARCHAR(20) NOT NULL COMMENT '通知类型：system/comment/mention/review/like',
                                   `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
                                   `content` TEXT COMMENT '通知内容',
                                   `link` VARCHAR(500) COMMENT '跳转链接',
                                   `related_type` VARCHAR(50) COMMENT '关联类型',
                                   `related_id` BIGINT COMMENT '关联ID',
                                   `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
                                   `read_time` DATETIME COMMENT '阅读时间',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_user_id` (`user_id`),
                                   KEY `idx_is_read` (`is_read`),
                                   KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

CREATE TABLE `kb_operation_log` (
                                    `id` BIGINT NOT NULL COMMENT '日志ID',
                                    `module` VARCHAR(50) COMMENT '模块名称',
                                    `operation_type` VARCHAR(20) COMMENT '操作类型：LOGIN/CREATE/UPDATE/DELETE等',
                                    `operation_desc` VARCHAR(500) COMMENT '操作描述',
                                    `request_method` VARCHAR(10) COMMENT '请求方法：GET/POST/PUT/DELETE',
                                    `request_url` VARCHAR(500) COMMENT '请求URL',
                                    `request_params` TEXT COMMENT '请求参数（JSON）',
                                    `response_result` TEXT COMMENT '响应结果（JSON）',
                                    `user_id` BIGINT COMMENT '操作用户ID',
                                    `username` VARCHAR(50) COMMENT '操作用户名',
                                    `ip_address` VARCHAR(50) COMMENT 'IP地址',
                                    `location` VARCHAR(200) COMMENT '地理位置',
                                    `user_agent` VARCHAR(500) COMMENT '用户代理',
                                    `execute_time` INT COMMENT '执行时长（毫秒）',
                                    `status` TINYINT COMMENT '状态：0-失败，1-成功',
                                    `error_msg` TEXT COMMENT '错误信息',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_user_id` (`user_id`),
                                    KEY `idx_create_time` (`create_time`),
                                    KEY `idx_operation_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';