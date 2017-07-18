package com.tangcheng.mybatis.xml.introduction.aop;

import com.tangcheng.mybatis.xml.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.xml.introduction.mapper.BaseMapperTest;
import com.tangcheng.mybatis.xml.introduction.mapper.SysUserDOMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by tangcheng on 7/19/2017.
 */
public class MyMapperProxyTest extends BaseMapperTest {

    @Test
    public void invoke() throws Exception {
        SqlSession sqlSession = getSqlSession();
        MyMapperProxy<SysUserDOMapper> mapperProxy = new MyMapperProxy<SysUserDOMapper>(SysUserDOMapper.class, sqlSession);
        SysUserDOMapper userDOMapper = (SysUserDOMapper) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{SysUserDOMapper.class},
                mapperProxy);

        //这个方法能调用成功， 是因为SysUserDOMapper.xml中namespace属性和Mapper interface的包名是完全一样的
        List<SysUserDO> userDOList = userDOMapper.selectAll();
        Assert.assertFalse(userDOList.isEmpty());
    }

}