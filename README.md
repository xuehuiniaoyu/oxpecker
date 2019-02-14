# Oxpecker（牛椋鸟）

The android UI framework, based on hjson format

(android 动态框架，用hjson格式写布局: 支持注释、容错性高、宜读、扩展性强)

* [布局框架 hjson](http://hjson.org/)
* [模板框架 handlebars.java](https://github.com/jknack/handlebars.java)
* [网络框架 okHttp](https://github.com/square/okhttp)
* [图片框架 glide](https://github.com/bumptech/glide)
* [javascript框架 Rhion](https://developer.mozilla.org/zh-CN/docs/Mozilla/Projects/Rhino)

![图](timg.jpg)

#### oxpecker and rhino is a best friend

If you want to get more help（原名 android_rhinoceros） [@see android_rhinoceros](https://github.com/xuehuiniaoyu/android_rhinoceros)

## How To（集成到项目）

```
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}
```

```
dependencies {
    implementation 'com.github.xuehuiniaoyu:oxpecker:v1.6'
}
```

## code（代码）

```
public void onCreate() {
    HTemplate hTemplate = new SimpleHTemplate();
    Oxpecker oxpecker = new Oxpecker(context, hTemplate);
    View androidView = oxpecker.parse(...).getView();
    setContentView(androidView);

    // begin peck
    oxpecker.startPecking();
}

public void onDestroy() {
    // end peck
    oxpecker.finishPecking();
}
```

##### If start running failure:

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

下面是一个简单例子的布局代码：

```
{
    define: {
        views: {
            img: {
                tag: {{img-view}}
                attrs: {
                    width: fill
                    height: auto
                }
            }
        }
    }
    body: {
        {{vscroll-layout}}: {
            {{linear-layout}}: {
                background: "#cccccc"
                orien: v
                img:{ src: "{{assets}}/images/img1.png" }
                img:{ src: "{{assets}}/images/img2.png" }
                img:{ src: "{{assets}}/images/img3.png" }
                img:{ src: "{{assets}}/images/img4.png" }
                img:{ src: "{{assets}}/images/img5.png" }
                img:{ src: "{{assets}}/images/img6.jpg" }
                img:{ src: "{{assets}}/images/img7.jpg" }
                img:{ src: "{{assets}}/images/img8.jpg" }
            }
        }
    }
}
```

*demo*

![图片演示](demo1.gif)

## 布局（Layout）

### （相对布局）relative-layout

{{relative-layout}} :{

    width: 100
    
    height: 100
    
    ...

}

默认的抽象屏幕宽高比是 100:100

#### width 宽度

width: auto 自动宽

width: 100 满屏

width: 10w 占父容器的10/100

width: fill 满屏

width: 10% 父容器宽度的 10%

width: 10%w 占父容器的10%

width: 10px

width: 10dp/dip

width:10h 占父容器高度的10/100

width:10%h 占父容器高度的10%

#### height 高度

同width属性相似

#### margin 边距

margin: \[10, 10, 10, 10\]  各自占10/100

margin:\["10%", "10%", "10%", "10%"\]  各占10%

margin:\[\"10dp", "10%", 10, "10h"]  left:10dp  top:10%  right:10/100  bottom:父容器高度的10/100

#### padding

同margin属性相似

#### Relative to other components 相对于组件

toTopOf: "#id1" 在(id: id1) 组件的上方

above: "#id1" ==toTopOf

toBottomOf: "#id1" 在(id: id1) 组件的下方

below: "#id1" ==toBottomOf

toRightOf: "#id1" 在(id: id1) 组件的右方

toLeftOf: "#id1" 在(id: id1) 组件的左方

上面几种属性你都可以简写，比如

topOf: "#id1"

bottomOf: "#id1"

asTop: "#id1" 和(id: id1) 组件的top齐平

asBottom: "#id1" 和(id: id1) 组件的bottom齐平

asLeft: "#id1" 和(id: id1) 组件的left齐平

asRight: "#id1" 和(id: id1) 组件的right齐平

*相对于父容器*

asParentLeft: true

asParentRight: true

asParentBottom: true

asParentTop: true

asParent: left == asParentLeft: true

....

你还可以

asParent: left|bottom

asParent: centerH|bottom 在父容器的下边同时横向居中

asParent: centerV 在父容器纵向居中的显示

centerHorizontal : true 水平居中( 你也可以 centerH或center_h)

centerVertical: true 垂直居中 ( 你也可以 centerV 或 center_v)

centerInParent: true 基于父容器居中

#### weight 平分

widthWeightSum: 9 表示子（children）将在宽度上平分该权重

widthWeight: 3 表示占父容器（parent）宽度的3/9

heightWeightSum: 9

heightWeight: 4

clickable: true 可以点击

focusable: true 可以获取焦点

enabled: true 被激活( 你也可以 enable: true)

#### background 背景

backgroundColor: "#ffffff"

backgroundColor: "@color/colorId"

backgroundColor: "@android:color/colorId"

background: "#ffffff"

background: "@color/colorId"

background: "@drawable/drawableId"

background: "@android:color/colorId"

background: "@android:drawable/drawableId"

background: "{{assets}}/a.jpg"

background: "{{drawable}}/a"

## (线性布局) linear-layout

拥有relative-layout所有属性

特有属性

orientation: horizontal 横向

orientation: vertical 纵向

你也可以缩写：

orien: h

orien: v

## (横向滚动布局) hscroll-layout

## (纵向滚动布局) vscroll-layout

## (列表) list-view

{{list-view}}: {

    width: fill
    
    height: fill
    
    listSelector: "@drawable/bmp" // 设置焦点样式，可以是图片也可以是资源
    
    drawSelectorOnTop: true // 焦点框画在页面之上
    
    data: [
    
        {name: "张桑" sex: “男"}
    
        {name: "李四" sex: "女"}
    
    ]
    
    view: {
    
        body: {
    
            {{text-view}}: {
    
                text: "姓名：{{name}} 性别：{{sex}}"
    
            }
    
        }
    
    }

}

data: hjson数组格式，也可以是uri形式如：

data: "http://localhost:8080/demo/data.hjson"

view: 布局，也可以是uri形式如：

view: "{{assets}}/demo/view.hjson"

{{name}} 和 {{sex}} 是自动填充的值，跟data中的name和sex匹配

事件

onItemClick: "javascript: itemClick" --------------> function itemClick(parent, view, position, id)

onItemLongClick: "javascript: itemLongClick" --------------> function itemLongClick(parent, view, position, id)

onItemSelected: "javascript: itemSelected"  -------------->  function itemSelected(parent, view, position, id) 

所有view都拥有的事件

onClick: "java: click" -------------> function click(v)

onLongClick: "java: longClick" -------------> function longClick(v)

onFocus: "javascript: focus" -------------> function focus(v, hasFocus)

## (网格布局) grid-view

numColumns: 3  网格为3列 或者 你可以写 columns: 3

verticalSpacing:  “2dp” 纵向间距

horizontalSpacing: “2w” 横向间距

你也可以使用缩写：

vGap: 2

hGap: 2

## (文本框) text-view

text: "hello world" 显示的文本内容

textSize: "20sp" 字体大小

textColor: "@color/colorId" 字体颜色

textBold: "字体加粗"

selected: true 选中

textOverFlow: "start|end|marquee|middle" 内容超出宽度以后的显示效果

lines: 1 显示的行数

textAlign:  内容有：

left 居左

right 居右

top 居上

bottom 居下

center_vertical | center_v| centerV 垂直居中

center_horizontal | center_h | centerH 横向居中

textAlign属性可以同时设置多项：

textAlign: right|bottom

## (编辑框) edit-view

## (按钮) button-view

## (图片控件) img-view

{{img-view}}: {

    width: 100
    
    height: 60
    
    scaleType: fit_xy|center|fit_start|fit_center|fit_end|matrix 显示样式
    
    type: gif | bitmap | drawable 图片类型
    
    default: @drawable/placeholder 占位图片
    
    error: @drawable/error 图片加载失败后的显示图
    
    //src: "@drawable/a"
    
    //src: "{{assets}}/bmp.jpg"
    
    src: "http://localhost:8080/demo/bmp.gif" 图片地址

}

## (画布) default-view

{{default-view}}: {

    width: fill
    
    height: fill
    
    onDraw: "javascript: onDraw"

}

onDraw 指向js方法onDraw

完整代码可以是这样

{  
    head: {  
        script:{  
            src: "{{assets}}/js/default_view.js"  
        }  
    }  

    body: {  
        {{default-view}}: {  
            id: view  
            onDraw: "javascript: onDraw"  
        }  
    }  

}

js代码：

__package.require("android.graphics.Paint").as("Paint");  
__reflect.from(__package)  

var mView  

/**  

绘图 **/function onDraw(canvas) {  

 if(mView == null) {  

     mView = __context.findViewById("#view");  

 }  

 var mPaint = __reflect.on("Paint").c_().new_();  

 mPaint.setColor(__color.parseColor("#cc0000"));  

 mPaint.setStyle(__reflect.clear().on("Paint$Style").get("FILL"));  

 canvas.drawCircle(mView.getWidth() / 2, mView.getHeight() / 2, 100, mPaint);  

 //__console.toast(mPaint)  

}

在屏幕中画了一个圆

关于js中如何使用java工具类，将在后面介绍

## (自定义) define

一个布局文件的完整格式是这样的：

```
{

    head: {
        // 组件申明
        define: {
        }
        
        // 内部脚本
        script: '''
        '''
        
        // 外部脚本
        script: {
           src: ""
        }

    }

    body: {

        ...

    }

}
```

###### 1.如果你有自定义的View要在布局中使用，你必须先要在define中进行注册

比如：

```


define:{
    myLayout: "xuehuiniaoyu.github.oxpecker.view.MyLayout"
}
```

下面你可以使用

```

myLayout: {
    width: fill
    height: fill
    ...
}
```

###### 2.如果你的布局中有大量重复配置，你希望一个公共组件来帮你完成这些繁琐的配置。

```
head: {
    define {
        // 申明一个公共组件，名为layout1
        layout1: {
            tag: {{relative-layout}}
            attrs: {
                width: fill
                height: fill
                background: "#cccccc"
                ......
            }
        }
    }
}

body: {

    layout1: {

    }

    layout1: {

    }

}
```

## js能够天然访问的java对象

__context 上下文对象，也就是当前的Activity对象

__package 包管理器

__reflect 反射工具类:

__package.require("android.graphics.Paint").as("Paint"); // 给Paint类去个别名，以后用别名代替使用
 __reflect.from(__package)
var mPaint = __reflect.on("Paint").c_().new_(); // c_(...) 构造函数 new_(...) 创建对象
// 比如要创建一个带参数的对象：
var obj = __reflect.on("ObjClass").c("String", "Integer").new("hello", 1)
// 如某个属性的值
var value = _reflect.on(obj).get("name");
// 执行方法
var object = __reflect.on(obj).method1("methodName", "String").invoke("hello");
var object = __reflect.on(obj).method("methodName", clz).invoke("hello");
如果__reflect被重复使用，建议在使用之前先.clear() 比如：
__reflect.clear().on(obj)

__console 控制台

__console.toast("") 弹出显示内容

__console.log("") Log显示内容

__net 网络工具

__net.okHttp({

    method: "post",
    
    url: "http://",
    
    contentType: "application/json",
    
    body: "",
    
    success: function(msg) {
    
    
    
    },
    
    error: function(e) {
    
    
    
    }

});

__utils 工具类

__utils.ID("#id") 获取id

__utils.md5("hello world") 计算字符串的md5

__utils.md5(file) // 计算文件的md5

__color 颜色工具类

__color.getColor("@color/colorId")

__color.getColor("@android:color/colorId")

__color.parseColor("#ffffff")

## 这些你都觉得不够

当然你可以添加你自己的java对象给js使用

你要做的首先是拿到Oxpecker对象

如果是HActivity中使用那就比较简单

```
getOxpecker().addJavaScriptInterface("obj1", new MyObj());

getOxpecker().addJavaScriptInterface("obj2", new MyObj());

setContentViewFromAssets("main.hj");
```

如果是自定义使用的

```
HTemplate hTemplate = new SimpleHTemplate();
// 添加自定义组件
hTemplate.as("layout1", MyLayout.class.getName());

Oxpecker oxpecker = new Oxpecker(this, hTemplate);


// 添加自定义java交互对象
oxpecker.addJavaScriptInterface("name1", new MyObj());

oxpecker.addJavaScriptInterface("obj2", new MyObj());


View v = oxpecker.parse(template).getView();

parent.addView(v);


oxpecker.startPecking();
```

## 事件管理对象 FunctionExec

1. 配置事件

```
onClick: "javascript: clickMethod"
```

2. 事件接收器接收到配置后会执行如下代码

```
FunctionExec functionExec = new FunctionExec(jsonValue, value1, value2, ...);

functionExec.exec(getContext(), getJsChannel(), getReflect());
```

3. javascript的代码被执行

```
function clickMethod(view) {

}
```
