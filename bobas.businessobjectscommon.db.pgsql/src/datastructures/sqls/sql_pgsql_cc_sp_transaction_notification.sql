DROP FUNCTION IF EXISTS "CC_SP_TRANSACTION_NOTIFICATION"(character varying, character, integer, character varying, character varying);


DROP TYPE IF EXISTS "RESULT_TYPE_CC_SP_TRANSACTION_NOTIFICATION";
CREATE TYPE "RESULT_TYPE_CC_SP_TRANSACTION_NOTIFICATION" AS("Error" integer,"Message" varchar(200));

CREATE OR REPLACE FUNCTION "CC_SP_TRANSACTION_NOTIFICATION"
(
    _object_type character varying,                             /*对象类型（BOCode）*/
    _transaction_type character,                            /*业务类型（Add:A-添加, Update:U-更新, Delete:D-删除, BeforeDelete:X-删除前, BeforeAdd:Y-添加前,BeforeUpdate:Z-更新前）*/
    _num_of_cols_in_key integer,                          /*主键个数*/
    _list_of_key_cols_tab_del character varying,               /*主键名称*/
    _list_of_cols_val_tab_del character varying               /*主键值*/
) RETURNS SETOF "RESULT_TYPE_CC_SP_TRANSACTION_NOTIFICATION" AS $_$
declare 
    _error  integer;                                     /*返回值(0 无错误)*/
    _error_message varchar (200);                        /*返回的消息*/
    _result record;
BEGIN

/* 返回值 */
_error = 0;
_error_message = N'OK';

/*--------------------------------------------------------------------------------------------------------------------------------*/

/*ADD YOUR CODE HERE*/

/*--------------------------------------------------------------------------------------------------------------------------------*/

/*返回结果*/

for _result in select _error "error",_error_message "message" loop 
return next _result; 
end loop; 

END;
$_$ LANGUAGE plpgsql;
/*
select "Error","Message" from "CC_SP_TRANSACTION_NOTIFICATION"('','',0,'','','');
*/ 