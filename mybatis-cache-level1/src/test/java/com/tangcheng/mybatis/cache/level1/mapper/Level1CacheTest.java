package com.tangcheng.mybatis.cache.level1.mapper;

import com.tangcheng.mybatis.cache.level1.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;

/**
 * MyBatis一级缓存示例
 * <p>
 * 一级缓存是SqlSession级别的缓存，默认开启，无法关闭。
 * 在同一个SqlSession中，执行相同的SQL查询，第二次会直接从缓存取数据，不再访问数据库。
 * <p>
 * Cache Key的构成：MappedStatement ID + RowBounds + SQL + 参数值
 * 只有这四项完全相同，才算"同一个查询"，才能命中缓存。
 * <p>
 * 一级缓存查找顺序（在BaseExecutor.query中）：
 * 1. 根据SQL+参数构造CacheKey
 * 2. 从localCache（HashMap）查找
 * 3. 命中 → 直接返回；未命中 → 查数据库，结果放入localCache
 * <p>
 * 适用场景：同一个业务操作中多次查询相同数据（如订单处理中多次校验用户信息）
 */
public class Level1CacheTest extends BaseMapperTest {

    /**
     * 场景1：同一个SqlSession中，两次查询同一个用户
     * <p>
     * 预期：第二次查询不执行SQL，直接从一级缓存获取
     * 关键验证：两次返回的是同一个Java对象（== true）
     */
    @Test
    public void testSameSqlSessionCacheHit() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            System.out.println("=== 第一次查询 ===");
            User user1 = mapper.selectById(1L);
            System.out.println("查询结果: " + user1);

            System.out.println("=== 第二次查询（从一级缓存获取，不执行SQL） ===");
            User user2 = mapper.selectById(1L);
            System.out.println("查询结果: " + user2);

            // 核心验证：一级缓存返回的是同一个对象引用
            assertThat(user1, sameInstance(user2));
            System.out.println("验证通过: 两次查询返回同一个对象引用，确认走了一级缓存");
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 场景2：增删改操作会清空一级缓存
     * <p>
     * 预期：update后再次查询，会重新执行SQL
     * 这就是为什么在循环中做增删改时，缓存会频繁失效
     */
    @Test
    public void testCacheInvalidAfterUpdate() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            System.out.println("=== 第一次查询 ===");
            User user1 = mapper.selectById(1L);
            System.out.println("查询结果: " + user1);

            System.out.println("=== 修改用户邮箱 ===");
            user1.setEmail("updated@test.com");
            mapper.updateEmail(user1);
            // 注意：这里不commit，update操作本身就会清空一级缓存
            System.out.println("执行了update，一级缓存已清空");

            System.out.println("=== 再次查询（缓存已失效，重新查数据库） ===");
            User user2 = mapper.selectById(1L);
            System.out.println("查询结果: " + user2);

            // update未commit但在同一事务中，DB已更新；一级缓存已被清空，所以重新查了数据库
            assertThat(user2.getEmail(), is("updated@test.com"));
            assertThat(user1 == user2, is(false));
            System.out.println("验证通过: update后缓存失效，重新查了数据库，返回了新对象");
        } finally {
            // rollback避免脏数据
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    /**
     * 场景3：手动清空一级缓存
     * <p>
     * 预期：clearCache()后再次查询，会重新执行SQL
     * 适用场景：需要强制刷新数据的业务场景
     */
    @Test
    public void testCacheInvalidAfterClearCache() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            System.out.println("=== 第一次查询 ===");
            User user1 = mapper.selectById(1L);
            System.out.println("查询结果: " + user1);

            System.out.println("=== 手动清空缓存 ===");
            sqlSession.clearCache();

            System.out.println("=== 再次查询（缓存已手动清空，重新查数据库） ===");
            User user2 = mapper.selectById(1L);
            System.out.println("查询结果: " + user2);

            // 清空缓存后，两次查询返回不同的对象（== false），但数据相同
            assertThat(user1 == user2, is(false));
            assertThat(user1.getId(), is(user2.getId()));
            assertThat(user1.getName(), is(user2.getName()));
            System.out.println("验证通过: clearCache后返回了新对象，确认重新查了数据库");
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 场景4：不同SqlSession之间无法共享一级缓存
     * <p>
     * 预期：每个SqlSession有独立的一级缓存，第二个SqlSession必须重新查数据库
     * 这是引入二级缓存的核心原因
     */
    @Test
    public void testDifferentSqlSessionCannotShareL1Cache() {
        SqlSession sqlSession1 = getSqlSession();
        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);

            System.out.println("=== SqlSession1: 第一次查询 ===");
            User user1 = mapper1.selectById(1L);
            System.out.println("查询结果: " + user1);

            System.out.println("=== SqlSession2: 查询同一个用户（无法共享SqlSession1的一级缓存） ===");
            User user2 = mapper2.selectById(1L);
            System.out.println("查询结果: " + user2);

            // 不同SqlSession返回不同对象（== false），但数据相同
            assertThat(user1 == user2, is(false));
            assertThat(user1.getId(), is(user2.getId()));
            assertThat(user1.getName(), is(user2.getName()));
            System.out.println("验证通过: 两个SqlSession各自查了数据库，说明一级缓存无法跨SqlSession共享");
            System.out.println("==> 这就是为什么需要二级缓存！");
        } finally {
            sqlSession1.close();
            sqlSession2.close();
        }
    }

    /**
     * 场景5：Cache Key的构成 —— 什么才算"同一个查询"
     * <p>
     * Cache Key = MappedStatement ID + RowBounds + SQL + 参数值
     * 同一条SQL但参数不同 → Cache Key不同 → 各自查数据库
     * <p>
     * 这个测试帮助理解：缓存不是"同一条SQL就命中"，而是"同SQL+同参数"才命中
     */
    @Test
    public void testCacheKeyComposition() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            System.out.println("=== 查询 id=1（第一次，查数据库） ===");
            User user1 = mapper.selectById(1L);
            System.out.println("查询结果: " + user1);

            System.out.println("=== 再次查询 id=1（缓存命中，不查数据库） ===");
            User user1Again = mapper.selectById(1L);
            assertThat(user1, sameInstance(user1Again));
            System.out.println("同一个SqlSession + 同一个SQL + 同一个参数 → 缓存命中");

            System.out.println("\n=== 查询 id=1001（参数变了，Cache Key不同，必须查数据库） ===");
            User user1001 = mapper.selectById(1001L);
            System.out.println("查询结果: " + user1001);
            System.out.println("同一条SQL，但参数从1变成1001 → Cache Key不同 → 查数据库");

            System.out.println("\n=== 查询 name=admin（换了一条SQL，Cache Key必然不同） ===");
            User userByName = mapper.selectByName("admin");
            System.out.println("查询结果: " + userByName);
            System.out.println("不同的MappedStatement ID(selectByName vs selectById) → Cache Key不同");

            System.out.println("\n验证通过: Cache Key = Statement ID + SQL + 参数，四项完全相同才命中缓存");
        } finally {
            sqlSession.close();
        }
    }

}
