# GitServer-core
GitServer核心Java代码
## Design Principle
基于jgit + sshd实现git管理协议，内建一个简单的http管理服务。

## Web Page

| url        | function                                            |      |
| ---------- | --------------------------------------------------- | ---- |
| /login.do  | login user, sumbit form containe username, password |      |
| /logout.do | logout current user                                 |      |
| /ajax.do   | call ajax interface                                 |      |



## Ajxa Interface

| Action                     | name                       |      |
| -------------------------- | -------------------------- | ---- |
| QUERY_PROJECTS_ACTION      | 查询项目列表，支持分页     |      |
| CREATE_PROJECT_ACTION      | 创建项目                   |      |
| DELETE_PROJECT_ACTION      | 删除项目                   |      |
| QUERY_USERS_ACTION         | 查询用户列表               |      |
| CREATE_USER_ACTION         | 创建用户                   |      |
| UPDATE_USER_ACTION         | 修改用户                   |      |
| DELETE_USER_ACTION         | 删除用户                   |      |
| RESET_USER_PASSWORD_ACTION | 充值用户密码               |      |
| GRANT_USER_ACTION          | 给指定用户进行赋予权限     |      |
| REVOKE_USER_ACTION         | 收回给指定用户进行赋予权限 |      |
| GET_USERS_ACTION           | 获取所有用户列表           |      |
| GET_PROJECTS_ACTION        | 获取所有项目列表           |      |
|                            |                            |      |



## Build

xxxx

## Developers

| name     | role      | e-mail                                      | picture |
| -------- | --------- | ------------------------------------------- | ------- |
| woate    | developer | devops@rnkrsoft.com                         |         |
| lin-liem | developer | 996052600@qq.com |         |
|          |           |                                             |         |

