# 表设计

## User

| 字段名     | 中文名 | 类型    | 说明                 |
| ---------- | ------ | ------- | -------------------- |
| ID         | 用户ID | INTEGER | 系统生成，自增       |
| UserName   | 用户名 | VARCHAR | 用户名，不允许重复   |
| Passwd     | 密码   | VARCHAR | 密码，未加密         |
| Permission | 权限   | INTEGER | 权限码，越低权限越高 |

## Login

| 字段名    | 中文名        | 类型    | 说明               |
| --------- | ------------- | ------- | ------------------ |
| ID        | 登录会话ID    | INTEGER | 系统生成，自增     |
| UserID    | 用户ID        | INTEGER | 外键，关联到用户ID |
| Token     | 登录会话Token | VARCHAR | 登陆Token，UUID    |
| TimeStamp | 登陆时间戳    | BIGINT  | 登陆时的毫秒时间戳 |

## File

| 字段名     | 中文名       | 类型    | 说明                             |
| ---------- | ------------ | ------- | -------------------------------- |
| ID         | 文件ID       | INTEGER | 系统生成，自增                   |
| FileName   | 文件名       | VARCHAR | 文件名                           |
| FilePath   | 文件实际路径 | VARCHAR | 文件路径                         |
| FileType   | 文件类型     | INTEGER | 文件类型码                       |
| Permission | 权限         | INTEGER | 文件权限码，与用户表中的权限对应 |

