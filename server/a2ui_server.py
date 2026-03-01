#!/usr/bin/env python3
"""
A2UI Server - 支持 WebSocket 实时推送
"""

from flask import Flask, jsonify
from flask_socketio import SocketIO
import json
import time

app = Flask(__name__)
app.config['SECRET_KEY'] = 'a2ui_secret_key'
socketio = SocketIO(app, cors_allowed_origins="*", async_mode='threading')

# 模拟商品数据 - 与客户端一致
PRODUCTS = [
    {
        "id": 1, 
        "title": "【好评如潮】纯棉T恤女短袖 宽松百搭 学生宿舍必备", 
        "price": "59.90", 
        "originalPrice": "89.90", 
        "sales": 12580, 
        "shop": "优品汇旗舰店", 
        "location": "杭州", 
        "images": [
            {"url": "https://picsum.photos/seed/tshirt1/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/tshirt1a/400/400", "type": "detail"},
            {"url": "https://picsum.photos/seed/tshirt1b/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/tshirt1/100/100"],
        "isFreeShipping": True, 
        "isVip": False, 
        "coupon": "满50减5",
        "tags": ["好评", "热卖"],
        "rating": 4.9,
        "comments": 2560
    },
    {
        "id": 2, 
        "title": "【限时特惠】无线蓝牙耳机 降噪运动耳机 苹果安卓通用", 
        "price": "159.00", 
        "originalPrice": "299.00", 
        "sales": 36800, 
        "shop": "数码精品屋", 
        "location": "深圳", 
        "images": [
            {"url": "https://picsum.photos/seed/earphone2/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/earphone2a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/earphone2/100/100"],
        "isFreeShipping": True, 
        "isVip": True, 
        "coupon": None,
        "tags": ["降噪", "运动"],
        "rating": 4.8,
        "comments": 8900
    },
    {
        "id": 3, 
        "title": "【爆款】北欧风简约客厅吊灯 创意设计师款", 
        "price": "288.00", 
        "originalPrice": "588.00", 
        "sales": 8920, 
        "shop": "家居生活馆", 
        "location": "广州", 
        "images": [
            {"url": "https://picsum.photos/seed/lamp3/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/lamp3a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/lamp3/100/100"],
        "isFreeShipping": False, 
        "isVip": False, 
        "coupon": "满200减20",
        "tags": ["北欧", "设计师"],
        "rating": 4.7,
        "comments": 1250
    },
    {
        "id": 4, 
        "title": "【新品上市】男士休闲西裤 商务直筒 免烫抗皱", 
        "price": "128.00", 
        "originalPrice": "256.00", 
        "sales": 15800, 
        "shop": "潮牌服饰专营", 
        "location": "上海", 
        "images": [
            {"url": "https://picsum.photos/seed/pants4/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/pants4a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/pants4/100/100"],
        "isFreeShipping": True, 
        "isVip": False, 
        "coupon": None,
        "tags": ["免烫", "商务"],
        "rating": 4.8,
        "comments": 3200
    },
    {
        "id": 5, 
        "title": "【旗舰店】SK-II神仙水精华液 护肤套装 美白保湿", 
        "price": "690.00", 
        "originalPrice": "990.00", 
        "sales": 25600, 
        "shop": "美妆护肤坊", 
        "location": "北京", 
        "images": [
            {"url": "https://picsum.photos/seed/skincare5/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/skincare5a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/skincare5/100/100"],
        "isFreeShipping": True, 
        "isVip": True, 
        "coupon": "满500减50",
        "tags": ["旗舰", "美白"],
        "rating": 4.9,
        "comments": 15600
    },
    {
        "id": 6, 
        "title": "【618预售】iPhone15手机壳 透明防摔 简约大气", 
        "price": "29.90", 
        "originalPrice": "59.90", 
        "sales": 45600, 
        "shop": "数码精品屋", 
        "location": "深圳", 
        "images": [
            {"url": "https://picsum.photos/seed/phonecase6/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/phonecase6a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/phonecase6/100/100"],
        "isFreeShipping": True, 
        "isVip": False, 
        "coupon": None,
        "tags": ["防摔", "透明"],
        "rating": 4.6,
        "comments": 9800
    },
    {
        "id": 7, 
        "title": "【出口品质】乳胶枕头 护颈枕 泰国进口 助眠舒适", 
        "price": "89.00", 
        "originalPrice": "199.00", 
        "sales": 32100, 
        "shop": "家居生活馆", 
        "location": "广州", 
        "images": [
            {"url": "https://picsum.photos/seed/pillow7/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/pillow7a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/pillow7/100/100"],
        "isFreeShipping": True, 
        "isVip": False, 
        "coupon": "满80减8",
        "tags": ["泰国进口", "助眠"],
        "rating": 4.8,
        "comments": 7800
    },
    {
        "id": 8, 
        "title": "【热销10万】纯棉四件套 床上用品 简约北欧风", 
        "price": "199.00", 
        "originalPrice": "399.00", 
        "sales": 89600, 
        "shop": "家居生活馆", 
        "location": "杭州", 
        "images": [
            {"url": "https://picsum.photos/seed/bedsheet8/400/400", "type": "main"},
            {"url": "https://picsum.photos/seed/bedsheet8a/400/400", "type": "detail"}
        ],
        "thumbnails": ["https://picsum.photos/seed/bedsheet8/100/100"],
        "isFreeShipping": True, 
        "isVip": True, 
        "coupon": None,
        "tags": ["热销", "北欧"],
        "rating": 4.9,
        "comments": 23400
    },
]

BANNERS = [
    {"id": 1, "title": "618年中大促", "subtitle": "全场5折起", "image": {"url": "https://picsum.photos/seed/banner1/750/320"}, "tags": ["618", "大促"]},
    {"id": 2, "title": "超级红包", "subtitle": "最高888元", "image": {"url": "https://picsum.photos/seed/banner2/750/320"}, "tags": ["红包", "福利"]},
    {"id": 3, "title": "新品首发", "subtitle": "限时优惠", "image": {"url": "https://picsum.photos/seed/banner3/750/320"}, "tags": ["新品", "首发"]},
    {"id": 4, "title": "品牌特卖", "subtitle": "正品保障", "image": {"url": "https://picsum.photos/seed/banner4/750/320"}, "tags": ["品牌", "正品"]},
]

CATEGORIES = [
    {"id": 1, "name": "天猫", "iconUrl": "https://placehold.co/80x80/FF6600/FFFFFF?text=天猫", "color": "#FF6600"},
    {"id": 2, "name": "淘宝", "iconUrl": "https://placehold.co/80x80/FF6600/FFFFFF?text=淘宝", "color": "#FF6600"},
    {"id": 3, "name": "聚划算", "iconUrl": "https://placehold.co/80x80/FF9500/FFFFFF?text=聚划算", "color": "#FF9500"},
    {"id": 4, "name": "天猫国际", "iconUrl": "https://placehold.co/80x80/007AFF/FFFFFF?text=国际", "color": "#007AFF"},
    {"id": 5, "name": "淘宝直播", "iconUrl": "https://placehold.co/80x80/FF2D55/FFFFFF?text=直播", "color": "#FF2D55"},
    {"id": 6, "name": "菜鸟驿站", "iconUrl": "https://placehold.co/80x80/34C759/FFFFFF?text=菜鸟", "color": "#34C759"},
    {"id": 7, "name": "支付宝", "iconUrl": "https://placehold.co/80x80/007AFF/FFFFFF?text=支付宝", "color": "#007AFF"},
    {"id": 8, "name": "饿了么", "iconUrl": "https://placehold.co/80x80/FF6600/FFFFFF?text=饿了么", "color": "#FF6600"},
]

HOT_SEARCHES = [
    {"word": "连衣裙", "hot": True, "icon": "🔥"},
    {"word": "T恤", "hot": False},
    {"word": "运动鞋", "hot": True, "icon": "🔥"},
    {"word": "蓝牙耳机", "hot": False},
    {"word": "吹风机", "hot": False},
    {"word": "面膜", "hot": False},
    {"word": "洗衣液", "hot": False},
]


def generate_main_page_messages():
    """生成首页 A2UI 消息"""
    messages = []
    
    messages.append({
        "version": "v0.9",
        "createSurface": {
            "surfaceId": "taobao_main",
            "catalogId": "https://a2ui.org/specification/v0_9/standard_catalog.json"
        }
    })
    
    messages.append({
        "version": "v0.9",
        "updateDataModel": {
            "surfaceId": "taobao_main",
            "path": "/",
            "data": {
                "banners": BANNERS,
                "categories": CATEGORIES,
                "hotSearches": HOT_SEARCHES,
                "products": PRODUCTS.copy(),
                "recommendTitle": "为你推荐"
            }
        }
    })
    
    messages.append({
        "version": "v0.9",
        "updateComponents": {
            "surfaceId": "taobao_main",
            "components": [
                {"id": "root", "component": {"Column": {"spacing": 0, "backgroundColor": "#F5F5F5"}}, "children": {"explicitList": ["header", "banner", "hot_search", "categories", "recommend_title", "product_grid"]}},
                {"id": "header", "component": {"Row": {"backgroundColor": "#FF6600", "padding": 12, "align": "center"}}, "children": {"explicitList": ["scan_icon", "search_container", "message_icon"]}},
                {"id": "scan_icon", "component": {"Text": {"text": "📷", "fontSize": 20}}, "events": {"onClick": "navigate_scan"}},
                {"id": "search_container", "component": {"Row": {"backgroundColor": "#FFFFFF", "flexWeight": 1, "marginStart": 8, "marginEnd": 8, "padding": 8, "align": "center"}}, "children": {"explicitList": ["search_hint"]}, "events": {"onClick": "navigate_search"}},
                {"id": "search_hint", "component": {"Text": {"text": "搜索商品、品牌", "color": "#999999", "fontSize": 14}}},
                {"id": "message_icon", "component": {"Text": {"text": "📩", "fontSize": 20}}, "events": {"onClick": "show_messages"}},
                {"id": "banner", "component": {"Column": {"height": "160"}}, "children": {"dataBinding": "/banners", "templateComponentId": "banner_item"}},
                {"id": "banner_item", "component": {"Card": {"height": "160", "cornerRadius": "0"}}, "children": {"explicitList": ["banner_image", "banner_content"]}},
                {"id": "banner_image", "component": {"Image": {"src": {"binding": "image.url"}, "width": "match", "height": "match", "objectFit": "cover"}}},
                {"id": "banner_content", "component": {"Column": {"align": "center", "spacing": "8"}}, "children": {"explicitList": ["banner_title", "banner_subtitle"]}},
                {"id": "banner_title", "component": {"Text": {"text": {"binding": "title"}, "color": "#FFFFFF", "fontSize": "20", "fontWeight": True}}},
                {"id": "banner_subtitle", "component": {"Text": {"text": {"binding": "subtitle"}, "color": "#FFFFFF", "fontSize": "14"}}},
                {"id": "hot_search", "component": {"Row": {"backgroundColor": "#FFFFFF", "padding": "12", "spacing": "8", "align": "center"}}, "children": {"explicitList": ["hot_label", "hot_tags"]}},
                {"id": "hot_label", "component": {"Text": {"text": "🔥 热搜", "color": "#FF6600", "fontSize": "12", "fontWeight": True}}},
                {"id": "hot_tags", "component": {"Row": {"spacing": "8"}}, "children": {"dataBinding": "/hotSearches", "templateComponentId": "hot_tag"}},
                {"id": "hot_tag", "component": {"Text": {"text": {"binding": "word"}, "color": "#666666", "fontSize": "12", "backgroundColor": "#F5F5F5", "padding": "6"}}},
                {"id": "categories", "component": {"Column": {"backgroundColor": "#FFFFFF", "padding": "8", "marginTop": "8"}}, "children": {"explicitList": ["category_grid"]}},
                {"id": "category_grid", "component": {"Grid": {"columns": "4", "spacing": "8"}}, "children": {"dataBinding": "/categories", "templateComponentId": "category_item"}},
                {"id": "category_item", "component": {"Column": {"align": "center", "spacing": "4", "padding": "8"}}, "children": {"explicitList": ["category_icon", "category_name"]}, "events": {"onClick": "navigate_category"}},
                {"id": "category_icon", "component": {"Image": {"src": {"binding": "iconUrl"}, "width": "40", "height": "40"}}},
                {"id": "category_name", "component": {"Text": {"text": {"binding": "name"}, "fontSize": "12", "color": "#333333"}}},
                {"id": "recommend_title", "component": {"Text": {"text": "为你推荐", "fontSize": "16", "fontWeight": True, "color": "#333333", "padding": "12"}}},
                {"id": "product_grid", "component": {"Grid": {"columns": "2", "spacing": "8", "padding": "8"}}, "children": {"dataBinding": "/products", "templateComponentId": "product_item"}},
                {"id": "product_item", "component": {"Card": {"backgroundColor": "#FFFFFF", "cornerRadius": "8"}}, "children": {"explicitList": ["product_image", "product_content"]}, "events": {"onClick": "increment_price"}},
                {"id": "product_image", "component": {"Image": {"src": {"binding": "images.0.url"}, "width": "match", "height": "160", "objectFit": "cover"}}},
                {"id": "product_content", "component": {"Column": {"padding": "8", "spacing": "6"}}, "children": {"explicitList": ["product_title", "product_price", "product_sales"]}},
                {"id": "product_title", "component": {"Text": {"text": {"binding": "title"}, "fontSize": "12", "color": "#333333", "maxLines": "2"}}},
                {"id": "product_price", "component": {"Text": {"text": {"literalString": "¥", "binding": "price"}, "fontSize": "16", "fontWeight": True, "color": "#FF6600"}}},
                {"id": "product_sales", "component": {"Text": {"text": {"binding": "sales"}, "fontSize": "10", "color": "#999999"}}},
            ]
        }
    })
    
    return messages


@socketio.on('connect')
def handle_connect():
    print(f"客户端连接")
    socketio.emit('connected', {'status': 'ok'})


@socketio.on('disconnect')
def handle_disconnect():
    print(f"客户端断开")


@socketio.on('request_main_page')
def handle_request_main_page(data=None):
    print(f"收到请求首页数据")
    messages = generate_main_page_messages()
    for msg in messages:
        socketio.emit('a2ui_message', msg)
        time.sleep(0.2)


@socketio.on('increment_price')
def handle_increment_price(data=None):
    print(f"增加价格")
    
    if PRODUCTS:
        try:
            current_price = float(PRODUCTS[0]['price'])
            PRODUCTS[0]['price'] = f"{current_price + 1:.2f}"
        except:
            pass
    
    update_msg = {
        "version": "v0.9",
        "updateDataModel": {
            "surfaceId": "taobao_main",
            "path": "/products",
            "data": PRODUCTS
        }
    }
    socketio.emit('a2ui_message', update_msg)


product_id_counter = 100


@socketio.on('add_product')
def handle_add_product(data=None):
    global product_id_counter
    print(f"添加商品")
    
    product_id_counter += 1
    import random
    img_num = random.randint(1, 5)
    new_product = {
        "id": product_id_counter,
        "title": f"【新品上架】商品 {product_id_counter} 测试商品",
        "price": f"{99.00 + product_id_counter}",
        "originalPrice": f"{199.00 + product_id_counter}",
        "sales": 100 + product_id_counter,
        "shop": "测试店铺",
        "location": "上海",
        "images": [
            {"url": f"file:///android_asset/images/product{img_num}.png", "type": "main"}
        ],
        "thumbnails": [f"file:///android_asset/images/product{img_num}.png"],
        "isFreeShipping": True,
        "isVip": False,
        "coupon": "满50减5",
        "tags": ["新品", "测试"],
        "rating": 4.5,
        "comments": 10
    }
    
    PRODUCTS.append(new_product)
    
    update_msg = {
        "version": "v0.9",
        "updateDataModel": {
            "surfaceId": "taobao_main",
            "path": "/products",
            "data": PRODUCTS
        }
    }
    socketio.emit('a2ui_message', update_msg)


@app.route('/')
def index():
    return jsonify({
        "name": "A2UI WebSocket Server",
        "version": "1.0.0",
    })


@app.route('/health')
def health():
    return jsonify({"status": "ok"})


if __name__ == '__main__':
    print("=" * 50)
    print("A2UI WebSocket Server 启动中...")
    print("访问地址: http://localhost:5000")
    print("=" * 50)
    socketio.run(app, host='0.0.0.0', port=5000, debug=False, allow_unsafe_werkzeug=True)
