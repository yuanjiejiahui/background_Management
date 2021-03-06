//package com.vector.manager.douyin.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import com.vector.manager.core.common.Result;
//import com.vector.manager.douyin.request.DouyinParam;
//import com.vector.manager.douyin.service.DouYinDiscoveryEntRankItemService;
//
//@RestController
//public class DouYinDiscoveryEntRankItemController {
//
//    @Autowired
//    private DouYinDiscoveryEntRankItemService douYinDiscoveryEntRankItemService;
//
//    @GetMapping("/discoveryEntRankItemGet")
//    public Result discoveryEntRankItemGet(@RequestParam Integer type, @RequestParam(required = false) Integer version) {
//        return Result.ok().add(douYinDiscoveryEntRankItemService.discoveryEntRankItemGet(type, version));
//    }
//
//    @GetMapping("/discoveryEntRankVersionGet")
//    public Result discoveryEntRankVersionGet(@RequestParam Integer type,
//                                             @RequestParam(required = false, defaultValue = DouyinParam.COUNT) Integer count,
//                                             @RequestParam(required = false, defaultValue = DouyinParam.CURSOR) Long cursor) {
//        return Result.ok().add(douYinDiscoveryEntRankItemService.discoveryEntRankVersionGet(type, count, cursor));
//    }
//
//}
