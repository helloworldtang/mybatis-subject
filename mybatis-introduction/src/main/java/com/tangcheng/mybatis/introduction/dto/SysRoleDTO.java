package com.tangcheng.mybatis.introduction.dto;

import com.tangcheng.mybatis.introduction.domain.SysRoleDO;
import com.tangcheng.mybatis.introduction.domain.SysUserDO;

public class SysRoleDTO extends SysRoleDO {
    private SysUserDO userDO;

    public SysUserDO getUserDO() { return userDO; }
    public void setUserDO(SysUserDO userDO) { this.userDO = userDO; }

    @Override
    public String toString() {
        return "SysRoleDTO{name='" + getName() + "', enabled=" + getEnabled() +
               ", userDO=" + userDO + '}';
    }
}
