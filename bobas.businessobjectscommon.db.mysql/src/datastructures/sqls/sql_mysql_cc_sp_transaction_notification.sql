DROP PROCEDURE IF EXISTS CC_SP_TRANSACTION_NOTIFICATION;
delimiter //
CREATE DEFINER=`root`@`%` PROCEDURE `CC_SP_TRANSACTION_NOTIFICATION`(
    _object_type varchar(20),                 -- BO Code
    _transaction_type char(1),                -- [A]dd, [U]pdate, [D]elete
    _num_of_cols_in_key int,                   -- Key Count
    _list_of_key_cols_tab_del varchar(255),   -- Key Names, split by ','
    _list_of_cols_val_tab_del varchar(255)    -- Key Values, split by ','
)
begin

declare _error int;                             -- return code, 0 is sucessful.
declare _error_message varchar(200);           -- return message
select _error = 0;
select _error_message = N'OK';

/*----------------------------------------------------------------------------------------------------------------------------*/

-- ADD YOUR CODE HERE

/* ----------------------------------------------------------------------------------------------------------------------------*/

-- return
select _error, _error_message;

end