# mybatis-subject

MyBatis 学习与实践项目，以多个独立 Maven 模块的形式组织，覆盖从基础用法到缓存机制的各个核心知识点。

## 模块说明

| 模块 | 说明 |
|------|------|
| **mybatis-introduction** | 注解方式使用 MyBatis，无 XML 配置 |
| **mybatis-xml-introduction** | XML 方式使用 MyBatis，包含 Mapper XML 配置 |
| **mybatis-cache-level1** | MyBatis 一级缓存机制演示 |
| **mybatis-cache-level2** | MyBatis 二级缓存机制演示 |

## 技术栈

- Java 21
- MyBatis 3.5.16
- Lombok
- MySQL
- Maven（多模块聚合）

## 快速开始

### 前置条件

- JDK 21+
- Maven 3.6+
- MySQL 5.7+（需本地运行）

### 1. 初始化数据库

所有模块共用同一个数据库 `mybatis-subject`，执行对应的 SQL 文件即可：

```bash
# 建库 + 建表（含初始数据）
mysql -u root -p < mybatis-introduction/db/v1.0.0.sql
mysql -u root -p < mybatis-xml-introduction/db/v1.0.0.sql
```

> cache 模块（level1/level2）使用的是 `sys_user` 表，已包含在 `mybatis-xml-introduction/db/v1.0.0.sql` 中，无需额外执行。

### 2. 修改数据库连接

各模块的数据库配置位于 `src/main/resources/mybatis-config.xml`，默认配置为：

```xml
<property name="url" value="jdbc:mysql://localhost:3306/mybatis-subject?useSSL=false&allowPublicKeyRetrieval=true"/>
<property name="username" value="root"/>
<property name="password" value="your_password"/>
```

请将 `password` 改为你本地 MySQL 的 root 密码。

### 3. 编译并运行测试

```bash
# 编译全部模块
mvn clean compile

# 运行指定模块的测试
mvn test -pl mybatis-introduction
mvn test -pl mybatis-xml-introduction
mvn test -pl mybatis-cache-level1
mvn test -pl mybatis-cache-level2
```

### 各模块测试类说明

| 模块 | 测试类 | 演示内容 |
|------|--------|----------|
| **mybatis-introduction** | `SysUserMapperTest` | 注解方式 CRUD、动态代理、自定义 Mapper 代理 |
| | `SysRoleMapperTest` | 注解方式关联查询 |
| | `MyMapperProxyTest` | 手写 Mapper 动态代理的实现原理 |
| **mybatis-xml-introduction** | `SysUserDOMapperTest` | XML 方式 CRUD、主键回写、动态 SQL |
| | `SysRoleDOMapperTest` | XML 方式关联查询 |
| | `MyMapperProxyTest` | 手写 Mapper 动态代理的实现原理 |
| **mybatis-cache-level1** | `Level1CacheTest` | 一级缓存（SqlSession 级别）命中与失效场景 |
| **mybatis-cache-level2** | `Level2CacheTest` | 二级缓存（Mapper 级别）命中与失效场景 |
