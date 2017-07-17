package com.tangcheng.mybatis.xml.introduction.mapper;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by tangcheng on 7/18/2017.
 */
public class BaseMapperTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseMapperTest.class);

    public static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void init() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }

    protected <T> void print(List<T> list) {
        for (T t : list) {
            LOGGER.info(ToStringBuilder.reflectionToString(t, ToStringStyle.MULTI_LINE_STYLE));
        }
    }
}
