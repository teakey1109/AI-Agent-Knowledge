SET NAMES utf8mb4;
USE `kb_document`;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `kb_category` (
                               `id` BIGINT NOT NULL COMMENT '分类ID',
                               `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID（0表示根分类）',
                               `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                               `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
                               `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
                               `icon` VARCHAR(50) DEFAULT '📁' COMMENT '分类图标',
                               `sort` INT NOT NULL DEFAULT 0 COMMENT '排序号',
                               `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
                               `document_count` INT NOT NULL DEFAULT 0 COMMENT '文档数量',
                               `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
                               `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
                               `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
                               PRIMARY KEY (`id`),
                               KEY `idx_parent_id` (`parent_id`),
                               KEY `idx_category_code` (`category_code`),
                               KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档分类表';