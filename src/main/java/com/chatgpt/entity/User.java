package com.chatgpt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("telephone")
    private String telephone;

    @TableField("email")
    private String email;

    @TableField("expireTime")
    private String expireTime;

    @TableField("createTime")
    private String createTime;

    @TableField("status")
    private int status;

    @TableField("recommender")
    private String recommender;

    @TableField("lastQuestionTime")
    private String lastQuestionTime;

    @TableField("currentNumber")
    private int currentNumber;

    @TableField("totalNumber")
    private int totalNumber;

    public boolean isNotEmpty() {
        // 使用 StringUtils 判断每个属性是否为空或者为空字符串
        return StringUtils.isNoneBlank(this.getUsername(), this.getTelephone(), this.getEmail(),this.getPassword());
    }

}
