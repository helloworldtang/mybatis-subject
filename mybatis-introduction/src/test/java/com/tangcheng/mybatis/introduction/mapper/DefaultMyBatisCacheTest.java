package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.Country;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.zip.CheckedOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * mybatis-subject
 *
 * @author tangcheng
 * @date 1/14/2019 10:46 PM
 */
@Slf4j
public class DefaultMyBatisCacheTest extends BaseMapperTest {

    /**
     * mybatis默认情况下会开启一级缓存，并且一级缓存不能关闭
     */
    @Test
    public void testL1Cache() {
        SqlSession sqlSession = getSqlSession();
        Country country;
        try {
            country = sqlSession.selectOne("mapper.CountryMapper.selectCountryById", 1);
            assertThat("中国", is(country.getName()));
            country.setName("test");
            Country country2 = sqlSession.selectOne("mapper.CountryMapper.selectCountryById", 1);
            assertThat("test", is(country2.getName()));
            assertThat(country2, is(country));
        } finally {
            sqlSession.close();
        }

    }

}
