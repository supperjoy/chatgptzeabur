package com.chatgpt.controller;


import cn.dev33.satoken.util.SaResult;
import com.chatgpt.entity.Redemption;
import com.chatgpt.service.RedemptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/redemption")
public class RedemptionController {
    @Autowired
    private RedemptionService redemptionService;

    @PostMapping
    public SaResult add(@RequestBody Redemption redemption){
        return redemptionService.add(redemption);
    }

    @DeleteMapping("{id}")
    public SaResult delete(@PathVariable Long id){
        return redemptionService.delete(id);
    }

    @GetMapping("/page")
    public SaResult getPage(int page,int pageSize,String search){
        return redemptionService.getPage(page,pageSize,search);
    }

    @PostMapping("/getRedem")
    public SaResult getRedem(@RequestBody Redemption redemption) throws ParseException {
        return redemptionService.getRedem(redemption);
    }
}
