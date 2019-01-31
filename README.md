# Oxpecker（牛椋鸟）
The android UI framework, based on hjson format(android UI框架，用hjson格式写布局: 支持注释、容错性高、宜读、扩展性强)
![](timg.jpg)

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
	implementation 'com.github.xuehuiniaoyu:oxpecker:v1.0'
}
```
## code（代码）
```
public void onCreate() {
	HTemplate hTemplate = new SimpleHTemplate();
    Oxpecker oxpecker = new Oxpecker(context, hTemplate);
    View androidView = oxpecker.parse(...).getView();

    // begin peck
    oxpecker.startPecking();
}

public void onDestroy() {
	// end peck
	oxpecker.finishPecking();
}
```
