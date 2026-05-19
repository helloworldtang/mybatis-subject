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

```bash
# 编译全部模块
mvn clean compile

# 运行指定模块的测试
mvn test -pl mybatis-introduction
```

各模块需要本地 MySQL 实例，数据库配置在各模块的 `src/test/resources/` 下。
