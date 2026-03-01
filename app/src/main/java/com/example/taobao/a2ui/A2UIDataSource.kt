package com.example.taobao.a2ui

class A2UIDataSource {

    private val productTitles = listOf(
        "【好评如潮】纯棉T恤女短袖 宽松百搭 学生宿舍必备",
        "【限时特惠】无线蓝牙耳机 降噪运动耳机 苹果安卓通用",
        "【爆款】北欧风简约客厅吊灯 创意设计师款",
        "【新品上市】男士休闲西裤 商务直筒 免烫抗皱",
        "【旗舰店】SK-II神仙水精华液 护肤套装 美白保湿",
        "【618预售】iPhone15手机壳 透明防摔 简约大气",
        "【出口品质】乳胶枕头 护颈枕 泰国进口 助眠舒适",
        "【热销10万】纯棉四件套 床上用品 简约北欧风",
        "【断码清仓】运动鞋男跑步鞋 透气减震 休闲百搭",
        "【品牌特卖】戴森吹风机 家用静音 大功率护发",
        "【优选】坚果零食大礼包 混合坚果 休闲小吃",
        "【品质保障】真皮皮带男士 商务自动扣 送礼首选",
        "【网红同款】化妆镜带灯 智能补光 桌面收纳",
        "【实用推荐】小米智能手环 运动监测 心率睡眠",
        "【家居必备】北欧简约茶几 实木小户型客厅",
        "【女神必备】口红套装 完美日记 礼盒送女友",
        "【学生党】笔记本电脑支架 散热增高架 铝合金",
        "【萌宠专区】猫爬架 猫抓板 猫窝一体 太空舱",
        "【父亲节礼物】茶叶礼盒 安溪铁观音 送礼佳品",
        "【夏季清仓】凉席竹席 麻将席 宿舍单人 双人大床"
    )

    private val productImages = listOf(
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
        "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9",
        "#F8B500", "#00CED1", "#FF69B4", "#32CD32", "#FF4500",
        "#8A2BE2", "#00FA9A", "#FF6347", "#40E0D0", "#EE82EE"
    )

    private val shopNames = listOf(
        "优品汇旗舰店", "潮牌服饰专营", "数码精品屋", "家居生活馆",
        "美妆护肤坊", "运动户外店", "食品超市", "图书音像店"
    )

    private val bannerData = listOf(
        mapOf("id" to 1, "title" to "618年中大促", "subtitle" to "全场5折起", "color" to "#FF6600"),
        mapOf("id" to 2, "title" to "超级红包", "subtitle" to "最高888元", "color" to "#FF2D55"),
        mapOf("id" to 3, "title" to "新品首发", "subtitle" to "限时优惠", "color" to "#007AFF"),
        mapOf("id" to 4, "title" to "品牌特卖", "subtitle" to "正品保障", "color" to "#34C759")
    )

    private val categoryData = listOf(
        mapOf("id" to 1, "name" to "天猫", "icon" to "🛍️"),
        mapOf("id" to 2, "name" to "淘宝", "icon" to "🛒"),
        mapOf("id" to 3, "name" to "聚划算", "icon" to "💰"),
        mapOf("id" to 4, "name" to "天猫国际", "icon" to "🌍"),
        mapOf("id" to 5, "name" to "淘宝直播", "icon" to "📺"),
        mapOf("id" to 6, "name" to "菜鸟驿站", "icon" to "📦"),
        mapOf("id" to 7, "name" to "支付宝", "icon" to "💳"),
        mapOf("id" to 8, "name" to "饿了么", "icon" to "🍜")
    )

    private val hotSearches = listOf(
        "连衣裙", "T恤", "运动鞋", "蓝牙耳机", "吹风机",
        "面膜", "洗衣液", "抽纸", "手机壳", "数据线"
    )

    fun getMainPageMessages(): List<A2UIMessage> {
        return listOf(
            A2UIMessage(
                createSurface = CreateSurfaceMessage(
                    surfaceId = "taobao_main",
                    catalogId = "https://a2ui.org/specification/v0_9/standard_catalog.json"
                )
            ),
            A2UIMessage(
                updateDataModel = UpdateDataModelMessage(
                    surfaceId = "taobao_main",
                    path = "/",
                    data = mapOf(
                        "banners" to bannerData,
                        "categories" to categoryData,
                        "hotSearches" to hotSearches,
                        "products" to (0 until 20).map { i ->
                            val price = (29.9..599.9).random()
                            val isContent = (i % 3 != 0)
                            val itemType = if (isContent) 3 else (0..2).random()
                            val baseMap = mutableMapOf(
                                "id" to i + 1,
                                "itemType" to itemType,
                                "hasImage" to !isContent,
                                "title" to productTitles[i % productTitles.size],
                                "price" to String.format("%.2f", price),
                                "originalPrice" to String.format("%.2f", price * 1.5),
                                "sales" to (100..50000).random(),
                                "shop" to shopNames[i % shopNames.size],
                                "location" to listOf("杭州", "上海", "广州", "深圳", "北京", "成都")[i % 6],
                                "imageColor" to productImages[i % productImages.size],
                                "isFreeShipping" to (i % 3 == 0),
                                "isVip" to (i % 5 == 0),
                                "coupon" to if (!isContent && i % 4 == 0) "满100减10" else null,
                                "tag" to listOf("热卖", "新品", "特惠", "爆款", "推荐")[i % 5]
                            )
                            if (isContent) {
                                baseMap["content"] = "这是一段非常详细的商品描述内容，包含了产品的所有信息和使用说明。用户可以在这里了解到商品的详细信息，包括规格、材质，功能特点以及使用方法等。这段文字需要足够长来展示纯文本内容样式的效果，总共约200字左右。商品质量保证正品，支持7天无理由退换货，全国联保，品质值得信赖。购买后可享受专属客服服务，有任何问题随时联系咨询。"
                            }
                            baseMap
                        },
                        "recommendTitle" to "为你推荐",
                        "guessTitle" to "猜你喜欢"
                    )
                )
            ),
            A2UIMessage(
                updateComponents = UpdateComponentsMessage(
                    surfaceId = "taobao_main",
                    components = listOf(
                        Component(
                            id = "root",
                            component = mapOf(
                                "Column" to mapOf(
                                    "spacing" to 0,
                                    "backgroundColor" to "#F5F5F5"
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf(
                                    "header", "search_bar", "banner", "hot_search", 
                                    "categories", "recommend_title", "product_grid"
                                )
                            )
                        ),
                        Component(
                            id = "header",
                            component = mapOf(
                                "Row" to mapOf(
                                    "backgroundColor" to "#FF6600",
                                    "padding" to 8,
                                    "align" to "center"
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("location_text", "header_title", "vip_badge")
                            )
                        ),
                        Component(
                            id = "location_text",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "杭州",
                                    "color" to "#FFFFFF",
                                    "fontSize" to 12
                                )
                            )
                        ),
                        Component(
                            id = "header_title",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "淘宝",
                                    "color" to "#FFFFFF",
                                    "fontSize" to 18,
                                    "fontWeight" to true
                                )
                            )
                        ),
                        Component(
                            id = "vip_badge",
                            component = mapOf(
                                "Badge" to mapOf(
                                    "text" to "88VIP",
                                    "backgroundColor" to "#FFD700"
                                )
                            )
                        ),
                        Component(
                            id = "search_bar",
                            component = mapOf(
                                "Row" to mapOf(
                                    "backgroundColor" to "#FF6600",
                                    "padding" to 12,
                                    "align" to "center"
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("scan_icon", "search_container", "message_icon")
                            )
                        ),
                        Component(
                            id = "scan_icon",
                            component = mapOf(
                                "Image" to mapOf(
                                    "src" to "#FFFFFF",
                                    "width" to 24,
                                    "height" to 24
                                )
                            ),
                            events = mapOf("onClick" to "navigate_scan")
                        ),
                        Component(
                            id = "search_container",
                            component = mapOf(
                                "Row" to mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "flexWeight" to 1,
                                    "marginStart" to 8,
                                    "marginEnd" to 8,
                                    "padding" to 8,
                                    "align" to "center",
                                    "cornerRadius" to 20
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("search_icon", "search_hint")
                            ),
                            events = mapOf("onClick" to "navigate_search")
                        ),
                        Component(
                            id = "search_icon",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "🔍",
                                    "fontSize" to 14
                                )
                            )
                        ),
                        Component(
                            id = "search_hint",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "搜索商品、品牌",
                                    "color" to "#999999",
                                    "fontSize" to 14
                                )
                            )
                        ),
                        Component(
                            id = "message_icon",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "📩",
                                    "fontSize" to 20
                                )
                            ),
                            events = mapOf("onClick" to "show_messages")
                        ),
                        Component(
                            id = "banner",
                            component = mapOf(
                                "Column" to mapOf("height" to 160)
                            ),
                            children = ChildrenReference(
                                dataBinding = "/banners",
                                templateComponentId = "banner_item"
                            )
                        ),
                        Component(
                            id = "banner_item",
                            component = mapOf(
                                "Card" to mapOf(
                                    "backgroundColor" to mapOf("binding" to "color"),
                                    "height" to 160,
                                    "cornerRadius" to 0
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("banner_content")
                            )
                        ),
                        Component(
                            id = "banner_content",
                            component = mapOf(
                                "Column" to mapOf(
                                    "align" to "center",
                                    "spacing" to 8
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("banner_title", "banner_subtitle")
                            )
                        ),
                        Component(
                            id = "banner_title",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "title"),
                                    "color" to "#FFFFFF",
                                    "fontSize" to 24,
                                    "fontWeight" to true,
                                    "textAlign" to "center"
                                )
                            )
                        ),
                        Component(
                            id = "banner_subtitle",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "subtitle"),
                                    "color" to "#FFFFFF",
                                    "fontSize" to 14,
                                    "textAlign" to "center"
                                )
                            )
                        ),
                        Component(
                            id = "hot_search",
                            component = mapOf(
                                "Row" to mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "padding" to 12,
                                    "spacing" to 8,
                                    "align" to "center"
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("hot_label", "hot_tags")
                            )
                        ),
                        Component(
                            id = "hot_label",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "🔥 热搜",
                                    "color" to "#FF6600",
                                    "fontSize" to 12,
                                    "fontWeight" to true
                                )
                            )
                        ),
                        Component(
                            id = "hot_tags",
                            component = mapOf(
                                "ScrollView" to mapOf("showsHorizontalScrollIndicator" to false)
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("hot_tags_row")
                            )
                        ),
                        Component(
                            id = "hot_tags_row",
                            component = mapOf(
                                "Row" to mapOf(
                                    "spacing" to 8
                                )
                            ),
                            children = ChildrenReference(
                                dataBinding = "/hotSearches",
                                templateComponentId = "hot_tag"
                            )
                        ),
                        Component(
                            id = "hot_tag",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to ""),
                                    "color" to "#666666",
                                    "fontSize" to 12,
                                    "backgroundColor" to "#F5F5F5",
                                    "padding" to 4
                                )
                            )
                        ),
                        Component(
                            id = "categories",
                            component = mapOf(
                                "Column" to mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "padding" to 8
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("category_grid")
                            )
                        ),
                        Component(
                            id = "category_grid",
                            component = mapOf(
                                "Grid" to mapOf(
                                    "columns" to 4,
                                    "spacing" to 8
                                )
                            ),
                            children = ChildrenReference(
                                dataBinding = "/categories",
                                templateComponentId = "category_item"
                            )
                        ),
                        Component(
                            id = "category_item",
                            component = mapOf(
                                "Column" to mapOf(
                                    "align" to "center",
                                    "spacing" to 4,
                                    "padding" to 8
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("category_icon", "category_name")
                            ),
                            events = mapOf("onClick" to "navigate_category")
                        ),
                        Component(
                            id = "category_icon",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "icon"),
                                    "fontSize" to 24
                                )
                            )
                        ),
                        Component(
                            id = "category_name",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "name"),
                                    "fontSize" to 12,
                                    "color" to "#333333"
                                )
                            )
                        ),
                        Component(
                            id = "recommend_title",
                            component = mapOf(
                                "Row" to mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "padding" to 16,
                                    "marginTop" to 8,
                                    "align" to "center"
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("recommend_icon", "recommend_text")
                            )
                        ),
                        Component(
                            id = "recommend_icon",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to "❤️",
                                    "fontSize" to 16
                                )
                            )
                        ),
                        Component(
                            id = "recommend_text",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "recommendTitle"),
                                    "color" to "#333333",
                                    "fontSize" to 16,
                                    "fontWeight" to true
                                )
                            )
                        ),
                        Component(
                            id = "product_grid",
                            component = mapOf(
                                "Grid" to mapOf(
                                    "columns" to 2,
                                    "spacing" to 8,
                                    "padding" to 8
                                )
                            ),
                            children = ChildrenReference(
                                dataBinding = "/products",
                                templateComponentId = "product_item"
                            )
                        ),
                        Component(
                            id = "product_item",
                            component = mapOf(
                                "Card" to mapOf(
                                    "backgroundColor" to "#FFFFFF",
                                    "cornerRadius" to 8,
                                    "spacing" to 0
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("product_image", "product_content")
                            ),
                            events = mapOf("onClick" to "navigate_product")
                        ),
                        Component(
                            id = "product_image",
                            component = mapOf(
                                "Image" to mapOf(
                                    "src" to mapOf("binding" to "imageColor"),
                                    "width" to "match",
                                    "height" to 160,
                                    "objectFit" to "cover"
                                )
                            )
                        ),
                        Component(
                            id = "product_content",
                            component = mapOf(
                                "Column" to mapOf(
                                    "padding" to 8,
                                    "spacing" to 6
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("product_title", "product_coupon", "product_price_row", "product_meta")
                            )
                        ),
                        Component(
                            id = "product_title",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "title"),
                                    "fontSize" to 13,
                                    "color" to "#333333",
                                    "maxLines" to 2,
                                    "ellipsize" to true,
                                    "height" to 34
                                )
                            )
                        ),
                        Component(
                            id = "product_coupon",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "coupon"),
                                    "fontSize" to 10,
                                    "color" to "#FF6600",
                                    "backgroundColor" to "#FFF5E6",
                                    "padding" to 2,
                                    "cornerRadius" to 4
                                )
                            )
                        ),
                        Component(
                            id = "product_price_row",
                            component = mapOf(
                                "Row" to mapOf(
                                    "align" to "center",
                                    "spacing" to 4
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("product_price", "product_original_price")
                            )
                        ),
                        Component(
                            id = "product_price",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf(
                                        "literalString" to "¥",
                                        "binding" to "price"
                                    ),
                                    "fontSize" to 16,
                                    "fontWeight" to true,
                                    "color" to "#FF6600"
                                )
                            )
                        ),
                        Component(
                            id = "product_original_price",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf(
                                        "literalString" to "¥",
                                        "binding" to "originalPrice"
                                    ),
                                    "fontSize" to 11,
                                    "color" to "#999999"
                                )
                            )
                        ),
                        Component(
                            id = "product_meta",
                            component = mapOf(
                                "Row" to mapOf(
                                    "align" to "center",
                                    "spacing" to 4
                                )
                            ),
                            children = ChildrenReference(
                                explicitList = listOf("product_sales", "product_location")
                            )
                        ),
                        Component(
                            id = "product_sales",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf(
                                        "binding" to "sales"
                                    ),
                                    "fontSize" to 10,
                                    "color" to "#999999"
                                )
                            )
                        ),
                        Component(
                            id = "product_location",
                            component = mapOf(
                                "Text" to mapOf(
                                    "text" to mapOf("binding" to "location"),
                                    "fontSize" to 10,
                                    "color" to "#999999"
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    fun getProductPageMessages(productId: Int): List<A2UIMessage> {
        val product = getProductDetail(productId)
        
        return listOf(
            A2UIMessage(
                createSurface = CreateSurfaceMessage(
                    surfaceId = "product_detail_$productId",
                    catalogId = "https://a2ui.org/specification/v0_9/standard_catalog.json"
                )
            ),
            A2UIMessage(
                updateDataModel = UpdateDataModelMessage(
                    surfaceId = "product_detail_$productId",
                    path = "/",
                    data = product
                )
            ),
            A2UIMessage(
                updateComponents = UpdateComponentsMessage(
                    surfaceId = "product_detail_$productId",
                    components = createProductDetailComponents(productId, product)
                )
            )
        )
    }

    private fun getProductDetail(productId: Int): Map<String, Any> {
        val index = (productId - 1) % productTitles.size
        val price = (29.9..599.9).random()
        
        return mapOf(
            "product" to mapOf(
                "id" to productId,
                "title" to productTitles[index % productTitles.size],
                "price" to String.format("%.2f", price),
                "originalPrice" to String.format("%.2f", price * 1.5),
                "discount" to "${((price / (price * 1.5)) * 10).toInt()}折",
                "sales" to (1000..50000).random(),
                "stock" to (50..500).random(),
                "shop" to shopNames[index % shopNames.size],
                "shopScore" to "4.9",
                "location" to listOf("杭州", "上海", "广州", "深圳", "北京")[index % 5],
                "description" to "【品质保证】本店产品均为正品，质量有保障！\n【关于发货】下单后48小时内发货，全国包邮。\n【关于售后】7天无理由退换货，15天质量问题包换。\n【温馨提醒】由于拍摄光线和显示器差异，实物颜色可能略有不同，请以实物为准。",
                "imageColor" to productImages[index % productImages.size],
                "isFreeShipping" to true,
                "isVip" to (productId % 5 == 0),
                "isSelfRun" to (productId % 3 == 0),
                "couponAmount" to if (productId % 4 == 0) 10.0 else 0.0,
                "points" to (price * 10).toInt()
            ),
            "sku" to listOf(
                mapOf("name" to "颜色", "options" to listOf("黑色", "白色", "蓝色", "灰色")),
                mapOf("name" to "尺码", "options" to listOf("S", "M", "L", "XL", "XXL"))
            ),
            "service" to listOf(
                mapOf("icon" to "✅", "text" to "7天无理由"),
                mapOf("icon" to "🚚", "text" to "免运费"),
                mapOf("icon" to "🛡️", "text" to "正品保证"),
                mapOf("icon" to "⏰", "text" to "72小时发货")
            ),
            "reviews" to listOf(
                mapOf("user" to "t***1", "content" to "质量很好，物流很快，包装完整！", "score" to 5, "date" to "2024-01-15"),
                mapOf("user" to "l***8", "content" to "性价比很高，会回购的！", "score" to 5, "date" to "2024-01-14"),
                mapOf("user" to "w***3", "content" to "和描述一致，很满意", "score" to 4, "date" to "2024-01-13")
            )
        )
    }

    private fun createProductDetailComponents(productId: Int, product: Map<String, Any>): List<Component> {
        return listOf(
            Component(
                id = "root",
                component = mapOf(
                    "ScrollView" to mapOf(
                        "backgroundColor" to "#F5F5F5"
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("product_image", "price_card", "coupon_card", "sku_card", "service_card", "shop_card", "review_card", "bottom_spacer")
                )
            ),
            Component(
                id = "product_image",
                component = mapOf(
                    "Image" to mapOf(
                        "src" to mapOf("binding" to "product.imageColor"),
                        "width" to "match",
                        "height" to 375,
                        "objectFit" to "cover"
                    )
                )
            ),
            Component(
                id = "price_card",
                component = mapOf(
                    "Column" to mapOf(
                        "backgroundColor" to "#FFFFFF",
                        "padding" to 12,
                        "spacing" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("price_row", "discount_label", "sales_row")
                )
            ),
            Component(
                id = "price_row",
                component = mapOf(
                    "Row" to mapOf(
                        "align" to "center",
                        "spacing" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("current_price", "original_price", "discount_badge")
                )
            ),
            Component(
                id = "current_price",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf(
                            "literalString" to "¥",
                            "binding" to "product.price"
                        ),
                        "fontSize" to 28,
                        "fontWeight" to true,
                        "color" to "#FF6600"
                    )
                )
            ),
            Component(
                id = "original_price",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf(
                            "literalString" to "¥",
                            "binding" to "product.originalPrice"
                        ),
                        "fontSize" to 14,
                        "color" to "#999999"
                    )
                )
            ),
            Component(
                id = "discount_badge",
                component = mapOf(
                    "Badge" to mapOf(
                        "text" to mapOf("binding" to "product.discount"),
                        "backgroundColor" to "#FF6600",
                        "color" to "#FFFFFF"
                    )
                )
            ),
            Component(
                id = "discount_label",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "促销：满100减10元",
                        "fontSize" to 12,
                        "color" to "#FF6600"
                    )
                )
            ),
            Component(
                id = "sales_row",
                component = mapOf(
                    "Row" to mapOf(
                        "align" to "center",
                        "spacing" to 16
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("sales_count", "stock_count", "points_text")
                )
            ),
            Component(
                id = "sales_count",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf(
                            "binding" to "product.sales"
                        ),
                        "fontSize" to 12,
                        "color" to "#999999"
                    )
                )
            ),
            Component(
                id = "stock_count",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf(
                            "binding" to "product.stock"
                        ),
                        "fontSize" to 12,
                        "color" to "#999999"
                    )
                )
            ),
            Component(
                id = "points_text",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf(
                            "binding" to "product.points"
                        ),
                        "fontSize" to 12,
                        "color" to "#FF6600"
                    )
                )
            ),
            Component(
                id = "coupon_card",
                component = mapOf(
                    "Row" to mapOf(
                        "backgroundColor" to "#FFFFFF",
                        "padding" to 12,
                        "align" to "center",
                        "marginTop" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("coupon_label", "coupon_value", "coupon_action")
                )
            ),
            Component(
                id = "coupon_label",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "券",
                        "fontSize" to 12,
                        "color" to "#FF6600",
                        "backgroundColor" to "#FF6600",
                        "color" to "#FFFFFF"
                    )
                )
            ),
            Component(
                id = "coupon_value",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "满99减${product["couponAmount"]?.let { it as Double }?.toInt() ?: 0}",
                        "fontSize" to 14,
                        "color" to "#FF6600"
                    )
                )
            ),
            Component(
                id = "coupon_action",
                component = mapOf(
                    "Button" to mapOf(
                        "text" to "领取",
                        "fontSize" to 12,
                        "backgroundColor" to "#FF6600",
                        "color" to "#FFFFFF"
                    )
                )
            ),
            Component(
                id = "sku_card",
                component = mapOf(
                    "Column" to mapOf(
                        "backgroundColor" to "#FFFFFF",
                        "padding" to 12,
                        "spacing" to 8,
                        "marginTop" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("sku_label", "sku_options")
                )
            ),
            Component(
                id = "sku_label",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "选择 颜色 尺码",
                        "fontSize" to 14,
                        "color" to "#333333"
                    )
                )
            ),
            Component(
                id = "sku_options",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "⬇️",
                        "fontSize" to 16
                    )
                )
            ),
            Component(
                id = "service_card",
                component = mapOf(
                    "Column" to mapOf(
                        "backgroundColor" to "#FFFFFF",
                        "padding" to 12,
                        "spacing" to 8,
                        "marginTop" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("service_label", "service_list")
                )
            ),
            Component(
                id = "service_label",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "服务",
                        "fontSize" to 14,
                        "color" to "#333333",
                        "fontWeight" to true
                    )
                )
            ),
            Component(
                id = "service_list",
                component = mapOf(
                    "Row" to mapOf(
                        "spacing" to 16
                    )
                ),
                children = ChildrenReference(
                    dataBinding = "/service",
                    templateComponentId = "service_item"
                )
            ),
            Component(
                id = "service_item",
                component = mapOf(
                    "Row" to mapOf(
                        "spacing" to 4
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("service_icon", "service_text")
                )
            ),
            Component(
                id = "service_icon",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf("binding" to "icon"),
                        "fontSize" to 12
                    )
                )
            ),
            Component(
                id = "service_text",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf("binding" to "text"),
                        "fontSize" to 12,
                        "color" to "#666666"
                    )
                )
            ),
            Component(
                id = "shop_card",
                component = mapOf(
                    "Column" to mapOf(
                        "backgroundColor" to "#FFFFFF",
                        "padding" to 12,
                        "spacing" to 8,
                        "marginTop" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("shop_info")
                )
            ),
            Component(
                id = "shop_info",
                component = mapOf(
                    "Row" to mapOf(
                        "align" to "center",
                        "spacing" to 12
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("shop_icon", "shop_detail")
                )
            ),
            Component(
                id = "shop_icon",
                component = mapOf(
                    "Image" to mapOf(
                        "src" to "#FF6600",
                        "width" to 40,
                        "height" to 40,
                        "cornerRadius" to 20
                    )
                )
            ),
            Component(
                id = "shop_detail",
                component = mapOf(
                    "Column" to mapOf(
                        "spacing" to 4
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("shop_name", "shop_score")
                )
            ),
            Component(
                id = "shop_name",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf("binding" to "product.shop"),
                        "fontSize" to 14,
                        "color" to "#333333"
                    )
                )
            ),
            Component(
                id = "shop_score",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf("binding" to "product.shopScore"),
                        "fontSize" to 12,
                        "color" to "#FF6600"
                    )
                )
            ),
            Component(
                id = "review_card",
                component = mapOf(
                    "Column" to mapOf(
                        "backgroundColor" to "#FFFFFF",
                        "padding" to 12,
                        "spacing" to 8,
                        "marginTop" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("review_header", "review_list")
                )
            ),
            Component(
                id = "review_header",
                component = mapOf(
                    "Row" to mapOf(
                        "align" to "center"
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("review_label", "review_more")
                )
            ),
            Component(
                id = "review_label",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "评价 (1000+)",
                        "fontSize" to 14,
                        "color" to "#333333",
                        "fontWeight" to true
                    )
                )
            ),
            Component(
                id = "review_more",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to "查看全部 >",
                        "fontSize" to 12,
                        "color" to "#999999"
                    )
                )
            ),
            Component(
                id = "review_list",
                component = mapOf(
                    "Column" to mapOf(
                        "spacing" to 12
                    )
                ),
                children = ChildrenReference(
                    dataBinding = "/reviews",
                    templateComponentId = "review_item"
                )
            ),
            Component(
                id = "review_item",
                component = mapOf(
                    "Column" to mapOf(
                        "spacing" to 8
                    )
                ),
                children = ChildrenReference(
                    explicitList = listOf("review_user", "review_content")
                )
            ),
            Component(
                id = "review_user",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf("binding" to "user"),
                        "fontSize" to 12,
                        "color" to "#999999"
                    )
                )
            ),
            Component(
                id = "review_content",
                component = mapOf(
                    "Text" to mapOf(
                        "text" to mapOf("binding" to "content"),
                        "fontSize" to 14,
                        "color" to "#333333"
                    )
                )
            ),
            Component(
                id = "bottom_spacer",
                component = mapOf(
                    "Spacer" to mapOf(
                        "height" to 80
                    )
                )
            )
        )
    }

    fun createStreamMessage(
        surfaceId: String,
        catalogId: String = "https://a2ui.org/specification/v0_9/standard_catalog.json"
    ): A2UIMessage {
        return A2UIMessage(
            createSurface = CreateSurfaceMessage(
                surfaceId = surfaceId,
                catalogId = catalogId
            )
        )
    }

    fun createUpdateComponentsMessage(
        surfaceId: String,
        components: List<Component>
    ): A2UIMessage {
        return A2UIMessage(
            updateComponents = UpdateComponentsMessage(
                surfaceId = surfaceId,
                components = components
            )
        )
    }

    fun createDataModelUpdateMessage(
        surfaceId: String,
        path: String,
        data: Any
    ): A2UIMessage {
        return A2UIMessage(
            updateDataModel = UpdateDataModelMessage(
                surfaceId = surfaceId,
                path = path,
                data = data
            )
        )
    }

    fun createDeleteSurfaceMessage(surfaceId: String): A2UIMessage {
        return A2UIMessage(
            deleteSurface = DeleteSurfaceMessage(surfaceId = surfaceId)
        )
    }

    private fun ClosedRange<Double>.random(): Double {
        return start + Math.random() * (endInclusive - start)
    }
}
