package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.security.safe.annotation.Authorize;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictVO;
import com.zf1976.ant.upms.biz.service.SysDictService;
import com.zf1976.ant.upms.biz.pojo.query.DictQueryParam;
import com.zf1976.ant.common.core.foundation.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/22 9:14 下午
 */
@RestController
@RequestMapping("/api/dictionaries")
public class SysDictController {

    private final SysDictService service;

    public SysDictController(SysDictService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public Result<IPage<DictVO>> selectDictPage(@RequestBody RequestPage<DictQueryParam> requestPage) {
        return Result.success(service.selectDictPage(requestPage));
    }

    @PostMapping("/save")
    @Authorize("dict:add")
    public Result<Optional<Void>> saveDict(@RequestBody @Validated({ValidationInsertGroup.class}) DictDTO dto) {
        return Result.success(service.saveDict(dto));
    }

    @PutMapping("/update")
    @Authorize("dict:edit")
    public Result<Optional<Void>> updateDict(@RequestBody @Validated(ValidationUpdateGroup.class) DictDTO dto) {
        return Result.success(service.updateDict(dto));
    }

    @DeleteMapping("/delete")
    @Authorize("dict:del")
    public Result<Optional<Void>> deleteDictList(@RequestBody Set<Long> ids) {
        return Result.success(service.deleteDictList(ids));
    }

    @PostMapping("/download")
    @Authorize("dict:list")
    public Result<Optional<Void>> downloadDictExcel(@RequestBody RequestPage<DictQueryParam> requestPage, HttpServletResponse response) {
        return Result.success(service.downloadDictExcel(requestPage, response));
    }

}
