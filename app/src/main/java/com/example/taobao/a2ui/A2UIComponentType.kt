package com.example.taobao.a2ui

data class A2UIComponentType(
    val type: String,
    val props: MutableMap<String, Any> = mutableMapOf()
)

object A2UIComponentTypes {
    const val TEXT = "Text"
    const val BUTTON = "Button"
    const val ROW = "Row"
    const val COLUMN = "Column"
    const val IMAGE = "Image"
    const val LIST = "List"
    const val CARD = "Card"
    const val INPUT = "Input"
    const val ICON = "Icon"
    const val DIVIDER = "Divider"
    const val SPACER = "Spacer"
    const val GRID = "Grid"
    const val STACK = "Stack"
    const val WRAP = "Wrap"
    const val SCROLL_VIEW = "ScrollView"
    const val FLAT_LIST = "FlatList"
    const val TEXT_INPUT = "TextInput"
    const val CHECK_BOX = "CheckBox"
    const val RADIO_BUTTON = "RadioButton"
    const val SWITCH = "Switch"
    const val SLIDER = "Slider"
    const val PROGRESS = "Progress"
    const val BADGE = "Badge"
    const val AVATAR = "Avatar"
    const val TAG = "Tag"
    const val TOAST = "Toast"
    const val MODAL = "Modal"
    const val BOTTOM_SHEET = "BottomSheet"
    const val DRAWER = "Drawer"
    const val TABS = "Tabs"
    const val CAROUSEL = "Carousel"
    const val PAGINATION = "Pagination"
    const val RATING = "Rating"
    const val PROGRESS_BAR = "ProgressBar"
    const val SKELETON = "Skeleton"
    const val EMPTY_STATE = "EmptyState"
    const val LOADING = "Loading"
    const val ERROR_STATE = "ErrorState"
}

object A2UIEventTypes {
    const val ON_CLICK = "onClick"
    const val ON_CHANGE = "onChange"
    const val ON_SUBMIT = "onSubmit"
    const val ON_FOCUS = "onFocus"
    const val ON_BLUR = "onBlur"
    const val ON_SCROLL = "onScroll"
    const val ON_REFRESH = "onRefresh"
    const val ON_LOAD_MORE = "onLoadMore"
    const val ON_SWIPE = "onSwipe"
    const val ON_DISMISS = "onDismiss"
}
