package org.colorcoding.ibas.bobas.db;

/**
 * 查询更新锁禁用标记。
 * <p>
 * 实现此接口的类型在框架按类型判断锁策略时不主动添加更新锁，包括仓储的事务内查询。
 * 同时实现{@link IDbTableLock}时，本接口优先。子类继承此标记。
 * 此标记不改变事务隔离级别，不阻止数据库自身加锁，也不添加SQL Server的NOLOCK提示。
 * 显式调用数据库适配器的带锁参数查询方法时，仍以传入的withLock参数为准。
 * <p>
 * 最大值查询根据MaxValue实例是否实现此接口判断，而非其getType()返回的业务对象类型。
 */
public interface IDbTableNoLock {

}
