package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.Country;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * Created by tangcheng on 7/16/2017.
 */
@Slf4j
public class CountryMapperTest extends BaseMapperTest {

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = getSqlSession();
        try {
            List<Country> countryList = sqlSession.selectList("mapper.CountryMapper.selectAll");
            //如果此项目中只有一个名为selectAll的方法，可以活力namespace mapper.CountryMapper.
            // 如下所示：
            // List<Country> countryList = sqlSession.selectList("selectAll");
            for (Country country : countryList) {
                Long id = country.getId();
                String name = country.getName();
                String code = country.getCode();
                log.info("{},{},{}", id, name, code);
                System.out.printf("%-4d%4s%4s \n", id, name, code);
            }

        } finally {
            sqlSession.close();
        }
    }

}