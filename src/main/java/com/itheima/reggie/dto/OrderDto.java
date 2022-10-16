package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<User> users;

    private String userName;
}
