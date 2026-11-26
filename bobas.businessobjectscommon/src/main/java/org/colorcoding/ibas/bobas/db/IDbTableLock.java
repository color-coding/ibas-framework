package org.colorcoding.ibas.bobas.db;

/**
 * 查询更新锁启用标记，用于并发控制。
 * <p>
 * 实现此接口的类型在框架查询时主动添加数据库适配器支持的更新锁提示，
 * 例如SQL Server的UPDLOCK。此标记本身不会开启事务。
 * 仓储在事务内查询时默认请求更新锁，不要求对象实现此接口。
 * 同时实现{@link IDbTableNoLock}时，以禁用更新锁为准。
 * <p>
 * 最大值查询根据MaxValue实例是否实现此接口判断，而非其getType()返回的业务对象类型。
 */
public interface IDbTableLock {

}
