package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.Country;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by tangcheng on 7/16/2017.
 */
public class CountryMapperTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(CountryMapperTest.class);

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void init() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Country> countryList = sqlSession.selectList("mapper.CountryMapper.selectAll");
            //如果此项目中只有一个名为selectAll的方法，可以活力namespace mapper.CountryMapper.
            // 如下所示：
            // List<Country> countryList = sqlSession.selectList("selectAll");
            for (Country country : countryList) {
                Long id = country.getId();
                String name = country.getName();
                String code = country.getCode();
                LOGGER.info("{},{},{}", id, name, code);
                System.out.printf("%-4d%4s%4s \n", id, name, code);
            }

        } finally {
            sqlSession.close();
        }
    }

}