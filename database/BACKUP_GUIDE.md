-- Database Backup Script
-- Usage: mysql -u root -p < database/schema.sql
-- Restore: mysql -u root -p database_name < backup_file.sql

-- Create a backup
SET @backup_date = DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s');
SET @backup_file = CONCAT('backup_', @backup_date, '.sql');

-- Note: To create actual backups, use mysqldump command line:
-- mysqldump -u user -p database_name > backup_file.sql
-- mysqldump -u user -p --all-databases > full_backup.sql
