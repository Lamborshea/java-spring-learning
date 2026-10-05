# L17: 实体映射与关系

> @OneToOne、@OneToMany、@ManyToOne、@ManyToMany

## 本节课目标

- 掌握一对一关系映射
- 掌握一对多/多对一关系
- 掌握多对多关系
- 理解 LAZY 和 EAGER 加载策略

## 关系映射速查

| 关系 | 主导方 | 注解 |
|------|--------|------|
| 一对一 | 任意方 | @OneToOne |
| 一对多 | 多方 | @OneToMany (One端) / @ManyToOne (Many端) |
| 多对多 | 任意方 | @ManyToMany |

## 加载策略

- **EAGER**: 立即加载（默认一对一一方）
- **LAZY**: 延迟加载（默认一对多、多对多）

## 下节课预告

L18: 事务管理 - @Transactional、传播行为、隔离级别
