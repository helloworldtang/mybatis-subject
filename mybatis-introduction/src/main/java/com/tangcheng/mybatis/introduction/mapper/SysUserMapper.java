package com.tangcheng.mybatis.introduction.mapper;

import com.tangcheng.mybatis.introduction.domain.SysUserDO;
import com.tangcheng.mybatis.introduction.dto.SysRoleDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 注解方式实现 SysUser 的 CRUD
 * <p>
 * 对比 mybatis-xml-introduction：
 * - XML 模式：SQL 写在 Mapper XML 文件中，通过 namespace 关联接口
 * - 注解模式：SQL 直接写在接口方法的 @Select/@Insert/@Update/@Delete 注解中
 * <p>
 * 注解模式的优势：简洁直观，适合简单 SQL
 * 注解模式的劣势：复杂 SQL（动态条件、长 SQL）不如 XML 灵活
 */
public interface SysUserMapper {

    @Select("SELECT id, name, password, email, created_time, updated_time, info, icon " +
            "FROM sys_user WHERE id = #{id}")
    SysUserDO selectByPrimaryKey(Long id);

    @Select("SELECT id, name, password, email, created_time, updated_time, info, icon FROM sys_user")
    List<SysUserDO> selectAll();

    /**
     * 注解模式的 INSERT，不回写主键
     */
    @Insert("INSERT INTO sys_user(name, password, email, created_time, updated_time, info, icon) " +
            "VALUES(#{name}, #{password}, #{email}, #{createdTime}, #{updatedTime}, #{info}, #{icon})")
    int insert(SysUserDO user);

    /**
     * @Options(useGeneratedKeys=true) 等价于 XML 中的 useGeneratedKeys="true" keyProperty="id"
     * 利用 JDBC 3.0 的 getGeneratedKeys API 回写自增主键
     */
    @Insert("INSERT INTO sys_user(name, password, email, created_time, updated_time, info, icon) " +
            "VALUES(#{name}, #{password}, #{email}, #{createdTime}, #{updatedTime}, #{info}, #{icon})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertWithGeneratedKey(SysUserDO user);

    /**
     * @SelectKey 等价于 XML 中的 <selectKey> 元素
     * 通过执行额外 SQL 获取自增主键值
     */
    @Insert("INSERT INTO sys_user(name, password, email, created_time, updated_time, info, icon) " +
            "VALUES(#{name}, #{password}, #{email}, #{createdTime}, #{updatedTime}, #{info}, #{icon})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id",
            before = false, resultType = Long.class)
    int insertWithSelectKey(SysUserDO user);

    @Update("UPDATE sys_user SET name=#{name}, password=#{password}, email=#{email}, " +
            "created_time=#{createdTime}, updated_time=#{updatedTime}, info=#{info}, icon=#{icon} " +
            "WHERE id=#{id}")
    int updateByPrimaryKey(SysUserDO user);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteByPrimaryKey(Long id);

    /**
     * JOIN 查询：用户关联的角色信息
     * <p>
     * 注解模式使用 @Results + @Result 嵌套对象映射，
     * 等价于 XML 模式的 <resultMap> + <association>
     * <p>
     * 嵌套对象属性用 "userDO.name" 的点号语法，和 XML 模式一致
     */
    @Select("SELECT r.id, r.name, r.enabled, r.created_by, r.created_time, " +
            "u.name as 'userDO.name', u.email as 'userDO.email' " +
            "FROM sys_user u " +
            "INNER JOIN sys_user_role ur ON u.id = ur.user_id " +
            "INNER JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.id = #{userId}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "enabled", property = "enabled"),
            @Result(column = "created_by", property = "createdBy"),
            @Result(column = "created_time", property = "createdTime"),
            @Result(column = "userDO.name", property = "userDO.name"),
            @Result(column = "userDO.email", property = "userDO.email")
    })
    List<SysRoleDTO> selectRolesByUserId(Long userId);

    /**
     * 多参数查询使用 @Param 注解，和 XML 模式完全一致
     */
    @Select("SELECT r.id, r.name, r.enabled, r.created_by, r.created_time, " +
            "u.name as 'userDO.name', u.email as 'userDO.email' " +
            "FROM sys_user u " +
            "INNER JOIN sys_user_role ur ON u.id = ur.user_id " +
            "INNER JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.id = #{userId} AND r.enabled = #{enabled}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "enabled", property = "enabled"),
            @Result(column = "created_by", property = "createdBy"),
            @Result(column = "created_time", property = "createdTime"),
            @Result(column = "userDO.name", property = "userDO.name"),
            @Result(column = "userDO.email", property = "userDO.email")
    })
    List<SysRoleDTO> selectRolesByUserIdAndEnabled(@Param("userId") Long userId,
                                                    @Param("enabled") Boolean enabled);
}
