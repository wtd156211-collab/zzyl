package com.zzyl.nursing.controller;

import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 护理项目 Controller
 * 
 * @author wtd
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/nursing/project")
public class NursingProjectController extends BaseController
{
    // 注入护理项目 Service 接口，用于调用业务逻辑层方法
    @Autowired
    private INursingProjectService nursingProjectService;

    /**
     * 查询护理项目列表
     * 
     * 权限要求：需要 nursing:project:list 权限
     * 请求方式：GET /nursing/project/list
     * 参数说明：支持通过 NursingProject 对象进行条件查询（如名称、状态等）
     * 返回结果：分页表格数据，包含护理项目列表和分页信息
     * 
     * @param nursingProject 护理项目查询条件对象
     * @return TableDataInfo 分页响应结果
     */
    @PreAuthorize("@ss.hasPermi('nursing:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(NursingProject nursingProject)
    {
        // 启动分页（从 BaseController 继承的方法，默认每页 10 条）
        startPage();
        // 调用 Service 层查询护理项目列表
        List<NursingProject> list = nursingProjectService.selectNursingProjectList(nursingProject);
        // 将查询结果封装为表格数据格式并返回
        return getDataTable(list);
    }

    /**
     * 导出护理项目列表
     * 
     * 权限要求：需要 nursing:project:export 权限
     * 请求方式：POST /nursing/project/export
     * 功能说明：将查询到的护理项目数据导出为 Excel 文件
     * 日志记录：记录操作日志，业务类型为导出（EXPORT）
     * 
     * @param response HTTP 响应对象，用于写入 Excel 文件流
     * @param nursingProject 护理项目查询条件对象
     */
    @PreAuthorize("@ss.hasPermi('nursing:project:export')")
    @Log(title = "护理项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NursingProject nursingProject)
    {
        // 查询护理项目列表数据
        List<NursingProject> list = nursingProjectService.selectNursingProjectList(nursingProject);
        // 创建 Excel 工具类实例，指定数据类型为 NursingProject
        ExcelUtil<NursingProject> util = new ExcelUtil<NursingProject>(NursingProject.class);
        // 将数据导出为 Excel 文件并通过 response 返回给客户端
        util.exportExcel(response, list, "护理项目数据");
    }

    /**
     * 获取护理项目详细信息
     * 
     * 权限要求：需要 nursing:project:query 权限
     * 请求方式：GET /nursing/project/{id}
     * 参数说明：路径变量 id 为护理项目的主键
     * 返回结果：包含护理项目详细信息的 AjaxResult 对象
     * 
     * @param id 护理项目主键 ID
     * @return AjaxResult 包含护理项目详情的响应结果
     */
    @PreAuthorize("@ss.hasPermi('nursing:project:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        // 根据 ID 查询护理项目详情并返回成功响应
        return success(nursingProjectService.selectNursingProjectById(id));
    }

    /**
     * 新增护理项目
     * 
     * 权限要求：需要 nursing:project:add 权限
     * 请求方式：POST /nursing/project
     * 参数说明：请求体中的 JSON 数据会被自动映射为 NursingProject 对象
     * 日志记录：记录操作日志，业务类型为新增（INSERT）
     * 返回结果：执行结果（成功/失败）
     * 
     * @param nursingProject 护理项目对象（包含名称、价格、状态等信息）
     * @return AjaxResult 操作结果响应
     */
    @PreAuthorize("@ss.hasPermi('nursing:project:add')")
    @Log(title = "护理项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NursingProject nursingProject)
    {
        // 调用 Service 层新增护理项目，并转换为标准 Ajax 响应格式
        return toAjax(nursingProjectService.insertNursingProject(nursingProject));
    }

    /**
     * 修改护理项目
     * 
     * 权限要求：需要 nursing:project:edit 权限
     * 请求方式：PUT /nursing/project
     * 参数说明：请求体中的 JSON 数据会被自动映射为 NursingProject 对象（需包含 id）
     * 日志记录：记录操作日志，业务类型为修改（UPDATE）
     * 返回结果：执行结果（成功/失败）
     * 
     * @param nursingProject 护理项目对象（必须包含 id 字段）
     * @return AjaxResult 操作结果响应
     */
    @PreAuthorize("@ss.hasPermi('nursing:project:edit')")
    @Log(title = "护理项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NursingProject nursingProject)
    {
        // 调用 Service 层更新护理项目，并转换为标准 Ajax 响应格式
        return toAjax(nursingProjectService.updateNursingProject(nursingProject));
    }

    /**
     * 删除护理项目
     * 
     * 权限要求：需要 nursing:project:remove 权限
     * 请求方式：DELETE /nursing/project/{ids}
     * 参数说明：路径变量 ids 为护理项目主键数组，支持批量删除
     * 日志记录：记录操作日志，业务类型为删除（DELETE）
     * 返回结果：执行结果（成功/失败）
     * 
     * @param ids 护理项目主键 ID 数组
     * @return AjaxResult 操作结果响应
     */
    @PreAuthorize("@ss.hasPermi('nursing:project:remove')")
    @Log(title = "护理项目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // 调用 Service 层批量删除护理项目，并转换为标准 Ajax 响应格式
        return toAjax(nursingProjectService.deleteNursingProjectByIds(ids));
    }
}
