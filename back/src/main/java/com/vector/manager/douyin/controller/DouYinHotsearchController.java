//package com.vector.manager.douyin.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import com.vector.manager.core.common.Result;
//import com.vector.manager.douyin.request.DouyinParam;
//import com.vector.manager.douyin.service.DouYinHotsearchService;
//
///**
// * 热点视频数据
// */
//@RestController
//public class DouYinHotsearchController {
//
//    @Autowired
//    private DouYinHotsearchService douYinHotsearchService;
//
//
//    @GetMapping("/hotsearchSentencesGet")
//    public Result hotsearchSentencesGet() {
//        return Result.ok().add(douYinHotsearchService.hotsearchSentencesGet());
//    }
//
//    @GetMapping("/hotsearchTrendingSentencesGet")
//    public Result hotsearchTrendingSentencesGet(@RequestParam(required = false, defaultValue = DouyinParam.COUNT) Integer count,
//                                                @RequestParam(required = false, defaultValue = DouyinParam.CURSOR) Long cursor) {
//        return Result.ok().add(douYinHotsearchService.hotsearchTrendingSentencesGet(count, cursor));
//    }
//
//    @GetMapping("/hotsearchVideosGet")
//    public Result hotsearchVideosGet(@RequestParam String hotSentence) {
//        return Result.ok().add(douYinHotsearchService.hotsearchVideosGet(hotSentence));
//    }
//
//}
