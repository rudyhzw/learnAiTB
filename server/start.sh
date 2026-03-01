#!/bin/bash
# A2UI Server 启动脚本

cd "$(dirname "$0")"

# 检查并创建虚拟环境
if [ ! -d "venv" ]; then
    echo "创建虚拟环境..."
    python3 -m venv venv
fi

# 激活虚拟环境
source venv/bin/activate

# 安装依赖
echo "安装依赖..."
pip install -r requirements.txt

# 启动服务器
echo "启动 A2UI Server..."
python a2ui_server.py
