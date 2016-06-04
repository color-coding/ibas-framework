CREATE PROCEDURE "CC_SP_TRANSACTION_NOTIFICATION"
(
    IN _object_type nvarchar(30),                       --对象类型（BOCode）
    IN _transaction_type nvarchar(1),                   --业务类型（Add:A-添加, Update:U-更新, Delete:D-删除, BeforeDelete:X-删除前, BeforeAdd:Y-添加前,BeforeUpdate:Z-更新前）
    IN _num_of_cols_in_key integer,                     --主键个数
    IN _list_of_key_cols_tab_del nvarchar(255),         --主键名称
    IN _list_of_cols_val_tab_del nvarchar(255)         --主键值
)
LANGUAGE SQLSCRIPT AS
BEGIN
--返回值
DECLARE _error integer;
--返回值(0 无错误)
DECLARE _error_message nvarchar(200);
--返回的消息
SELECT 0 INTO _error FROM  dummy;
SELECT 'OK' INTO _error_message FROM dummy;
--------------------------------------------------------------------------------------------------------------------------------
--ADD YOUR CODE HERE
--------------------------------------------------------------------------------------------------------------------------------
--返回结果
SELECT TO_INTEGER(_error) "code", _error_message "message" FROM dummy;
END