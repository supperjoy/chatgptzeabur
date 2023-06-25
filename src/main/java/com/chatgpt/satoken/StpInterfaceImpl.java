package com.chatgpt.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chatgpt.entity.Role;
import com.chatgpt.service.RoleService;
import com.chatgpt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LambdaQueryWrapper<Role> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Role::getId,loginId);
        List<Role> roleList = roleService.list(lqw);
        List<String> roleListString = new ArrayList<>();
        for (Role role :
                roleList) {
            roleListString.add(role.getPer());
        }
        return roleListString;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
//        UserRole userRole = getUserRole(loginId);
//        LambdaQueryWrapper<Role> lqw1 = new LambdaQueryWrapper<>();
//        lqw1.eq(Role::getRoleId,userRole.getRoleId()).eq(Role::getStatus,1);
//        List<Role> roleList = roleService.list(lqw1);
//        List<String> rolePerList = new ArrayList<>();
//        for (Role role :
//                roleList) {
//            rolePerList.add(role.getPer());
//        }
//        return rolePerList;
        return null;
    }

//    private UserRole getUserRole(Object loginId){
//        LambdaQueryWrapper<UserRole> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(UserRole::getUserId,loginId);
//        return userRoleService.getOne(lqw);
//    }
}
