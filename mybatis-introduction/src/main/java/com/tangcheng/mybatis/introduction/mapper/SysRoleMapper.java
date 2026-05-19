package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.SysRoleDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SysRoleMapper {

    @Select("SELECT id, name, enabled, created_by, created_time, updated_time " +
            "FROM sys_role WHERE id = #{id}")
    SysRoleDO selectByPrimaryKey(Long id);

    @Select("SELECT id, name, enabled, created_by, created_time, updated_time FROM sys_role")
    List<SysRoleDO> selectAll();

    @Insert("INSERT INTO sys_role(name, enabled, created_by, created_time, updated_time) " +
            "VALUES(#{name}, #{enabled}, #{createdBy}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysRoleDO role);

    @Update("UPDATE sys_role SET name=#{name}, enabled=#{enabled}, " +
            "created_by=#{createdBy}, created_time=#{createdTime}, updated_time=#{updatedTime} " +
            "WHERE id=#{id}")
    int updateByPrimaryKey(SysRoleDO role);

    @Delete("DELETE FROM sys_role WHERE id = #{id}")
    int deleteByPrimaryKey(Long id);

    /**
     * 部分字段更新 + @Param 多参数
     * <p>
     * tinyint(1) 与 Boolean 的映射注意事项：
     * MySQL 中 tinyint(1) 可以存储 -128~127，MyBatis 将大于 0 的值映射为 true
     * 如果 set 的值超过 tinyint(1) 范围，会报 MysqlDataTruncation 错误
     */
    @Update("UPDATE sys_role SET enabled=#{enabled}, updated_time=now() WHERE id=#{id}")
    int updateEnabledStatus(@Param("id") Long id, @Param("enabled") Integer enabled);
}
