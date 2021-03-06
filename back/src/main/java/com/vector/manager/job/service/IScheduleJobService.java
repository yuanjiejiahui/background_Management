package com.vector.manager.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vector.manager.job.entity.ScheduleJob;

import java.util.Map;

/**
 *
 * @author Acerola
 * @since 2021-08-04
 */
public interface IScheduleJobService extends IService<ScheduleJob> {

    Page<ScheduleJob> queryPage(Map<String, Object> params);

    /**
     * 保存定时任务
     */
    void insert(ScheduleJob scheduleJob);

    /**
     * 更新定时任务
     */
    void update(ScheduleJob scheduleJob);

    /**
     * 批量删除定时任务
     */
    void deleteBatch(Long[] jobIds);

    /**
     * 批量更新定时任务状态
     */
    int updateBatch(Long[] jobIds, Boolean status);

    /**
     * 立即执行
     */
    void run(Long[] jobIds);

    /**
     * 暂停运行
     */
    void pause(Long[] jobIds);

    /**
     * 恢复运行
     */
    void resume(Long[] jobIds);

}
