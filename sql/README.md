# SQL 脚本说明

本目录包含项目的数据库初始化脚本和补丁脚本。

## 目录结构

- `init.sql`: 完整的数据库初始化脚本，包含建库、建表和初始数据。
- `fix_missing_table.sql`: 修复缺失 `user_settings` 表的补丁脚本。

## 常见问题修复

### 错误：Table 'study_planner.user_settings' doesn't exist

如果在启动后端服务时遇到如下错误：

```
### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Table 'study_planner.user_settings' doesn't exist
```

**原因**：
数据库中缺少 `user_settings` 表。这可能是因为使用了旧版本的 `init.sql` 初始化数据库，或者初始化过程中该表创建失败。

**解决方案**：

请在数据库中执行 `fix_missing_table.sql` 脚本。

**执行步骤**：

1. 使用数据库管理工具（如 MySQL Workbench, Navicat, DBeaver 等）连接到您的 MySQL 数据库。
2. 打开 `fix_missing_table.sql` 文件。
3. 运行该脚本中的 SQL 语句。

或者使用命令行：

```bash
mysql -u root -p study_planner < sql/fix_missing_table.sql
```
