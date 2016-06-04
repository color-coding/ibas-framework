
IF EXISTS (SELECT name FROM sysobjects 
         WHERE name = 'CC_SP_TRANSACTION_NOTIFICATION' AND type = 'P')
   DROP PROCEDURE CC_SP_TRANSACTION_NOTIFICATION
GO
CREATE PROCEDURE CC_SP_TRANSACTION_NOTIFICATION
    @object_type nvarchar(20),                 -- BO Code
    @transaction_type nchar(1),                -- [A]dd, [U]pdate, [D]elete
    @num_of_cols_in_key int,                   -- Key Count
    @list_of_key_cols_tab_del nvarchar(255),   -- Key Names, split by ','
    @list_of_cols_val_tab_del nvarchar(255)    -- Key Values, split by ','
AS

begin

declare @error int                             -- return code, 0 is sucessful.
declare @error_message nvarchar(200)           -- return message
select @error = 0
select @error_message = N'OK'

--------------------------------------------------------------------------------------------------------------------------------

--ADD YOUR CODE HERE

--------------------------------------------------------------------------------------------------------------------------------

--return
select @error "Code", @error_message "Message"

end
Go
