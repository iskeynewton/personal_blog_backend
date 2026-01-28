# 个人博客API接口文档

## 概述

本博客的前端已经实现，包含完整的博客功能。后端需要实现以下API接口来支持留言功能。

**注意**: 项目移除了粒子背景效果以确保更好的兼容性。

## 接口列表

### 1. 获取博客文章列表

- **接口**: `GET /api/posts`
- **描述**: 获取最新文章列表，前端会统一为所有文章使用post-1.jpg作为缩略图
- **响应格式**:

```json
[
  {
    "id": 1,
    "title": "Vue 3 新特性探索",
    "excerpt": "深入了解 Vue 3 的 Composition API、Teleport 等新特性，以及它们在实际项目中的应用场景。",
    "createdAt": "2024-01-20T10:30:00Z",
    "tags": ["Vue.js", "前端开发"]
  }
]
```

### 2. 获取单篇文章

- **接口**: `GET /api/posts/{id}`
- **描述**: 获取指定ID的文章详情
- **响应格式**:

```json
{
  "id": 1,
  "title": "Vue 3 新特性探索",
  "excerpt": "深入了解 Vue 3 的 Composition API、Teleport 等新特性，以及它们在实际项目中的应用场景。",
  "content": "文章的完整内容...",
  "createdAt": "2024-01-20T10:30:00Z",
  "tags": ["Vue.js", "前端开发"]
}
```

### 3. 获取留言列表

- **接口**: `GET /api/comments`
- **描述**: 获取所有留言的列表
- **响应格式**:

```json
[
  {
    "id": 1,
    "name": "访客姓名",
    "email": "visitor@example.com",
    "message": "留言内容",
    "createdAt": "2024-01-20T10:30:00Z"
  }
]
```

### 4. 提交留言

- **接口**: `POST /api/comments`
- **描述**: 提交新的留言
- **请求体**:

```json
{
  "name": "访客姓名",
  "email": "visitor@example.com",
  "message": "留言内容"
}
```

- **响应格式**:

```json
{
  "id": 123,
  "name": "访客姓名",
  "email": "visitor@example.com",
  "message": "留言内容",
  "createdAt": "2024-01-20T10:30:00Z"
}
```

### 5. 管理员登录

- **接口**: `POST /api/auth/login`
- **描述**: 管理员登录，后端需要验证密码并返回JWT令牌
- **请求体**:

```json
{
  "password": "管理员密码"
}
```

- **响应格式**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "admin"
  }
}
```

### 6. 创建新文章

- **接口**: `POST /api/posts`
- **描述**: 创建新的博客文章（需要认证，请求头中包含JWT token）
- **请求头**: `Authorization: Bearer {jwt_token}`
- **请求体**:

```json
{
  "title": "文章标题",
  "excerpt": "文章摘要",
  "content": "文章完整内容",
  "tags": ["标签1", "标签2"]
}
```

- **响应格式**:

````json
{

### 7. 更新文章
- **接口**: `PUT /api/posts/{id}`
- **描述**: 更新指定文章，需管理员认证（JWT）
- **请求头**: `Authorization: Bearer {jwt_token}`
- **请求体** (可部分更新):
```json
{
  "title": "新的标题",
  "excerpt": "新的摘要",
  "content": "新的内容",
  "tags": ["标签1"]
}
````

### 8. 删除文章

- **接口**: `DELETE /api/posts/{id}`
- **描述**: 删除指定文章，需管理员认证（JWT）
- **请求头**: `Authorization: Bearer {jwt_token}`

### 9. 更新评论

- **接口**: `PUT /api/comments/{id}`
- **描述**: 更新指定留言。允许方式：管理员认证（JWT）或留言者通过邮箱验证码验证后更新。
- **请求体** (可部分更新):

```json
{
  "message": "新的留言内容"
}
```

### 10. 删除评论

- **接口**: `DELETE /api/comments/{id}`
- **描述**: 删除指定留言。允许方式：管理员认证（JWT）或留言者通过邮箱验证码验证后删除。

### 11. 请求留言邮箱验证码

- **接口**: `POST /api/comments/verify/request`
- **描述**: 后端向指定邮箱发送验证码，用于留言者验证编辑或删除留言的权限。
- **请求体**:

```json
{
  "email": "visitor@example.com"
}
```

### 12. 使用验证码验证留言操作

- **接口**: `POST /api/comments/{id}/verify`
- **描述**: 使用邮箱验证码验证对留言的编辑或删除权限。前端在验证通过后会继续调用对应的 `PUT /api/comments/{id}` 或 `DELETE /api/comments/{id}`。
- **请求体**:

```json
{
  "token": "123456",
  "action": "edit" // 或 "delete"
}
```

"id": 4,
"title": "文章标题",
"excerpt": "文章摘要",
"content": "文章完整内容",
"createdAt": "2024-01-23T11:00:00Z",
"tags": ["标签1", "标签2"]
}

````

## 后端实现建议

### 数据结构
```javascript
interface Comment {
  id: number;
  name: string;
  email: string;
  message: string;
  createdAt: string;
}
````

### 验证规则

- `name`: 必填，字符串，最大长度50
- `email`: 必填，有效的邮箱格式
- `message`: 必填，字符串，最小长度10，最大长度1000

### 错误处理

- 返回适当的HTTP状态码
- 提供清晰的错误信息

## 认证与权限说明

- 管理员认证：使用 `POST /api/auth/login` 返回 JWT token。前端会把 token 存入 `localStorage`，并自动在后续请求头 `Authorization: Bearer {token}` 中发送。
- 受保护的文章接口：`POST /api/posts`、`PUT /api/posts/{id}`、`DELETE /api/posts/{id}` 均需要管理员 JWT。
- 留言的保护：留言的编辑/删除允许两种方式授权：
  - 留言者邮箱验证：前端调用 `POST /api/comments/verify/request` 提交邮箱，后端生成短时效验证码并发送到邮箱；随后前端调用 `POST /api/comments/{id}/verify` 提交 `token` 与要做的 `action`（edit/delete），后端校验通过后允许进行对应的 `PUT` 或 `DELETE` 操作。
  - 管理员 JWT：管理员登录后可直接调用 `PUT /api/comments/{id}` 或 `DELETE /api/comments/{id}`。

## 后端需要配合点（给后端的说明）

- /api/posts 返回的文章对象建议包含字段 `image`（可为完整 URL 或文件名），以便前端展示缩略图；若返回文件名，前端会尝试解析为 `src/assets/images/{filename}`。
- /api/comments/verify/request 应当接受 `{ email }`，并把单次验证码发送到该邮箱，验证码与邮箱绑定并短时有效（建议 10 分钟）。
- /api/comments/{id}/verify 应当接受 `{ token, action }`，验证通过后返回 200；若失败返回 401 或 400。
- 验证通过后，前端会继续调用 `PUT /api/comments/{id}` 或 `DELETE /api/comments/{id}`；后端也可以在 `/verify` 接口内部直接执行删除或更新并返回结果，任选其一即可，但请在文档中说明行为。
- 管理接口需校验 JWT，建议在响应中返回清晰的错误码和信息，例如 401（未认证）、403（无权限）、404（资源不存在）。

## 前端本地验证建议（操作步骤）

1. 确保 `src/assets/images/` 目录下包含 README 列出的图片，Vite 在构建时会解析相对路径。若后端返回图片 URL，请保证可访问性。
2. 启动前端：

```bash
npm install
npm run dev
```

3. 管理员操作流程：先使用 `POST /api/auth/login` 获取 token，再在前端进行文章的创建/编辑/删除测试。
4. 留言者邮箱验证流程：提交留言后，点击留言下方的“通过邮箱编辑/删除”按钮，输入接收到的验证码完成操作。

如果你愿意，我可以把 README-API.md 再细化成更标准的 OpenAPI 格式，或把示例请求 curl 命令补充进去。

## 图片资源位置

博客需要以下图片资源，请将它们放置在 `src/assets/images/` 目录中：

1. **hero-bg.jpg** - 首页英雄区域背景图片
2. **profile.jpg** - 个人头像图片
3. **post-1.jpg** - 第一篇博客文章缩略图
4. **post-2.jpg** - 第二篇博客文章缩略图
5. **post-3.jpg** - 第三篇博客文章缩略图
6. **github.png** - GitHub图标 (32x32像素)

## 运行项目

```bash
cd personal-blog
npm run dev
```

项目将在 http://localhost:5173 上运行。
