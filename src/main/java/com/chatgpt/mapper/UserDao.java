package com.chatgpt.mapper;

import com.chatgpt.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

}
