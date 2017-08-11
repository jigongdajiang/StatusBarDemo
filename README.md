# StatusBarDemo
系统状态栏和标题栏终极适配
# 简介
### 市面上适配方案的不足
1.大都针对了标题栏而没有考虑底部导航栏的问题</br>
2.在低版本中addView时没有考虑重复情况下会重复添加</br>
3.大都以静态类的形式实现，调用起来不灵活</br>
4.没有对不同背景下的沉浸式时状态栏图标的颜色更换做统一处理
### 本方案的特点
1.对标题栏和底部导航栏做了处理</br>
2.采用Builder模式进行构建，配置化操作标题栏</br>
3.提供沉浸式标题位置的解决方案</br>
4.能根据系统和配置情况自动切换标题栏的图标颜色</br>
5.支持全屏方案
6.提供抽屉菜单下显示不合理的解决方案
### 示例代码，代码注释比较全面，需要细究请down源码直接看即可
1.全隐藏
``` Java
new StatusBarCompat.Builder(this)
    .setSupportType(2)
    .builder()
    .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/1.png) </br>
2.全沉浸 
``` Java
new StatusBarCompat.Builder(this)
    .setSupportType(0)
    .builder()
    .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/2.png) </br>
3.沉浸适配顶部高度
``` Java
View top_warpper = findViewById(R.id.top_warpper);
new StatusBarCompat.Builder(this)
     .setSupportType(0)
     .setPaddingChangedView(top_warpper)
     .builder()
     .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/3.png) </br>
4.沉浸半透明且支持同步操作底部导航栏
``` Java
new StatusBarCompat.Builder(this)
                .setSupportType(0)
                .setColor(R.color.color_black)
                .setAlpha(128)
                .setSupportNavBar(true)
                .builder()
                .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/4.png) </br>
5.着色且支持同步操作底部导航栏
``` Java
new StatusBarCompat.Builder(this)
                .setSupportType(1)
                .setColor(R.color.colorAccent)
                .setSupportNavBar(true)
                .builder()
                .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/5.png) </br>
6.着色半透明
``` Java
new StatusBarCompat.Builder(this)
                .setSupportType(1)
                .setColor(R.color.colorAccent)
                .setAlpha(125)
                .builder()
                .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/6.png) </br>
7.动态改变图标颜色
``` Java
private void changeIcon(int position){
        StatusBarCompat.Builder builder = new StatusBarCompat.Builder(this)
                .setSupportType(0).setColor(R.color.SpringGreen).setAlpha(100);
        if(0 == position || 2 == position){
            builder.setChangeIconType(2);
        }else{
            builder.setImmerseForIconColor(R.color.DeepSkyBlue).setChangeIconType(1);
        }
        builder.setSupportNavBar(true).builder().apply();
    }
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/7_1.png) 
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/7_2.png) </br>
8.抽屉菜单下的标题栏
``` Java
new StatusBarCompat.Builder(this)
                .setSupportType(3)
                .setColor(R.color.DeepSkyBlue)
                .builder()
                .apply();
```
![1](https://github.com/jigongdajiang/StatusBarDemo/raw/master/app/imgs/8.png) </br>

