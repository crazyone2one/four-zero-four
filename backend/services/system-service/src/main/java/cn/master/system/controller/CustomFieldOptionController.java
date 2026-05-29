package cn.master.system.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.system.entity.CustomFieldOption;
import cn.master.system.service.CustomFieldOptionService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 自定义字段选项 控制层。
 *
 * @author 11's papa
 * @since 2026-05-26
 */
@RestController
@Tag(name = "自定义字段选项接口")
@RequestMapping("/customFieldOption")
public class CustomFieldOptionController {

    @Autowired
    private CustomFieldOptionService customFieldOptionService;

    /**
     * 保存自定义字段选项。
     *
     * @param customFieldOption 自定义字段选项
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存自定义字段选项")
    public boolean save(@RequestBody @Parameter(description="自定义字段选项")CustomFieldOption customFieldOption) {
        return customFieldOptionService.save(customFieldOption);
    }

    /**
     * 根据主键删除自定义字段选项。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除自定义字段选项")
    public boolean remove(@PathVariable @Parameter(description="自定义字段选项主键") String id) {
        return customFieldOptionService.removeById(id);
    }

    /**
     * 根据主键更新自定义字段选项。
     *
     * @param customFieldOption 自定义字段选项
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新自定义字段选项")
    public boolean update(@RequestBody @Parameter(description="自定义字段选项主键") CustomFieldOption customFieldOption) {
        return customFieldOptionService.updateById(customFieldOption);
    }

    /**
     * 查询所有自定义字段选项。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有自定义字段选项")
    public List<CustomFieldOption> list() {
        return customFieldOptionService.list();
    }

    /**
     * 根据主键获取自定义字段选项。
     *
     * @param id 自定义字段选项主键
     * @return 自定义字段选项详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取自定义字段选项")
    public CustomFieldOption getInfo(@PathVariable @Parameter(description="自定义字段选项主键") String id) {
        return customFieldOptionService.getById(id);
    }

    /**
     * 分页查询自定义字段选项。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询自定义字段选项")
    public Page<CustomFieldOption> page(@Parameter(description="分页信息") Page<CustomFieldOption> page) {
        return customFieldOptionService.page(page);
    }

}
