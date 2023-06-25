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
@TableName("redemption")
public class Redemption {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("status")
    private int status;

    @TableField("username")
    private String username;

    @TableField("time")
    private int time;

    @TableField("type")
    private String type;

    @TableField("createTime")
    private String createTime;

    @TableField("useTime")
    private String useTime;
}
