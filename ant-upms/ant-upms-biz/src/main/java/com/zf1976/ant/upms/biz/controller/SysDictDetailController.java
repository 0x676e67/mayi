package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDetailDTO;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictDetailVO;
import com.zf1976.ant.upms.biz.service.SysDictDetailService;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.query.DictDetailQueryParam;
import com.zf1976.ant.common.core.foundation.Result;
import com.zf1976.ant.common.security.safe.annotation.Authorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author mac
 * @date 2020/10/23 6:58 下午
 */
@RestController
@RequestMapping("/api/dictionaries/details")
public class SysDictDetailController {

    private final SysDictDetailService service;

    public SysDictDetailController(SysDictDetailService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public Result<IPage<DictDetailVO>> selectDictDetailPage(@RequestBody RequestPage<DictDetailQueryParam> requestPage) {
        return Result.success(service.selectDictDetailPage(requestPage));
    }

    @PostMapping("/save")
    @Authorize("dict:add")
    public Result<Optional<Void>> saveDictDetail(@RequestBody @Validated(ValidationInsertGroup.class) DictDetailDTO dto) {
        return Result.success(service.saveDictDetail(dto));
    }

    @PutMapping("/update")
    @Authorize("dict:edit")
    public Result<Optional<Void>> updateDictDetail(@RequestBody @Validated(ValidationUpdateGroup.class) DictDetailDTO dto) {
        return Result.success(service.updateDictDetail(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Authorize("dict:del")
    public Result<Optional<Void>> deleteDictDetailList(@PathVariable Long id) {
        return Result.success(service.deleteDictDetail(id));
    }


}
