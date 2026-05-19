package com.tangcheng.mybatis.cache.level2.mapper;

import com.tangcheng.mybatis.cache.level2.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * MyBatis二级缓存示例
 * <p>
 * 二级缓存是Mapper级别的缓存，跨SqlSession共享，需要手动开启。
 * 开启方式：在Mapper XML中添加 <cache/> 标签。
 * <p>
 * MyBatis完整的查询链路（CachingExecutor → BaseExecutor）：
 * <pre>
 *   SqlSession.query(id, param)
 *       │
 *       ▼
 *   CachingExecutor.query()              ← 二级缓存在这里
 *       │
 *       ├── 命中二级缓存 → 直接返回（不往下走）
 *       │
 *       ▼
 *   BaseExecutor.query()                 ← 一级缓存在这里
 *       │
 *       ├── 命中一级缓存 → 直接返回
 *       │
 *       ▼
 *   queryFromDatabase()                  ← 真正查数据库
 *       │
 *       └── 结果同时写入一级缓存 + 二级缓存（需commit后）
 * </pre>
 * <p>
 * 二级缓存配置参数：
 * - eviction:   淘汰策略（LRU/FIFO/SOFT/WEAK）
 * - flushInterval: 自动刷新间隔（毫秒），默认不刷新
 * - size:       最多缓存对象数，默认1024
 * - readOnly:   true=返回引用（快但不安全），false=返回序列化副本（安全但慢）
 * <p>
 * 适用场景：读多写少的业务（如后台管理系统的用户查询、系统配置查询等）
 */
public class Level2CacheTest extends BaseMapperTest {

    /**
     * 场景1：跨SqlSession共享二级缓存
     * <p>
     * SqlSession1查询用户并提交 → 数据写入二级缓存
     * SqlSession2查询同一用户 → 直接从二级缓存获取，不查数据库
     * <p>
     * 对比一级缓存：一级缓存无法跨SqlSession，每次都要查数据库
     */
    @Test
    public void testCrossSqlSessionCacheHit() {
        SqlSession sqlSession1 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);

            System.out.println("=== SqlSession1: 第一次查询（查数据库） ===");
            User user1 = mapper1.selectById(1L);
            System.out.println("查询结果: " + user1);

            // 必须 commit，数据才会从一级缓存写入二级缓存
            sqlSession1.commit();
            System.out.println("SqlSession1 commit，数据已写入二级缓存");
        } finally {
            sqlSession1.close();
        }

        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);

            System.out.println("\n=== SqlSession2: 查询同一用户（从二级缓存获取，不查数据库） ===");
            User user2 = mapper2.selectById(1L);
            System.out.println("查询结果: " + user2);

            assertThat(user2.getId(), is(1L));
            System.out.println("验证通过: SqlSession2没有执行SQL，直接从二级缓存获取了数据");
        } finally {
            sqlSession2.close();
        }
    }

    /**
     * 场景2：增删改操作会清空二级缓存
     * <p>
     * SqlSession1查询并提交 → 写入二级缓存
     * SqlSession2修改数据并提交 → 清空该Mapper的二级缓存
     * SqlSession3查询 → 缓存已失效，重新查数据库获取最新数据
     */
    @Test
    public void testCacheInvalidAfterUpdate() {
        // 先确保缓存中有数据
        SqlSession sqlSession1 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
            System.out.println("=== SqlSession1: 查询用户，写入二级缓存 ===");
            User user1 = mapper1.selectById(1L);
            System.out.println("查询结果: " + user1);
            sqlSession1.commit();
        } finally {
            sqlSession1.close();
        }

        // 修改数据，使二级缓存失效
        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
            System.out.println("\n=== SqlSession2: 修改用户邮箱 ===");
            User updateUser = new User();
            updateUser.setId(1L);
            updateUser.setEmail("new-email@test.com");
            mapper2.updateEmail(updateUser);
            System.out.println("执行update");
            // commit会同时清空该namespace下的二级缓存
            sqlSession2.commit();
            System.out.println("commit后，二级缓存已清空");
        } finally {
            sqlSession2.close();
        }

        // 验证缓存已失效，重新查数据库
        SqlSession sqlSession3 = getSqlSession();
        try {
            UserMapper mapper3 = sqlSession3.getMapper(UserMapper.class);
            System.out.println("\n=== SqlSession3: 再次查询（缓存已失效，重新查数据库） ===");
            User user3 = mapper3.selectById(1L);
            System.out.println("查询结果: " + user3);

            assertThat(user3.getEmail(), is("new-email@test.com"));
            System.out.println("验证通过: update后缓存失效，查到了最新的数据库数据");
        } finally {
            // 还原数据
            UserMapper restoreMapper = sqlSession3.getMapper(UserMapper.class);
            User restoreUser = new User();
            restoreUser.setId(1L);
            restoreUser.setEmail("admin@tangcheng.com");
            restoreMapper.updateEmail(restoreUser);
            sqlSession3.commit();
            sqlSession3.close();
        }
    }

    /**
     * 场景3：不commit则数据不会写入二级缓存
     * <p>
     * SqlSession1查询但不commit → 数据只在一級缓存中
     * SqlSession2查询同一用户 → 二级缓存没有，必须查数据库
     * <p>
     * 这个场景解释了为什么二级缓存需要在事务提交后才生效
     */
    @Test
    public void testNoCommitMeansNoL2Cache() {
        SqlSession sqlSession1 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
            System.out.println("=== SqlSession1: 查询用户（不commit） ===");
            User user1 = mapper1.selectById(1L);
            System.out.println("查询结果: " + user1);
            // 故意不commit，数据不会写入二级缓存
            System.out.println("没有commit，数据只在一级缓存中，不会写入二级缓存");
        } finally {
            sqlSession1.close();
        }

        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
            System.out.println("\n=== SqlSession2: 查询同一用户（二级缓存没有，必须查数据库） ===");
            User user2 = mapper2.selectById(1L);
            System.out.println("查询结果: " + user2);
            System.out.println("验证通过: SqlSession1没commit，SqlSession2必须重新查数据库");
        } finally {
            sqlSession2.close();
        }
    }

    /**
     * 场景4：二级缓存可能导致"脏读"的经典坑
     * <p>
     * SqlSession1查询用户(id=1)，获得email="admin@tangcheng.com"，不关闭
     * SqlSession2查询用户(id=1)，写入二级缓存，然后修改并提交
     * SqlSession1再次查询 → 从一级缓存拿到旧数据（不是最新值）
     * <p>
     * 这就是二级缓存的"脏读"问题：
     * 多个SqlSession并发操作时，一个session拿到了旧缓存数据，另一个session已经更新了数据库
     */
    @Test
    public void testDirtyReadPitfall() {
        SqlSession sqlSession1 = getSqlSession();
        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);

            System.out.println("=== SqlSession1: 查询用户 ===");
            User user1 = mapper1.selectById(1L);
            System.out.println("SqlSession1 查询结果: email=" + user1.getEmail());

            System.out.println("\n=== SqlSession2: 查询同一用户，写入二级缓存 ===");
            User user2 = mapper2.selectById(1L);
            System.out.println("SqlSession2 查询结果: email=" + user2.getEmail());
            sqlSession2.commit();

            System.out.println("\n=== SqlSession2: 修改邮箱并提交 ===");
            user2.setEmail("dirty-test@test.com");
            mapper2.updateEmail(user2);
            sqlSession2.commit();
            System.out.println("SqlSession2 已将邮箱改为: dirty-test@test.com");

            System.out.println("\n=== SqlSession1: 再次查询（从一级缓存拿到旧数据！） ===");
            User user1Again = mapper1.selectById(1L);
            System.out.println("SqlSession1 再次查询结果: email=" + user1Again.getEmail());
            System.out.println("数据库实际值: dirty-test@test.com");
            System.out.println("但SqlSession1拿到的还是: " + user1Again.getEmail());
            System.out.println("这就是二级缓存的\"脏读\"坑！SqlSession1的一级缓存还是旧数据");

            // 还原数据
            user2.setEmail("admin@tangcheng.com");
            mapper2.updateEmail(user2);
            sqlSession2.commit();
        } finally {
            sqlSession1.close();
            sqlSession2.close();
        }
    }

    /**
     * 场景5：statement级别控制 —— useCache="false" 让特定查询绕过二级缓存
     * <p>
     * 有些查询实时性要求高（如余额、库存），不希望走缓存。
     * 在<select>标签上加 useCache="false" 即可让该查询始终查库，但仍走一级缓存。
     * <p>
     * 对比：selectById（走缓存）vs selectByIdNoCache（始终查库）
     */
    @Test
    public void testUseCacheFalse() {
        // 先用selectById写入二级缓存
        SqlSession sqlSession1 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
            System.out.println("=== SqlSession1: selectById查询（走缓存） ===");
            mapper1.selectById(1L);
            sqlSession1.commit();
            System.out.println("数据已写入二级缓存");
        } finally {
            sqlSession1.close();
        }

        // selectById从二级缓存取（不查库）
        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);

            System.out.println("\n=== SqlSession2: selectById（命中二级缓存，无SQL） ===");
            mapper2.selectById(1L);

            System.out.println("\n=== SqlSession2: selectByIdNoCache（useCache=false，绕过二级缓存，必须查库） ===");
            User user = mapper2.selectByIdNoCache(1L);
            System.out.println("查询结果: " + user);
            System.out.println("验证通过: useCache=false的查询始终查数据库，不受二级缓存影响");
        } finally {
            sqlSession2.close();
        }
    }

    /**
     * 场景6：readOnly=true 的隐患 —— 缓存对象是引用，修改会影响后续查询
     * <p>
     * 当前配置 readOnly="true"，二级缓存返回的是同一个Java对象的引用。
     * 如果业务代码修改了这个对象，下次从二级缓存取到的就是被修改过的"脏"数据。
     * <p>
     * 解决方案：设 readOnly="false"，MyBatis会通过序列化返回独立副本，但性能略低。
     * 权衡：绝大多数场景用 readOnly="true"，只在并发修改风险高时用 "false"。
     */
    @Test
    public void testReadOnlyTrueMutationRisk() {
        SqlSession sqlSession1 = getSqlSession();
        try {
            UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
            System.out.println("=== SqlSession1: 查询用户并提交，写入二级缓存 ===");
            User user1 = mapper1.selectById(1L);
            System.out.println("查询结果: email=" + user1.getEmail());
            sqlSession1.commit();
        } finally {
            sqlSession1.close();
        }

        SqlSession sqlSession2 = getSqlSession();
        try {
            UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);

            System.out.println("\n=== SqlSession2: 从二级缓存获取用户 ===");
            User user2 = mapper2.selectById(1L);
            System.out.println("缓存中: email=" + user2.getEmail());

            System.out.println("\n=== 业务代码不小心修改了返回的对象 ===");
            user2.setEmail("hacked@test.com");
            System.out.println("修改后: email=" + user2.getEmail());

            sqlSession2.commit();
        } finally {
            sqlSession2.close();
        }

        // SqlSession3验证：缓存中的数据被"污染"了
        SqlSession sqlSession3 = getSqlSession();
        try {
            UserMapper mapper3 = sqlSession3.getMapper(UserMapper.class);

            System.out.println("\n=== SqlSession3: 再次从二级缓存获取 ===");
            User user3 = mapper3.selectById(1L);
            System.out.println("缓存中: email=" + user3.getEmail());
            System.out.println("数据库: email=admin@tangcheng.com（没变）");
            System.out.println("缓存数据: email=" + user3.getEmail() + "（被SqlSession2的修改污染了！）");
            System.out.println("原因: readOnly=true 返回的是同一个对象引用，改了就改了");
            System.out.println("解决: 设 readOnly=\"false\" 会返回序列化副本，互不影响");
        } finally {
            sqlSession3.close();
        }
    }

}
