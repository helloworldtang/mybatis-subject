package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.SysRoleDO;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class SysRoleMapperTest extends BaseMapperTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(SysRoleMapperTest.class);

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
            List<SysRoleDO> roleList = mapper.selectAll();
            Assert.assertNotNull(roleList);
            Assert.assertTrue(roleList.size() > 0);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
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

            SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
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
            SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
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
            SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);

            SysRoleDO role = new SysRoleDO();
            role.setName("待删除角色");
            role.setEnabled(false);
            role.setCreatedBy(1L);
            role.setCreatedTime(new Date());
            role.setUpdatedTime(new Date());
            mapper.insert(role);
            Long roleId = role.getId();
            Assert.assertNotNull(roleId);

            Assert.assertEquals(1, mapper.deleteByPrimaryKey(roleId));
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
            SysRoleMapper mapper = sqlSession.getMapper(SysRoleMapper.class);
            /*
            tinyint(1) 与 Boolean 映射：
            - MyBatis 将大于 0 的值映射为 true
            - 如果 set 的值超过 tinyint(1) 范围（-128~127），会报 MysqlDataTruncation 错误
             */
            int count = mapper.updateEnabledStatus(1L, 100);
            Assert.assertEquals(1, count);
            SysRoleDO role = mapper.selectByPrimaryKey(1L);
            LOGGER.info("{}", role);
            Assert.assertTrue(role.getEnabled());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }
}
