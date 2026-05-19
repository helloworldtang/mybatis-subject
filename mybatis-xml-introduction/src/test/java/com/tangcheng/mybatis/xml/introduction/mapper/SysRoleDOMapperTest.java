package com.tangcheng.mybatis.xml.introduction.mapper;

import com.tangcheng.mybatis.xml.introduction.domain.SysRoleDO;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class SysRoleDOMapperTest extends BaseMapperTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(SysRoleDOMapperTest.class);

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleDOMapper mapper = sqlSession.getMapper(SysRoleDOMapper.class);
            List<SysRoleDO> roleList = mapper.selectAll();
            Assert.assertNotNull(roleList);
            Assert.assertTrue(roleList.size() > 0);
            for (SysRoleDO role : roleList) {
                LOGGER.info("{}", role);
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleDOMapper mapper = sqlSession.getMapper(SysRoleDOMapper.class);
            SysRoleDO role = mapper.selectByPrimaryKey(1L);
            Assert.assertNotNull(role);
            Assert.assertEquals("管理员", role.getName());
            Assert.assertTrue(role.getEnabled());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleDO role = new SysRoleDO();
            role.setName("测试角色");
            role.setEnabled(true);
            role.setCreatedBy(1L);
            Date now = new Date();
            role.setCreatedTime(now);
            role.setUpdatedTime(now);

            SysRoleDOMapper mapper = sqlSession.getMapper(SysRoleDOMapper.class);
            int result = mapper.insert(role);
            Assert.assertEquals(1, result);
            Assert.assertNotNull(role.getId());
            LOGGER.info("inserted role id: {}", role.getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleDOMapper mapper = sqlSession.getMapper(SysRoleDOMapper.class);
            SysRoleDO role = mapper.selectByPrimaryKey(1L);
            Assert.assertNotNull(role);

            String newName = "超级管理员";
            role.setName(newName);
            int result = mapper.updateByPrimaryKey(role);
            Assert.assertEquals(1, result);

            SysRoleDO updated = mapper.selectByPrimaryKey(1L);
            Assert.assertEquals(newName, updated.getName());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testDeleteByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleDOMapper mapper = sqlSession.getMapper(SysRoleDOMapper.class);

            // 先插入一条
            SysRoleDO role = new SysRoleDO();
            role.setName("待删除角色");
            role.setEnabled(false);
            role.setCreatedBy(1L);
            role.setCreatedTime(new Date());
            role.setUpdatedTime(new Date());
            mapper.insert(role);
            Long roleId = role.getId();
            Assert.assertNotNull(roleId);

            // 删除
            int result = mapper.deleteByPrimaryKey(roleId);
            Assert.assertEquals(1, result);

            // 验证已删除
            Assert.assertNull(mapper.selectByPrimaryKey(roleId));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateEnabledStatus() {
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
            Assert.assertEquals(1, count);
            SysRoleDO sysRoleDO = mapper.selectByPrimaryKey(1L);
            LOGGER.info("{}", sysRoleDO);
            Assert.assertTrue(sysRoleDO.getEnabled());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }
}
