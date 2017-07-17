package com.tangcheng.mybatis.xml.introduction.mapper;

import com.tangcheng.mybatis.xml.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.xml.introduction.dto.SysRoleDTO;

import java.util.List;

public interface SysUserDOMapper {

    List<SysRoleDTO> selectRolesByUserId(Long userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated
     */
    int insert(SysUserDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated
     */
    SysUserDO selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated
     */
    List<SysUserDO> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SysUserDO record);
}