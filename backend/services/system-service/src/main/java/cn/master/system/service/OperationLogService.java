package cn.master.system.service;

import cn.master.system.log.dto.LogDTO;
import com.mybatisflex.core.service.IService;
import cn.master.system.entity.OperationLog;

import java.util.List;

/**
 * 操作日志 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface OperationLogService extends IService<OperationLog> {
    void add(LogDTO log);

    void batchAdd(List<LogDTO> logs);
}
