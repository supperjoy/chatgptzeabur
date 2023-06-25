package com.chatgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_order")
public class Order {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("orderId")
    private String orderId;

    @TableField("username")
    private String username;

    @TableField("money")
    private String money;

    @TableField("product")
    private String product;

    @TableField("createTime")
    private String createTime;

    @TableField("number")
    private int number;
}
