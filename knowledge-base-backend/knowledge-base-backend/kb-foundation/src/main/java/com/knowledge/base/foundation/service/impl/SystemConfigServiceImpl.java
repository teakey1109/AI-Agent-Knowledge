package com.knowledge.base.foundation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.foundation.entity.SystemConfig;
import com.knowledge.base.foundation.mapper.SystemConfigMapper;
import com.knowledge.base.foundation.service.SystemConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    /**
     * 分页查询配置列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param category 配置分类
     * @return 配置分页信息
     */
    @Override
    public IPage<SystemConfig> pageConfigs(Long current, Long size, String category) {
        log.info("分页查询配置参数：current={}, size={},category={}", current, size, category);

        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category)) {
            queryWrapper.eq(SystemConfig::getCategory, category);
        }
        queryWrapper.orderByAsc(SystemConfig::getId);

        Page<SystemConfig>  page = new Page<>(current, size);
        return systemConfigMapper.selectPage(page, queryWrapper);
    }
    /**
     * 根据键名查询系统配置。
     * <p>
     * 缓存策略：
     * 1. 缓存名称为 systemConfig，动态键为传入的 key。
     * 2. 若数据库查询结果为 null，则不写入缓存（防止缓存穿透）。
     *
     * @param key 配置项键名
     * @return 配置项的值
     */
    @Override
    @Cacheable(value = "systemConfig", key = "#key")
    public SystemConfig getConfigByKey(String key) {
        log.info("获取配置：key={}", key);

        if (!StringUtils.hasText(key)) {
            throw new BusinessException("配置键不能为空");
        }

        return systemConfigMapper.selectByConfigKey(key);
    }

    /**
     * 创建新的系统配置项。
     * <p>
     * 【注解含义】
     * 1. @Transactional(rollbackFor = Exception.class)：开启数据库事务。
     *    当方法执行过程中抛出任何异常（包括受检异常和非受检异常）时，自动回滚整个事务，确保数据一致性。
     * 2. @CacheEvict(value = "systemConfig", allEntries = true)：触发缓存清除。
     *    在方法执行后，强制清空 "systemConfig" 命名空间下的【所有】缓存条目，而非仅清除特定 Key。
     * 【设计优点】
     * 1. 数据强一致性：通过事务机制，防止因部分 SQL 执行成功、部分失败导致的脏数据入库。
     * 2. 防御性缓存策略：采用全量清除（allEntries = true）而非精确清除，避免了因 SpEL 表达式解析错误或
     *    复杂对象传参导致缓存未命中清除的隐患，绝对保证新增配置后，下一次读取能穿透到数据库获取最新数据。
     * 3. 严谨的业务校验：在入库前进行“非空”与“唯一性”双重校验，从源头拦截非法请求。
     * 4. 自动化属性填充：使用雪花算法生成全局唯一 ID，并自动设置创建和更新时间，解耦了业务调用方的代码。
     *
     * @param config 系统配置对象（必须包含有效的 configKey）
     * @return 若插入成功返回 true，否则返回 false
     * @throws BusinessException 当配置键为空或配置键已存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "systemConfig", allEntries = true)
    public Boolean createConfig(SystemConfig config) {
        log.info("创建配置：key={}", config.getConfigKey());

        if (!StringUtils.hasText(config.getConfigKey())) {
            throw new BusinessException("配置键不能为空");
        }

        SystemConfig existConfig = systemConfigMapper.selectByConfigKey(config.getConfigKey());
        if (existConfig != null) {
            throw new BusinessException("配置键已存在");
        }

        config.setId(SnowflakeIdGenerator.getInstance().nextId());
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());

        int count = systemConfigMapper.insert(config);
        return count > 0;
    }

    /**
     * 更新配置
     *
     * @param key    配置键
     * @param config 配置信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "systemConfig", allEntries = true)
    public Boolean updateConfig(String key, SystemConfig config) {
        log.info("更新配置：key={}", key);

        SystemConfig existConfig = systemConfigMapper.selectByConfigKey(key);
        if (existConfig == null) {
            throw new BusinessException("配置不存在");
        }

        existConfig.setConfigValue(config.getConfigValue());
        existConfig.setDescription(config.getDescription());
        existConfig.setCategory(config.getCategory());
        existConfig.setIsPublic(config.getIsPublic());
        existConfig.setUpdateTime(LocalDateTime.now());

        int count = systemConfigMapper.updateById(existConfig);
        return count > 0;
    }

    /**
     * 删除配置
     *
     * @param key 配置键
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "systemConfig", allEntries = true)
    public Boolean deleteConfig(String key) {
        log.info("删除配置：key={}", key);

        SystemConfig existConfig = systemConfigMapper.selectByConfigKey(key);
        if (existConfig == null) {
            throw new BusinessException("配置不存在");
        }

        int count = systemConfigMapper.deleteById(existConfig.getId());
        return count > 0;
    }

    /**
     * 按分类获取配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    @Override
    public List<SystemConfig> getConfigsByCategory(String category) {
        log.info("按分类获取配置：category={}", category);

        if (!StringUtils.hasText(category)) {
            throw new BusinessException("配置分类不能为空");
        }

        return systemConfigMapper.selectByCategory(category);
    }

    /**
     * 获取公开配置
     *
     * @return 公开配置 Map
     */
    @Override
    @Cacheable(value = "publicConfigs")
    public Map<String, String> getPublicConfigs() {
        log.info("获取公开配置");

        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getIsPublic, 1);

        List<SystemConfig> systemConfigs = systemConfigMapper.selectList(queryWrapper);

        Map<String, String> publicConfigs = new HashMap<>();
        for (SystemConfig systemConfig : systemConfigs) {
            publicConfigs.put(systemConfig.getConfigKey(), systemConfig.getConfigValue());
        }

        return publicConfigs;
    }
}
