package com.chatgpt.dto;

import com.chatgpt.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;
    private String key;
}
