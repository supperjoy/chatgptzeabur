package com.chatgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatgpt.entity.Order;
import com.chatgpt.entity.User;
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
public interface OrderDao extends BaseMapper<Order> {

}
