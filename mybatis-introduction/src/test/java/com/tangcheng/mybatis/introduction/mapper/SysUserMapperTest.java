package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.introduction.dto.SysRoleDTO;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * 注解模式 SysUser 测试
 * <p>
 * 对比 mybatis-xml-introduction 的 SysUserDOMapperTest：
 * - 两者功能完全一致，只是 SQL 的定义方式不同（注解 vs XML）
 * - 测试用例一一对应，方便对比学习
 */
public class SysUserMapperTest extends BaseMapperTest {

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            List<SysUserDO> userList = mapper.selectAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.size() > 0);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            SysUserDO user = mapper.selectByPrimaryKey(1L);
            Assert.assertNotNull(user);
            Assert.assertEquals("admin", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsertWithoutGeneratedKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDO user = createUserDO();
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            int result = mapper.insert(user);
            Assert.assertEquals(1, result);
            Assert.assertNull(user.getId());
            System.out.println("insert 无回写主键: id=" + user.getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testInsertWithGeneratedKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDO user = createUserDO();
            Assert.assertNull(user.getId());
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            // @Options(useGeneratedKeys=true) 等价于 XML 的 useGeneratedKeys="true"
            int result = mapper.insertWithGeneratedKey(user);
            Assert.assertEquals(1, result);
            Assert.assertNotNull(user.getId());
            System.out.println("insertWithGeneratedKey: id=" + user.getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testInsertWithSelectKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDO user = createUserDO();
            Assert.assertNull(user.getId());
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            // @SelectKey 等价于 XML 的 <selectKey>
            int result = mapper.insertWithSelectKey(user);
            Assert.assertEquals(1, result);
            Assert.assertNotNull(user.getId());
            System.out.println("insertWithSelectKey: id=" + user.getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            long userId = 1L;
            SysUserDO user = mapper.selectByPrimaryKey(userId);
            Assert.assertEquals("admin", user.getName());

            String newName = "admin-new";
            user.setName(newName);
            int result = mapper.updateByPrimaryKey(user);
            Assert.assertEquals(1, result);

            SysUserDO updated = mapper.selectByPrimaryKey(userId);
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
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            Long userId = 1L;
            Assert.assertNotNull(mapper.selectByPrimaryKey(userId));
            Assert.assertEquals(1L, mapper.deleteByPrimaryKey(userId));
            Assert.assertNull(mapper.selectByPrimaryKey(userId));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testSelectRolesByUserId() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            List<SysRoleDTO> roleDTOList = mapper.selectRolesByUserId(1L);
            Assert.assertNotNull(roleDTOList);
            Assert.assertFalse(roleDTOList.isEmpty());
            for (SysRoleDTO dto : roleDTOList) {
                System.out.println(dto);
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectRolesByUserIdAndEnabled() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
            List<SysRoleDTO> dtoList = mapper.selectRolesByUserIdAndEnabled(1L, Boolean.TRUE);
            Assert.assertNotNull(dtoList);
            Assert.assertFalse(dtoList.isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    private SysUserDO createUserDO() {
        SysUserDO user = new SysUserDO();
        user.setName("annotationUser");
        user.setPassword("123456");
        user.setEmail("test@tangcheng.com");
        user.setInfo("annotation test");
        user.setIcon(new byte[]{1, 2, 3});
        Date now = new Date();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        return user;
    }
}
