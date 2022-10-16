package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 套餐历史数据显示
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Orders::getUserId);
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number){
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> dtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //根据订单号模糊查询
        queryWrapper.like(number != null, Orders::getNumber,number);
        queryWrapper.orderByAsc(Orders::getUserId);
        orderService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Orders> records = pageInfo.getRecords();

        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();

            //对象拷贝
            BeanUtils.copyProperties(item, orderDto);
            //分类id
            Long userId = item.getUserId();
            //根据UserId查询User对象
            User user = userService.getById(userId);
            if (user != null){
                //UserName
                String userName = user.getName();
                orderDto.setUserName(userName);
            }
            return orderDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(pageInfo);
    }

}