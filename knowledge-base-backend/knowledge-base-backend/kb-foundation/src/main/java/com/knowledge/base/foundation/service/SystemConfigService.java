package com.knowledge.base.foundation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.foundation.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置 Service 接口
 *
 * @author fangAndlu
 */
public interface SystemConfigService {

    /**
     * 分页查询配置列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param category 配置分类
     * @return 配置分页信息
     */
    IPage<SystemConfig> pageConfigs(Long current, Long size, String category);

    /**
     * 根据配置键查询配置项
     *
     * @param key 配置键
     * @return 配置信息
     */
    SystemConfig getConfigByKey(String key);

    /**
     * 创建配置
     *
     * @param config 配置信息
     * @return 是否成功
     */
    Boolean createConfig(SystemConfig config);

    /**
     * 更新配置
     *
     * @param key    配置键
     * @param config 配置信息
     * @return 是否成功
     */
    Boolean updateConfig(String key, SystemConfig config);

    /**
     * 删除配置
     *
     * @param key 配置键
     * @return 是否成功
     */
    Boolean deleteConfig(String key);

    /**
     * 按分类获取配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    List<SystemConfig> getConfigsByCategory(String category);

    /**
     * 获取公开配置
     *
     * @return 公开配置Map
     */
    Map<String, String> getPublicConfigs();


}
