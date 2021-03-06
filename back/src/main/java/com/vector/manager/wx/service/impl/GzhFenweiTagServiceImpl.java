package com.vector.manager.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.vector.manager.core.utils.UserUtil;
import com.vector.manager.wx.dao.GzhUserDao;
import com.vector.manager.wx.entity.GzhAccount;
import com.vector.manager.wx.entity.GzhFenweiTag;
import com.vector.manager.wx.dao.GzhFenweiTagDao;
import com.vector.manager.wx.service.IGzhAccountService;
import com.vector.manager.wx.service.IGzhFenweiTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 公众号粉丝分维解析标签 服务实现类
 * </p>
 *
 * @author YuYue
 * @since 2020-04-19
 */
@Service
public class GzhFenweiTagServiceImpl extends ServiceImpl<GzhFenweiTagDao, GzhFenweiTag> implements IGzhFenweiTagService {

    @Autowired
    private GzhUserDao gzhUserDao;
    @Autowired
    private IGzhAccountService gzhAccountService;

    @Override
    public List<GzhFenweiTag> getGzhFenweiTagList(String openId, Long gzhId) {
        List<GzhFenweiTag> gzhFenweiTagList = this.list(new QueryWrapper<GzhFenweiTag>()
                .select("distinct `name`, score, content")
                .eq("create_by", UserUtil.getUserId())
                .eq("gzh_id", gzhId)
                .eq("open_id", openId)
                .groupBy("name, score, content"));
        return gzhFenweiTagList;
    }

    @Override
    public List<Map<String, Object>> getFenweiTagList() {
        return gzhUserDao.getFenweiTagList();
    }

    @Override
    public List<GzhFenweiTag> getList(GzhFenweiTag gzhFenweiTag) {
        GzhAccount account = gzhAccountService.getDefalutGzhAccount();
        return this.list(new LambdaQueryWrapper<GzhFenweiTag>()
                .eq(GzhFenweiTag::getCreateBy, UserUtil.getUserId())
                .eq(GzhFenweiTag::getGzhId, account.getId()));
    }

}
