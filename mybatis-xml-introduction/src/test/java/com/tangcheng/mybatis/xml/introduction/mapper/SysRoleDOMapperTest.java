package com.tangcheng.mybatis.xml.introduction.mapper;

import com.tangcheng.mybatis.xml.introduction.domain.SysRoleDO;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysRoleDOMapperTest extends BaseMapperTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(SysRoleDOMapperTest.class);

    @Test
    public void updateEnabledStatus() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleDOMapper mapper = sqlSession.getMapper(SysRoleDOMapper.class);
           /*
            mysql：
            tiny     1Byte，有符号数(-128,127),     无符号数(0,255)        小整数值
            SmallInt 1Byte, 有符号数(-32768,32767), 无符号数（0，65 535）  大整数值
            生成表对对应的DO时，tiny(1)对应Boolean型
            tiny(1)可以设置tiny(1)范围内的所有数字，mybatis不对这些值进行转换
            而且在将enabled字段中大于0的值，都会被翻译成true

            如果set超过tiny(1)范围的值，
            int count = mapper.updateEnabledStatus(1L, 1000);
            会报错：
            Caused by: com.mysql.jdbc.MysqlDataTruncation: Data truncation: Out of range value for column 'enabled' at row 1
            */
            int count = mapper.updateEnabledStatus(1L, 100);
            //insert返回的是影响的记录数
            Assert.assertEquals(1, count);
            SysRoleDO sysRoleDO = mapper.selectByPrimaryKey(1L);
            LOGGER.info("{}", sysRoleDO);
            Assert.assertTrue(sysRoleDO.getEnabled());
        } finally {
            //为了 不影响其它测试，这里选择rollback,
            // 由于默认的sqlSessionFactory.openSession()是不自动提交的，
            // 因此不手动执行commit也不会提交到数据库
            sqlSession.rollback();
            sqlSession.close();
        }
    }
}