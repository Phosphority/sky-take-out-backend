package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Slf4j
@RestController
@Api(tags = "员工接口")
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        BaseContext.setCurrentId(null);
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增员工")
    public Result add(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}", employeeDTO);
        employeeService.add(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(@ModelAttribute EmployeePageQueryDTO employeePageQueryDTO) {
        System.out.println(employeePageQueryDTO.getPage());
        System.out.println(employeePageQueryDTO.getPageSize());
        log.info("employeePageQueryDTO:{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询员工")
    public Result<Employee> findById(@PathVariable Long id) {
        log.info("员工ID:{}", id);
        Employee employee = employeeService.findById(id);
        employee.setPassword("********");
        return Result.success(employee);
    }

    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("员工更新信息:{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改员工的状态")
    public Result startOrStop(@PathVariable("status") Integer status, long id) {
        log.info("修改员工状态:{},{}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    @PutMapping("/editPassword")
    @ApiOperation("修改员工密码")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("修改密码员工id:{},新密码:{},旧密码:{}",passwordEditDTO.getEmpId(),passwordEditDTO.getOldPassword(),passwordEditDTO.getNewPassword());
        Employee employee = employeeService.findById(passwordEditDTO.getEmpId());
        if(employee.getPassword().equals(DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes()))){
            employeeService.editPassword(passwordEditDTO.getNewPassword(),passwordEditDTO.getEmpId());
        }
        return Result.success();
    }

}


























