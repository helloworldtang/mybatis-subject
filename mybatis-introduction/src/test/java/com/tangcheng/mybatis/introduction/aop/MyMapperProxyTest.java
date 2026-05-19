package com.tangcheng.mybatis.introduction.aop;

import com.tangcheng.mybatis.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.introduction.mapper.BaseMapperTest;
import com.tangcheng.mybatis.introduction.mapper.SysUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 自定义 Mapper 代理测试
 * <p>
 * 演示 MyBatis 底层动态代理原理：
 * 接口全限定名 + 方法名 = MappedStatement ID
 * <p>
 * 注解模式下，@Select 等注解也会注册为 MappedStatement，
 * 所以无论是 XML 还是注解，代理都能正确路由到对应的 SQL。
 */
public class MyMapperProxyTest extends BaseMapperTest {

    @Test
    public void testProxyInvocation() {
        SqlSession sqlSession = getSqlSession();
        try {
            MyMapperProxy<SysUserMapper> mapperProxy = new MyMapperProxy<>(SysUserMapper.class, sqlSession);
            SysUserMapper userMapper = (SysUserMapper) Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{SysUserMapper.class},
                    mapperProxy);

            List<SysUserDO> userList = userMapper.selectAll();
            Assert.assertFalse(userList.isEmpty());
            System.out.println("动态代理查询成功，共 " + userList.size() + " 条记录");
        } finally {
            sqlSession.close();
        }
    }
}
