package org.csu.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.csu.api.persistence.UserMapper;

@SpringBootTest
class MallApiApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectUser() {
        System.out.println(userMapper.selectList(null));
    }

    @Test
    void contextLoads() {
    }


    @Test
    void testPage() {
        System.out.println(JSON.toJSONString(testPageTool()));
    }

    CommonResponse<Page<User>> testPageTool() {
        Page<User> userPage = new Page<>();
        userPage.setCurrent(3);
        userPage.setSize(2);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userMapper.selectPage(userPage, queryWrapper);
        return CommonResponse.createForSuccess(userPage);
    }

}
