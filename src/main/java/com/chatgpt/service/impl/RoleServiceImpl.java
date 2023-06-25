package com.chatgpt.service.impl;

import com.chatgpt.entity.Role;
import com.chatgpt.mapper.RoleDao;
import com.chatgpt.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

}
