# 智慧社区（SmartCommunity）

智慧社区综合服务平台，包含**管理后台**（物业/运营人员使用）和**商城门户**（社区居民使用）两大系统。

## 项目架构

```
SmartCommunity_zjust/
├── admin-server/          # 管理后台后端 (Spring Boot 3.3.3, Java 21)
├── mall-portal-server/    # 商城门户后端 (Spring Boot 3.3.3, Java 21)
├── admin-web/             # 管理后台前端 (Vue 3 + Element Plus + Vite)
├── mall-portal-web/       # 商城门户前端 (Vue 3 + Element Plus + Vite)
└── tasks/                 # 数据库脚本与文档
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.3.3 |
| Java 版本 | JDK 21 |
| ORM | MyBatis-Plus 3.5.7 |
| 安全认证 | Spring Security + JWT |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 3.5 (Composition API) |
| UI 组件库 | Element Plus 2.14 |
| 状态管理 | Pinia 3.0 |
| 构建工具 | Vite 8.0 |
| 包管理器 | pnpm |

## 环境要求

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21+ | 后端编译运行 |
| Maven | 3.8+ | 后端依赖管理 |
| Node.js | 18+ | 前端运行 |
| pnpm | 8+ | 前端包管理 |
| MySQL | 8.0+（兼容 5.7） | 数据库 |

---

## 一、数据库初始化

### 方案一：单体数据库（推荐，最简单）

所有表放在同一个数据库 `mall_db` 中：

```sql
-- 1. 创建数据库
CREATE DATABASE mall_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 导入主数据（包含所有表结构 + 基础数据）
USE mall_db;
SOURCE F:/SmartCommunity_zjust/tasks/mall_db（单体应用使用）.sql;

-- 3. 导入管理后台种子数据（菜单、角色、默认管理员）
SOURCE F:/SmartCommunity_zjust/admin-server/src/main/resources/init-data.sql;

-- 4. 导入商品分类表
SOURCE F:/SmartCommunity_zjust/admin-server/src/main/resources/create-category.sql;
```

> **提示：** 在 Navicat 等 GUI 工具中直接运行上述 SQL 文件更方便。

### 方案二：多数据库分离（需要修改配置）

如果你希望管理后台、商城、社区各自独立数据库：

```sql
-- 创建三个数据库
CREATE DATABASE mall_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE mall_portal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE community_portal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 分别导入
USE mall_admin;      SOURCE F:/SmartCommunity_zjust/tasks/mall_admin（管理服务）.sql;
USE mall_portal;     SOURCE F:/SmartCommunity_zjust/tasks/mall_portal（商城门户）.sql;
USE community_portal; SOURCE F:/SmartCommunity_zjust/tasks/community_portal（社区服务）.sql;
```

> ⚠️ 若使用分离模式，需修改 `admin-server` 和 `mall-portal-server` 的 `application.yml` 中数据库连接信息。

### 默认管理员账号

| 字段 | 值 |
|------|-----|
| 手机号 | `13800000000` |
| 密码 | `123456` |

---

## 二、启动后端服务

### 1. 配置数据库连接

确认 `application.yml` 中数据库连接信息与你的本地环境一致：

- **admin-server** `src/main/resources/application.yml` → 默认连接 `mall_db`
- **mall-portal-server** `src/main/resources/application.yml` → 默认连接 `mall_portal`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root        # 改成你的 MySQL 用户名
    password: 123456      # 改成你的 MySQL 密码
```

### 2. 启动 admin-server（端口 8082）

```bash
cd admin-server

# 方式一：Maven 打包运行
mvn clean package -DskipTests
java -jar target/admin-server-*.jar

# 方式二：Maven 插件直接运行
mvn spring-boot:run

# 方式三：在 IDE（IntelliJ IDEA / VS Code）中直接运行
# 主类：com.smartcommunity.admin.AdminApplication
```

### 3. 启动 mall-portal-server（端口 8081）

```bash
cd mall-portal-server

# 方式一：Maven 打包运行
mvn clean package -DskipTests
java -jar target/mall-portal-server-*.jar

# 方式二：Maven 插件直接运行
mvn spring-boot:run

# 方式三：在 IDE 中直接运行
# 主类：com.smartcommunity.mall.MallPortalApplication
```

---

## 三、启动前端应用

### 1. 管理后台前端 admin-web（端口 5173）

```bash
cd admin-web
pnpm install        # 首次运行需安装依赖
pnpm run dev        # 启动开发服务器
```

访问地址：`http://localhost:5173`

### 2. 商城门户前端 mall-portal-web（端口 5174）

```bash
cd mall-portal-web
pnpm install        # 首次运行需安装依赖
pnpm run dev        # 启动开发服务器
```

访问地址：`http://localhost:5174`

> **说明：** 前端开发服务器已配置 Vite 代理，`/api` 请求会自动转发到对应后端：
> - `admin-web` → `http://localhost:8082`
> - `mall-portal-web` → `http://localhost:8081`

---

## 四、服务端口总览

| 服务 | 端口 | 说明 |
|------|------|------|
| admin-server | 8082 | 管理后台 API |
| mall-portal-server | 8081 | 商城门户 API |
| admin-web | 5173 | 管理后台前端（Vite 开发服务器） |
| mall-portal-web | 5174 | 商城门户前端（Vite 开发服务器） |
| MySQL | 3306 | 数据库 |

---

## 五、API 接口概览

### 管理后台（admin-server :8082）

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 认证 | `/api/auth/**` | 登录、用户信息（公开：`POST /api/auth/login`） |
| 用户管理 | `/api/admin/user/**` | 管理员账号管理 |
| 角色管理 | `/api/admin/role/**` | 角色与权限 |
| 菜单管理 | `/api/admin/menu/**` | 动态菜单 |
| 区域管理 | `/api/admin/area/**` | 社区区域 |
| 商铺管理 | `/api/admin/store/**` | 商铺信息 |
| 商品管理 | `/api/admin/goods/**` | 商品 CRUD |
| 分类管理 | `/api/admin/category/**` | 商品分类 |
| 订单管理 | `/api/admin/order/**` | 订单列表 |
| 专题管理 | `/api/admin/special/**` | 营销专题 |
| 通知公告 | `/api/admin/community/notice/**` | 社区通知 |
| 停车管理 | `/api/admin/community/parking/**` | 车位管理 |
| 访客管理 | `/api/admin/community/visitor/**` | 访客登记 |
| 投诉建议 | `/api/admin/community/complaint/**` | 投诉处理 |
| 收费管理 | `/api/admin/community/charge/**` | 物业收费 |

### 商城门户（mall-portal-server :8081）

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 用户 | `/api/user/**` | 登录、注册（公开） |
| 商品 | `/api/goods/**` | 商品浏览（公开） |
| 分类 | `/api/category/**` | 分类浏览（公开） |
| 商铺 | `/api/store/**` | 商铺浏览（公开） |
| 专题 | `/api/special/**` | 专题浏览（公开） |
| 购物车 | `/api/cart/**` | 购物车（需登录） |
| 订单 | `/api/order/**` | 订单管理（需登录） |
| 收藏 | `/api/favorite/**` | 商品收藏（需登录） |

---

## 六、常见问题

### Q: 前端启动报错 "Cannot find module ..."
```bash
# 删除 node_modules 后重新安装
cd admin-web  # 或 mall-portal-web
rm -rf node_modules
pnpm install
```

### Q: 后端启动报数据库连接失败
- 确认 MySQL 服务已启动
- 确认 `application.yml` 中用户名和密码正确
- 确认数据库已创建并导入了 SQL 脚本

### Q: 前端请求接口 401 未授权
- 确认后端服务已启动
- 前端登录后 token 存储在 `localStorage`（管理后台 key 为 `admin_token`，商城门户 key 为 `token`）
- 清除 localStorage 后重新登录

### Q: Maven 依赖下载慢
- 配置阿里云 Maven 镜像（`~/.m2/settings.xml`）
- 或使用代理：`mvn -DproxySet=true -DproxyHost=127.0.0.1 -DproxyPort=7890 spring-boot:run`

---

## 七、项目文档

- `tasks/智慧社区项目介绍(1).xlsx` — 项目业务介绍
- `tasks/商城端-数据库说明文档(1).docx` — 商城数据库设计说明
- `style.md` — 前端 UI 风格指南
