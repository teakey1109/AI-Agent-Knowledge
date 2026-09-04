SET NAMES utf8mb4;
USE `kb_document`;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `kb_document` (
                               `id` BIGINT NOT NULL COMMENT '文档ID',
                               `title` VARCHAR(200) NOT NULL COMMENT '文档标题',
                               `content` LONGTEXT NOT NULL COMMENT '文档内容',
                               `summary` TEXT DEFAULT NULL COMMENT '文档摘要',
                               `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
                               `author_id` BIGINT NOT NULL COMMENT '作者ID',
                               `author_name` VARCHAR(50) DEFAULT NULL COMMENT '作者姓名（冗余字段）',
                               `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
                               `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态',
                               `is_public` TINYINT NOT NULL DEFAULT 1 COMMENT '是否公开',
                               `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶',
                               `allow_comment` TINYINT NOT NULL DEFAULT 1 COMMENT '允许评论',
                               `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
                               `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
                               `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论次数',
                               `collect_count` INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
                               `version` INT NOT NULL DEFAULT 1 COMMENT '版本号',
                               `word_count` INT DEFAULT NULL COMMENT '字数',
                               `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
                               PRIMARY KEY (`id`),
                               KEY `idx_title` (`title`(100)),
                               KEY `idx_category_id` (`category_id`),
                               KEY `idx_author_id` (`author_id`),
                               KEY `idx_status` (`status`),
                               KEY `idx_publish_time` (`publish_time`),
                               FULLTEXT KEY `ft_content` (`title`, `content`, `summary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表';

CREATE TABLE `kb_category` (
                               `id` BIGINT NOT NULL COMMENT '分类ID',
                               `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                               `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID',
                               `category_icon` VARCHAR(50) DEFAULT '📁' COMMENT '分类图标',
                               `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
                               `sort` INT NOT NULL DEFAULT 0 COMMENT '排序号',
                               `document_count` INT NOT NULL DEFAULT 0 COMMENT '文档数量',
                               `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
                               `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
                               `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
                               PRIMARY KEY (`id`),
                               KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档分类表';

CREATE TABLE `kb_tag` (
    `id` BIGINT NOT NULL COMMENT '标签ID',
    `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `tag_code` VARCHAR(50) NOT NULL COMMENT '标签编码',
    `category_id` BIGINT DEFAULT NULL COMMENT '所属分类ID',
    `tag_type` TINYINT NOT NULL DEFAULT 1 COMMENT '标签类型：0-SYSTEM，1-用户标签',
    `color` VARCHAR(20) DEFAULT NULL COMMENT '标签颜色',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '标签图标',
    `doc_count` INT NOT NULL DEFAULT 0 COMMENT '文档数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_code` (`tag_code`),
    KEY `idx_tag_name` (`tag_name`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_tag_type` (`tag_type`),
    KEY `idx_status` (`status`),
    KEY `idx_doc_count` (`doc_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 文档评论表
DROP TABLE IF EXISTS `kb_comment`;
CREATE TABLE `kb_comment` (
                              `id` BIGINT(20) PRIMARY KEY COMMENT '评论 ID（雪花ID）',
                              `document_id` BIGINT(20) NOT NULL COMMENT '文档 ID',
                              `parent_id` BIGINT(20) COMMENT '父评论 ID',
                              `root_id` BIGINT(20) COMMENT '根评论 ID',
                              `content` TEXT NOT NULL COMMENT '评论内容',
                              `commenter_id` BIGINT(20) NOT NULL COMMENT '评论人 ID',
                              `commenter_name` VARCHAR(50) COMMENT '评论人姓名',
                              `commenter_avatar` VARCHAR(500) COMMENT '评论人头像',
                              `reply_to_user_id` BIGINT(20) COMMENT '回复给谁（用户 ID）',
                              `reply_to_user_name` VARCHAR(50) COMMENT '回复给谁（用户姓名）',
                              `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-隐藏，1-正常',
                              `like_count` INT DEFAULT 0 COMMENT '点赞数',
                              `reply_count` INT DEFAULT 0 COMMENT '回复数',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
                              KEY `idx_document_id` (`document_id`),
                              KEY `idx_parent_id` (`parent_id`),
                              KEY `idx_root_id` (`root_id`),
                              KEY `idx_commenter_id` (`commenter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 点赞表
DROP TABLE IF EXISTS `kb_like`;
CREATE TABLE `kb_like` (
                           `id` BIGINT(20) PRIMARY KEY COMMENT '点赞 ID',
                           `target_id` BIGINT(20) NOT NULL COMMENT '目标 ID（文档或评论）',
                           `target_type` TINYINT NOT NULL COMMENT '目标类型：1-文档，2-评论',
                           `user_id` BIGINT(20) NOT NULL COMMENT '用户 ID',
                           `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           UNIQUE KEY `uk_target_user_type` (`target_id`, `user_id`, `target_type`),
                           KEY `idx_target_id` (`target_id`),
                           KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

CREATE TABLE `kb_document_version` (
                                       `id` BIGINT NOT NULL COMMENT '版本ID',
                                       `document_id` BIGINT NOT NULL COMMENT '文档ID',
                                       `version` INT NOT NULL COMMENT '版本号',
                                       `title` VARCHAR(200) NOT NULL COMMENT '文档标题',
                                       `content` LONGTEXT NOT NULL COMMENT '文档内容',
                                       `summary` TEXT DEFAULT NULL COMMENT '文档摘要',
                                       `change_description` VARCHAR(500) DEFAULT NULL COMMENT '版本变更说明',
                                       `change_size` BIGINT DEFAULT NULL COMMENT '变更大小（字节）',
                                       `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
                                       `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
                                       `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_doc_version` (`document_id`, `version`),
                                       KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档版本表';

DROP TABLE IF EXISTS `kb_document_review`;
CREATE TABLE `kb_document_review` (
                                      `id` BIGINT(20) PRIMARY KEY COMMENT '审核ID',
                                      `document_id` BIGINT(20) NOT NULL COMMENT '文档ID',
                                      `reviewer_id` BIGINT(20) NOT NULL COMMENT '审核人ID',
                                      `reviewer_name` VARCHAR(50) DEFAULT NULL COMMENT '审核人姓名',
                                      `review_result` TINYINT NOT NULL COMMENT '审核结果：1-通过，2-驳回',
                                      `review_comment` TEXT COMMENT '审核意见',
                                      `before_status` TINYINT COMMENT '审核前状态',
                                      `reviewed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
                                      `review_round` INT DEFAULT 1 COMMENT '审核轮次',
                                      `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      KEY `idx_document_id` (`document_id`),
                                      KEY `idx_reviewer_id` (`reviewer_id`),
                                      KEY `idx_reviewed_at` (`reviewed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档审核记录表';

CREATE TABLE `kb_document_tag` (
                                   `id` BIGINT NOT NULL COMMENT '主键ID',
                                   `document_id` BIGINT NOT NULL COMMENT '文档ID',
                                   `tag_id` BIGINT NOT NULL COMMENT '标签ID',
                                   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_document_tag` (`document_id`, `tag_id`),
                                   KEY `idx_document_id` (`document_id`),
                                   KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档标签关联表';