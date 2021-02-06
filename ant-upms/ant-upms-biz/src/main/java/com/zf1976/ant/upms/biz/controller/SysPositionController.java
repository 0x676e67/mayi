package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.ant.upms.biz.pojo.query.PositionQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.job.PositionVO;
import com.zf1976.ant.upms.biz.service.SysPositionService;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.common.core.foundation.Result;
import com.zf1976.ant.common.security.safe.annotation.Authorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/25 5:40 下午
 */
@RestController
@RequestMapping("/api/positions")
public class SysPositionController {

    private final SysPositionService service;

    public SysPositionController(SysPositionService service) {
        this.service = service;
    }

    @PostMapping("/page")
    @Authorize("positions:list")
    public Result<IPage<PositionVO>> selectPositionPage(@RequestBody RequestPage<PositionQueryParam> requestPage) {
        return Result.success(service.selectPositionPage(requestPage));
    }

    @PostMapping("/save")
    @Authorize("positions:add")
    public Result<Optional<Void>> savePosition(@RequestBody @Validated(ValidationInsertGroup.class) PositionDTO dto) {
        return Result.success(service.savePosition(dto));
    }

    @PutMapping("/update")
    @Authorize("positions:edit")
    public Result<Optional<Void>> updatePosition(@RequestBody @Validated(ValidationUpdateGroup.class) PositionDTO dto) {
        return Result.success(service.updatePosition(dto));
    }
    @DeleteMapping("/delete")
    @Authorize("positions:del")
    public Result<Optional<Void>> deletePositionList(@RequestBody Set<Long> ids) {
        return Result.success(service.deletePositionList(ids));
    }

    @PostMapping("/download")
    @Authorize("positions:list")
    public Result<Optional<Void>> downloadPositionExcel(@RequestBody RequestPage<PositionQueryParam> requestPage, HttpServletResponse response) {
        return Result.success(service.downloadPositionExcel(requestPage, response));
    }
}
