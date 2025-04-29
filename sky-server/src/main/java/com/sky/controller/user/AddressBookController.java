package com.sky.controller.user;


import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.impl.AddressBookServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "地址簿接口")
public class AddressBookController {

    @Resource
    private AddressBookServiceImpl addressBookService;

    @PostMapping
    @ApiOperation("添加地址")
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("添加地址信息为");
        addressBookService.add(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("获取当前用户的所有地址信息")
    public Result list() {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        log.info("当前用户:{}", userId);
        addressBookService.list(userId);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result delete(Long userId) {
        log.info("删除地址簿id:{}", userId);
        addressBookService.delete(userId);
        return Result.success();
    }
}














