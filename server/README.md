# A2UI Server 服务端

为 A2UI Android 客户端提供数据的服务端。

## 快速开始

### 1. 安装依赖

```bash
cd server
pip install -r requirements.txt
```

### 2. 启动服务器

```bash
python a2ui_server.py
```

服务器将在 `http://localhost:5000` 启动。

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/` | GET | 服务信息 |
| `/health` | GET | 健康检查 |
| `/api/main_page` | GET | 获取首页数据 (JSON) |
| `/api/main_page/stream` | GET | 获取首页数据 (流式 SSE) |
| `/api/products` | GET | 获取商品列表 |
| `/api/products/<id>` | GET | 获取商品详情 |

## Android 客户端配置

1. 确保 Android 设备/模拟器能访问服务器
2. 模拟器使用 `10.0.2.2` 访问主机
3. 真机测试需使用局域网 IP

## 测试

```bash
# 测试首页接口
curl http://localhost:5000/api/main_page

# 测试流式接口
curl http://localhost:5000/api/main_page/stream
```
