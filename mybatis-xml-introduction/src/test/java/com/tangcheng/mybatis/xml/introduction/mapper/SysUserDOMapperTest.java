package com.tangcheng.mybatis.xml.introduction.mapper;

import com.tangcheng.mybatis.xml.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.xml.introduction.dto.SysRoleDTO;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

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


}