package com.tangcheng.mybatis.xml.introduction.mapper;

import com.tangcheng.mybatis.xml.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.xml.introduction.dto.SysRoleDTO;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by tangcheng on 7/18/2017.
 */
public class SysUserDOMapperTest extends BaseMapperTest {


    @Test
    public void selectAllOnlyWithXml() throws Exception {
        SqlSession sqlSession = getSqlSession();
        try {
            //method selectAll non-uniqueness,so,need use fully path,
            // is to say with namespace"com.tangcheng.mybatis.xml.introduction.mapper.SysUserDOMapper"
            List<SysUserDO> userDOList = sqlSession.selectList("com.tangcheng.mybatis.xml.introduction.mapper.SysUserDOMapper.selectAll");
            print(userDOList);
        } finally {
            sqlSession.close();
        }
    }


    @Test
    public void selectAllWithMapperInterface() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDOMapper userDOMapper = sqlSession.getMapper(SysUserDOMapper.class);
            List<SysUserDO> userDOList = userDOMapper.selectAll();
            Assert.assertNotNull(userDOList);
            Assert.assertTrue(userDOList.size() > 0);
        } finally {
            sqlSession.close();
        }
    }


    @Test
    public void testSelectById() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDOMapper userDOMapper = sqlSession.getMapper(SysUserDOMapper.class);
            SysUserDO sysUserDO = userDOMapper.selectByPrimaryKey(1L);
            Assert.assertNotNull(sysUserDO);
            Assert.assertEquals("admin", sysUserDO.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectRolesByUserId() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDOMapper userDOMapper = sqlSession.getMapper(SysUserDOMapper.class);
            List<SysRoleDTO> roleDTOList = userDOMapper.selectRolesByUserId(1L);
            for (SysRoleDTO sysRoleDTO : roleDTOList) {
                LOGGER.info(sysRoleDTO.toString());
            }
            print(roleDTOList);
        } finally {
            sqlSession.close();
        }
    }


    @Test
    public void testCustomInsert() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDO userDO = createUserDO();
            SysUserDOMapper mapper = sqlSession.getMapper(SysUserDOMapper.class);
            int result = mapper.insertCustom(userDO);
            //insert返回的是影响的记录数
            Assert.assertEquals(1, result);
            //id为null,没有给id赋值，并且没有配置回写id的值
            Assert.assertNull(userDO.getId());
        } finally {
            //为了 不影响其它测试，这里选择rollback,
            // 由于默认的sqlSessionFactory.openSession()是不自动提交的，
            // 因此不手动执行commit也不会提交到数据库
            sqlSession.rollback();
            sqlSession.close();
        }

    }

    private SysUserDO createUserDO() {
        SysUserDO userDO = new SysUserDO();
        userDO.setName("customInsert");
        userDO.setPassword("123456");
        userDO.setEmail("123@tangcheng.com");
        userDO.setInfo("custom info");
        userDO.setIcon(new byte[]{1, 2, 3});//java.io.ByteArrayInputStream@1d371b2d(ByteArrayInputStream)
        userDO.setCreatedTime(new Date());
        userDO.setUpdatedTime(new Date());
        return userDO;
    }

    @Test
    public void testInsertWithGeneratedKey() {
        SqlSession sqlSession = getSqlSession();

        try {
            SysUserDO userDO = createUserDO();
            Assert.assertNull(userDO.getId());
            SysUserDOMapper mapper = sqlSession.getMapper(SysUserDOMapper.class);
            //xml中配置的insertWithGeneratedKey方法，
            // 使用useGeneratedKeys属性获取主键，
            // 将把获取的id赋值给keyProperty对应的对象属性值
            int result = mapper.insertGeneratedKeyWithJDBC(userDO);
            Assert.assertEquals(1, result);
            Assert.assertNotNull(userDO.getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }

    }

    @Test
    public void testInsertGenerateWithSelectKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDO userDO = createUserDO();
            Assert.assertNull(userDO.getId());
            SysUserDOMapper mapper = sqlSession.getMapper(SysUserDOMapper.class);
            int result = mapper.insertGenerateWithSelectKey(userDO);
            Assert.assertEquals(1, result);
            Assert.assertNotNull(userDO.getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDOMapper mapper = sqlSession.getMapper(SysUserDOMapper.class);
            long userId = 1L;
            SysUserDO sysUserDO = mapper.selectByPrimaryKey(userId);
            Assert.assertEquals("admin", sysUserDO.getName());
            String expectedNewUserName = "admin-new";
            sysUserDO.setName(expectedNewUserName);
            int result = mapper.updateByPrimaryKey(sysUserDO);
            Assert.assertEquals(1L, result);
            SysUserDO sysUserDONew = mapper.selectByPrimaryKey(userId);
            Assert.assertEquals(expectedNewUserName, sysUserDONew.getName());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testDeleteByPrimaryKey() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDOMapper mapper = sqlSession.getMapper(SysUserDOMapper.class);
            Long userId = 1L;
            SysUserDO sysUserDO = mapper.selectByPrimaryKey(userId);
            Assert.assertNotNull(sysUserDO);

            Assert.assertEquals(1L, mapper.deleteByPrimaryKey(userId));

            Assert.assertNull(mapper.selectByPrimaryKey(userId));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testSelectRolesByUserIdAndRoleId() {
        SqlSession sqlSession = getSqlSession();
        try {
            SysUserDOMapper mapper = sqlSession.getMapper(SysUserDOMapper.class);
            List<SysRoleDTO> dtoList = mapper.selectRolesByUserIdAndRoleId(1L, Boolean.TRUE);
            Assert.assertNotNull(dtoList);
            Assert.assertFalse(dtoList.isEmpty());
        } finally {
            sqlSession.close();
        }

    }


}