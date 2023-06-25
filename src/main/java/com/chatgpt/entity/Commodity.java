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
@TableName("commodity")
public class Commodity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("productName")
    private String productName;

    @TableField("money")
    private String money;

    @TableField("description")
    private String description;

    @TableField("inventory")
    private int inventory;

    @TableField("time")
    private String time;

    @TableField("number")
    private int number;
}
