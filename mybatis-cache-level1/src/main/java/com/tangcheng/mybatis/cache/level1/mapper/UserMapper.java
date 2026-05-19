package com.tangcheng.mybatis.cache.level1.mapper;

import com.tangcheng.mybatis.cache.level1.domain.User;

public interface UserMapper {

    User selectById(Long id);

    User selectByName(String name);

    int updateEmail(User user);
}
