package com.tangcheng.mybatis.introduction.domain;

import java.io.Serializable;
import java.util.Date;

public class SysUserDO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String password;
    private String email;
    private Date createdTime;
    private Date updatedTime;
    private String info;
    private byte[] icon;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password == null ? null : password.trim(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim(); }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public Date getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(Date updatedTime) { this.updatedTime = updatedTime; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info == null ? null : info.trim(); }

    public byte[] getIcon() { return icon; }
    public void setIcon(byte[] icon) { this.icon = icon; }

    @Override
    public String toString() {
        return "SysUserDO{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
