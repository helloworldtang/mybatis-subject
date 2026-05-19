package com.tangcheng.mybatis.cache.level2.mapper;

import com.tangcheng.mybatis.cache.level2.domain.User;

public interface UserMapper {

    User selectById(Long id);

    /**
     * useCache=false: 该查询的结果不进入二级缓存（但仍走一级缓存）
     * 适用场景：实时性要求高的查询，不希望被缓存
     */
    User selectByIdNoCache(Long id);

    int updateEmail(User user);
}
