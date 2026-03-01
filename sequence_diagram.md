sequenceDiagram
    participant User
    participant SimpleA2UIActivity
    participant A2UIDataSource
    participant renderUI
    participant ProductCard
    
    User->>SimpleA2UIActivity: onCreate()
    SimpleA2UIActivity->>A2UIDataSource: getMainPageMessages()
    A2UIDataSource-->>SimpleA2UIActivity: messages (banners, categories, products)
    
    SimpleA2UIActivity->>renderUI: renderUI()
    
    rect rgb(240, 248, 255)
        Note over renderUI: 构建UI组件
        renderUI->>renderUI: 创建ScrollView
        renderUI->>renderUI: 创建Header (橙色标题栏)
        renderUI->>renderUI: 创建Banner轮播
        renderUI->>renderUI: 创建Category分类图标
        renderUI->>renderUI: 创建Product Grid商品网格
    end
    
    loop 为每个商品创建卡片
        renderUI->>ProductCard: createProductCard(product, index)
        ProductCard-->>renderUI: View组件
    end
    
    renderUI-->>SimpleA2UIActivity: UI渲染完成
    SimpleA2UIActivity->>User: 显示淘宝风格首页
    
    User->>ProductCard: 点击商品卡片
    
    rect rgb(255, 240, 245)
        Note over ProductCard: 处理点击事件
        ProductCard->>ProductCard: 获取当前价格
        ProductCard->>ProductCard: price + 1.0
        ProductCard->>ProductCard: 更新products数据
        ProductCard->>ProductCard: 直接更新TextView (不重绘整个界面)
    end
    
    ProductCard-->>User: Toast: "价格已+1: ¥xx.xx"
