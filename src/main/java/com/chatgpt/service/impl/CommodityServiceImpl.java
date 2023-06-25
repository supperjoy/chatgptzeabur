package com.chatgpt.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatgpt.entity.Commodity;
import com.chatgpt.entity.Order;
import com.chatgpt.entity.Role;
import com.chatgpt.mapper.CommodityDao;
import com.chatgpt.mapper.RoleDao;
import com.chatgpt.service.CommodityService;
import com.chatgpt.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CommodityServiceImpl extends ServiceImpl<CommodityDao, Commodity> implements CommodityService {

    @Autowired
    private CommodityService commodityService;
    @Override
    public SaResult getPage(int page, int pageSize, String search) {
        if(StpUtil.getPermissionList().contains("admin")){
            LambdaQueryWrapper<Commodity> lqw = new LambdaQueryWrapper<>();
            lqw.like(StringUtils.isNotBlank(search),Commodity::getProductName,search);
            IPage<Commodity> pageInfo = new Page<>(page,pageSize);
            IPage<Commodity> page1 = commodityService.page(pageInfo, lqw);
            return SaResult.data(page1);
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult addCommodity(Commodity commodity) {
        if(StpUtil.getPermissionList().contains("admin")){
            // 使用正则表达式匹配金额格式
            String pattern = "^(([1-9]\\d{0,7})|0)(\\.\\d{2})$";
            if(!commodity.getMoney().matches(pattern)){
                return SaResult.error("金额格式不正确！");
            }
            boolean save = commodityService.save(commodity);
            if (save){
                return SaResult.ok();
            }
            return SaResult.error("添加失败");
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult deleteCommodity(Long id) {
        if(StpUtil.getPermissionList().contains("admin")){
            boolean b = commodityService.removeById(id);
            if(b){
                return SaResult.ok();
            }
            return SaResult.error("删除失败");
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult editCommodity(Commodity commodity) {
        if(StpUtil.getPermissionList().contains("admin")){
            // 使用正则表达式匹配金额格式
            String pattern = "^(([1-9]\\d{0,7})|0)(\\.\\d{2})$";
            if(!commodity.getMoney().matches(pattern)){
                return SaResult.error("金额格式不正确！");
            }

            boolean b = commodityService.updateById(commodity);
            if(b){
                return SaResult.ok();
            }
            return SaResult.error("修改失败");
        }
        return SaResult.error("您没有权限");
    }


}
