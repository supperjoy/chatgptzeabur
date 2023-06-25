package com.chatgpt.controller;


import cn.dev33.satoken.util.SaResult;
import com.chatgpt.entity.Commodity;
import com.chatgpt.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commodity")
public class CommodityController {
    @Autowired
    private CommodityService commodityService;


    @GetMapping
    public SaResult getList(){
        return SaResult.data(commodityService.list());
    }

    @GetMapping("/page")
    public SaResult getCommodity(int page, int pageSize, String search){
        return commodityService.getPage(page,pageSize,search);
    }

    @PostMapping
    public SaResult addCommodity(@RequestBody Commodity commodity){
        return commodityService.addCommodity(commodity);
    }

    @DeleteMapping("{id}")
    public SaResult deleteCommodity(@PathVariable Long id){
        return commodityService.deleteCommodity(id);
    }
    @PutMapping
    public SaResult editCommodity(@RequestBody Commodity commodity){
        return commodityService.editCommodity(commodity);
    }
}
