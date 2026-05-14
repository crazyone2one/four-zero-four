package cn.master.quartz.config;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ClusterQuartzFixedDelayJobBean extends ClusterQuartzJobBean {
}
