package com.chatgpt.controller;


import cn.dev33.satoken.util.SaResult;
import com.alipay.api.AlipayApiException;
import com.chatgpt.dto.CommodityandOrder;
import com.chatgpt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("query")
    public SaResult queryOrder(@RequestBody CommodityandOrder commodityandOrder) throws AlipayApiException, InterruptedException, ParseException {
        return orderService.queryOrder(commodityandOrder);
    }

    @GetMapping("/page")
    public SaResult getOrders(int page,int pageSize,String search){
        return orderService.getPage(page,pageSize,search);
    }

    @DeleteMapping("{id}")
    public SaResult delete(@PathVariable Long id){
        return orderService.deleteOrder(id);
    }
}
