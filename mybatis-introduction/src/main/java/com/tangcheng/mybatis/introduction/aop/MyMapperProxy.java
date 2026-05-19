package com.tangcheng.mybatis.introduction.aop;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义 Mapper 代理，演示 MyBatis 底层如何通过动态代理将接口方法映射到 SQL
 * <p>
 * MyBatis 内部也是通过 JDK 动态代理实现的：
 * 1. SqlSession.getMapper(MapperInterface.class) 返回一个代理对象
 * 2. 调用代理对象的方法时，InvocationHandler 将方法名映射到 namespace + method
 * 3. 根据映射找到对应的 MappedStatement 执行 SQL
 * <p>
 * 注解模式和：接口全限定名 + 方法名 = MappedStatement ID
 * XML 模式是：namespace + select|insert|update|delete 的 id = MappedStatement ID
 */
public class MyMapperProxy<T> implements InvocationHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(MyMapperProxy.class);

    private final Class<T> mapperInterface;
    private final SqlSession sqlSession;

    public MyMapperProxy(Class<T> mapperInterface, SqlSession sqlSession) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String statementId = mapperInterface.getCanonicalName() + "." + method.getName();
        LOGGER.info("代理调用: {}", statementId);
        List<T> list = sqlSession.selectList(statementId);
        return list;
    }
}
