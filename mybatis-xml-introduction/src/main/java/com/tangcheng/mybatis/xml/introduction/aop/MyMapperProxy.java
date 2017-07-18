package com.tangcheng.mybatis.xml.introduction.aop;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by tangcheng on 7/19/2017.
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
        String canonicalName = mapperInterface.getCanonicalName();
        LOGGER.info("是基于XML来实现ORM功能.{}" + canonicalName);
        //针对不同的sql类型，需要调用sqlSession不同的方法
        //接口方法中的参数也有很多情况，这里只考虑没有参数的情况
        List<T> list = sqlSession.selectList(canonicalName + "." + method.getName());
        //返回值 也有多种场景，此处不再做处理
        return list;
    }
}
