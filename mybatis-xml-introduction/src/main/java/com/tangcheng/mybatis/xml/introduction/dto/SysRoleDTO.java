package com.tangcheng.mybatis.xml.introduction.dto;

import com.tangcheng.mybatis.xml.introduction.domain.SysRoleDO;
import com.tangcheng.mybatis.xml.introduction.domain.SysUserDO;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by tangcheng on 7/18/2017.
 */
public class SysRoleDTO extends SysRoleDO {
    /**
     * 用户信息
     */
    private SysUserDO userDO;

    public SysUserDO getUserDO() {
        return userDO;
    }

    public void setUserDO(SysUserDO userDO) {
        this.userDO = userDO;
    }

    @Override
    public String toString() {
        return "SysRoleDTO{" +
                "userDO=" + ToStringBuilder.reflectionToString(userDO, ToStringStyle.MULTI_LINE_STYLE) +
                '}';
    }
}
