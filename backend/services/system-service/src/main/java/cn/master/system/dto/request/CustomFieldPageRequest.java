package cn.master.system.dto.request;

import cn.master.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/5/28, 星期四
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomFieldPageRequest extends BasePageRequest {
    String scene;
}
