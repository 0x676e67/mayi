package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.ant.upms.biz.pojo.query.DeptQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.common.core.validate.ValidationInsertGroup;
import com.zf1976.ant.common.core.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.ant.upms.biz.service.SysDepartmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/26 7:32 下午
 */
@RestController
@RequestMapping("/api/departments")
public class SysDepartmentController {

    private final SysDepartmentService service;

    public SysDepartmentController(SysDepartmentService service) {
        this.service = service;
    }

    @PostMapping("/page")
//    @Authorize("dept:list")
    public DataResult<IPage<DepartmentVO>> selectDeptPage(@RequestBody Query<DeptQueryParam> requestPage) {
        return DataResult.success(service.selectDeptPage(requestPage));
    }

    @PostMapping("/vertex/{id}")
//    @Authorize("dept:list")
    public DataResult<IPage<DepartmentVO>> deptVertex(@PathVariable Long id) {
        return DataResult.success(service.selectDeptVertex(id));
    }

    @PostMapping("/save")
//    @Authorize("dept:add")
    public DataResult<Optional<Void>> saveDept(@RequestBody @Validated({ValidationInsertGroup.class}) DepartmentDTO dto) {
        return DataResult.success(service.savaDept(dto));
    }

    @PutMapping("/update")
//    @Authorize("dept:edit")
    public DataResult<Optional<Void>> updateDept(@RequestBody @Validated(ValidationUpdateGroup.class) DepartmentDTO dto) {
        return DataResult.success(service.updateDept(dto));
    }

    @DeleteMapping("/delete")
//    @Authorize("dept:del")
    public DataResult<Optional<Void>> deleteDeptList(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteDeptList(ids));
    }

    @PostMapping("/download")
//    @Authorize("dept:list")
    public DataResult<Optional<Void>> downloadDeptExcel(@RequestBody Query<DeptQueryParam> requestPage, HttpServletResponse response) {
        return DataResult.success(service.downloadExcelDept(requestPage, response));
    }
}
