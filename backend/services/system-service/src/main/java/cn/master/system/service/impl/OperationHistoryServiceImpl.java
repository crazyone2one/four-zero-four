package cn.master.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.system.entity.OperationHistory;
import cn.master.system.mapper.OperationHistoryMapper;
import cn.master.system.service.OperationHistoryService;
import org.springframework.stereotype.Service;

/**
 * 变更记录 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
public class OperationHistoryServiceImpl extends ServiceImpl<OperationHistoryMapper, OperationHistory>  implements OperationHistoryService{

}
