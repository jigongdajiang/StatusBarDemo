package gjg.com.statusbardemo.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import gjg.com.statusbardemo.R;

/**
 * @author gaojigong
 * @version V1.0
 * @Description: 透明状态栏兼容器
 * @date 16/12/22
 */
public class StatusBarCompat {
    //添加过标志
    private static final String TAG_ADDED_STATUS_BAR = "added_status_bar";
    private static final String TAG_ADDED_NAV_BAR = "added_nav_bar";
    private Builder.Params mParams;
    private ViewTreeObserver.OnGlobalLayoutListener navBarShowListener;

    private StatusBarCompat(Builder.Params params){
        this.mParams = params;
    }

    /**
     * 自定义状态栏和导航栏颜色，
     * 无需单独去为顶部View去做高度适配
     * @param activity
     * @param color
     * @param alpha 最小为 0，最大为 255,为0时表示没有透明度
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private  void setColorBar(Activity activity,@ColorInt int color, int alpha,boolean supportNavBar) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//>5.0时
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//添加可自由绘制系统Bar背景的标志
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//清除系统原来的透明半透明状态栏标志
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//清除系统原来的透明半透明导航栏标志
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);//得到最终颜色
            window.setStatusBarColor(alphaColor);//直接设置状态蓝颜色
            if(supportNavBar && navigationBarExist(activity)){
                window.setNavigationBarColor(alphaColor);//直接设置导航栏颜色
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4~5.0
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//添加半透明状态栏标志
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);//计算最终颜色
            ViewGroup decorView = (ViewGroup) window.getDecorView();

            boolean supportNav = supportNavBar && navigationBarExist(activity);
            addStatusBar(decorView,activity,alphaColor,-1,supportNav);
            if(supportNav){
                addNavBar(decorView,activity,alphaColor,-1);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            if(mParams.supportType == 0 && mParams.paddingChangedView != null){
                //当沉浸，又已经通过外部适配过顶部View则不需要设置子View属性，否则会导致顶部有两个高度。
                //这种情况只放在自动更换图标且适配的顶部的情况
                setRootView(activity, false);
            }else{
                setRootView(activity, true);
            }
        }
    }

    /**
     * 任意透明度状态栏和导航栏
     * 这里会是沉浸模式，如果需要适配顶部View的高度需要单独去改变顶部View的高度和Padding值
     * @param activity
     * @param color
     * @param alpha
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setImmerseBar(Activity activity,@ColorInt int color, int alpha,boolean supportNavBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));

            window.setStatusBarColor(finalColor);
            if(supportNavBar && navigationBarExist(activity)){
                window.setNavigationBarColor(finalColor);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            //目的是给相应的位置盖一个指定颜色的View
            boolean supportNav = supportNavBar && navigationBarExist(activity);
            addStatusBar(decorView,activity,finalColor,-1,supportNav);
            if(supportNav){
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                addNavBar(decorView,activity,finalColor,-1);
            }
        }

    }

    /**
     * 状态栏和导航栏全部隐藏
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setHintBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 针对DrawerLayout的特殊处理
     *
     * <android.support.v4.widget.DrawerLayout
     xmlns:android="http://schemas.android.com/apk/res/android"
     android:id="@+id/drawer_layout"
     android:layout_width="match_parent"
     android:layout_height="match_parent">

         <LinearLayout
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         android:fitsSystemWindows="true"
         android:orientation="vertical">
         </LinearLayout>

         <FrameLayout
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         android:background="@color/SpringGreen"
         android:layout_gravity="left"/>

     </android.support.v4.widget.DrawerLayout>

     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setColorBarForDrawer(Activity activity,@ColorInt int color, int alpha,boolean supportNavBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (supportNavBar && navigationBarExist(activity)) {
                option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);

            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            boolean supportNav = supportNavBar && navigationBarExist(activity);
            addStatusBar(decorView,activity,alphaColor,0,supportNav);
            if(supportNav){
                window.setNavigationBarColor(Color.TRANSPARENT);
                addNavBar(decorView,activity,alphaColor,1);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            boolean supportNav = supportNavBar && navigationBarExist(activity);

            addStatusBar(decorView,activity,alphaColor,0,supportNav);

            if(supportNav){
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                addNavBar(decorView,activity,alphaColor,1);
            }
        }
    }

    /**
     * 针对原来为warp_parent的控件，单纯的增加其包含顶部控件的padding
     * @param warpperTopView
     * @param oldPaddingTop
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void addPadding(View warpperTopView,int oldPaddingTop){
        int oldPaddingTopPx = 0;
        if(oldPaddingTop != 0){
            oldPaddingTopPx = ResUtil.getResDimensionPixelOffset(warpperTopView.getContext(),oldPaddingTop);
        }
        warpperTopView.setPadding(warpperTopView.getPaddingLeft(),
                oldPaddingTopPx+getStatusBarHeight(warpperTopView.getContext()),
                warpperTopView.getPaddingRight(),
                warpperTopView.getPaddingBottom());
    }
    /**
     * 针对制定了固定高度的需要改变其高度并增加padding，以保证沉浸后的标题不被状态栏遮盖
     * @param topView
     * @param oldHeight
     * @param oldPaddingTop
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void changeTopViewHeightAndPadding(View topView,int oldHeight,int oldPaddingTop){
        ViewGroup.LayoutParams params = topView.getLayoutParams();
        params.height = ResUtil.getResDimensionPixelOffset(topView.getContext(),oldHeight) + getStatusBarHeight(topView.getContext());
        topView.setLayoutParams(params);
        int oldPaddingTopPx = 0;
        if(oldPaddingTop != 0){
            oldPaddingTopPx = ResUtil.getResDimensionPixelOffset(topView.getContext(),oldPaddingTop);
        }
        topView.setPadding(topView.getPaddingLeft(),
                oldPaddingTopPx+getStatusBarHeight(topView.getContext()),
                topView.getPaddingRight(),
                topView.getPaddingBottom());
    }
    private View createStatusBarView(Context context, @ColorInt int color) {
        View mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
        params.gravity = Gravity.TOP;
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(color);
        return mStatusBarTintView;
    }
    private void addStatusBar(ViewGroup decorView,Activity activity,int alphaColor,int position,boolean supporNav){
        int decorViewChilds = decorView.getChildCount();
        View statusBarView = null;
        View tempView = null;
        if(supporNav){
            if(-1 == position){//非抽屉添加
                tempView = decorView.getChildAt(decorViewChilds - 2);
            }else{
                tempView = decorView.getChildAt(position);
            }
        }else{
            if(-1 == position) {//非抽屉添加
                tempView = decorView.getChildAt(decorViewChilds - 1);
            }else{
                tempView = decorView.getChildAt(position);
            }
        }
        if(null != tempView){
            String tag = (String) tempView.getTag();
            if(!TextUtils.isEmpty(tag) && TAG_ADDED_STATUS_BAR.equals(tag)){
                //添加过
                statusBarView = tempView;
            }
        }
        if(statusBarView == null){
            statusBarView = createStatusBarView(activity,alphaColor);
            statusBarView.setTag(TAG_ADDED_STATUS_BAR);
            decorView.addView(statusBarView,position);
        }else{
            statusBarView.setBackgroundColor(alphaColor);
        }
    }
    private void addNavBar(ViewGroup decorView,Activity activity,int alphaColor,int position){
        int decorViewChilds = decorView.getChildCount();
        View navBarView = null;
        View tempView = null;

        if(-1 == position){//非抽屉添加
            tempView = decorView.getChildAt(decorViewChilds - 1);
        }else{
            tempView = decorView.getChildAt(position);
        }

        if(null != tempView){
            String tag = (String) tempView.getTag();
            if(!TextUtils.isEmpty(tag) && TAG_ADDED_NAV_BAR.equals(tag)){
                //添加过
                navBarView = tempView;
            }
        }
        if(navBarView == null){
            navBarView = createNavBarView(activity,alphaColor);
            navBarView.setTag(TAG_ADDED_NAV_BAR);
            decorView.addView(navBarView,position);
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(createNavBarShowableListener(activity));
        }else{
            navBarView.setBackgroundColor(alphaColor);
        }
    }

    private View createNavBarView(Context context, @ColorInt int color) {
        View mNavBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getNavigationHeight(context));
        params.gravity = Gravity.BOTTOM;
        mNavBarTintView.setLayoutParams(params);
        mNavBarTintView.setBackgroundColor(color);
        return mNavBarTintView;
    }
    private ViewTreeObserver.OnGlobalLayoutListener createNavBarShowableListener(final Activity activity){
        if(navBarShowListener == null){
            navBarShowListener =  new ViewTreeObserver.OnGlobalLayoutListener(){
                @Override
                public void onGlobalLayout() {
                    Window window = activity.getWindow();
                    ViewGroup decorView = (ViewGroup) window.getDecorView();
                    View navBarView = decorView.getChildAt(1);
                    if(null != navBarView){
                        if(navigationBarShowding(activity)){
                            navBarView.setVisibility(View.VISIBLE);
                        }else{
                            navBarView.setVisibility(View.GONE);
                        }
                    }
                }
            };
        }
        return navBarShowListener;
    }

    /**
     * 通过高度差来判断是否正在显示导航栏,但是并不能作为系统是否有导航栏的依据
     * 因为当设置为不显示时，这个方法就会失效
     * @param activity
     * @return
     */
    public static boolean navigationBarShowding(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }
    public static boolean navigationBarExist(Context activity) {
        boolean hasNavigationBar = false;
        Resources rs = activity.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    /**
     * 移除监听，只有在支持navbar，且是通过添加的方式实现navbar的时候才能执行该方法。
     * 如果支持操作navbar且通过添加的方式进行了覆盖，需要在onDestrory时调用此方法
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public StatusBarCompat destoryNavBarShowListener(){
        if(mParams != null && mParams.activity != null && navBarShowListener != null && mParams.supportNavBar && navigationBarExist(mParams.activity)){
            Window window = mParams.activity.getWindow();
            View detorView = window.getDecorView();
            detorView.getViewTreeObserver().removeOnGlobalLayoutListener(navBarShowListener);
        }
        return this;

    }
    @ColorInt
    private int calculateColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }


    /**
     * 这个方法是用来设置布局所有子view的 fitsSystemWindows 参数的
     * @param activity
     * @param fit
     */
    private void setRootView(Activity activity, boolean fit) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fit);
                ((ViewGroup)childView).setClipToPadding(fit);
            }
        }
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }


    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelOffset(resourceId);
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    private boolean FlymeSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    private boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field  field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;
            }catch (Exception e){

            }
        }
        return result;
    }
    private boolean setStatusBarLightMode(Activity activity,boolean dark){
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(dark){
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            result = true;
        }
        return result;
    }

    private boolean chaneIcon(Activity activity,boolean dark){
        boolean r1 = setStatusBarLightMode(activity,dark);
        boolean r2 = MIUISetStatusBarLightMode(activity,dark);
        boolean r3 = FlymeSetStatusBarLightMode(activity,dark);
        return r1 || r2 || r3;
    }

    /**
     * 根据参数确定标题栏情况
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public StatusBarCompat apply(){
        Activity activity = mParams.activity;
        int supportType = mParams.supportType;
        int changeIconType = mParams.changeIconType;
        int oldPadding = mParams.oldPadding;
        View paddingChangedView = mParams.paddingChangedView;
        boolean supportNavBar = mParams.supportNavBar;
        int color = mParams.color;
        int alpha = mParams.alpha;
        int immerseForIconColor = mParams.immerseForIconColor;
        //参数安全性检查
        if(supportType != -1 && supportType != 0 && supportType != 1 && supportType != 2 && supportType != 3){
            //如果支持类型不合法则认为是不支持
            supportType = -1;
        }
        if(changeIconType != 0 && changeIconType != 1 && changeIconType != 2){
            //图标切换参数不合法时认为是不切换
            changeIconType = 0;
        }
        if(oldPadding >0){
            oldPadding = ResUtil.getResDimensionPixelOffset(activity,mParams.oldPadding);
        }
        if(color <= 0){
            //颜色参数不合法默认为灰色
            color = Color.TRANSPARENT;
        }else{
            //将color转换
            color = ResUtil.getResColor(activity,mParams.color);
        }
        if(alpha<0 || alpha > 255){
            //透明度不合法
            alpha = 0;
        }
        if(immerseForIconColor > 0){
            //将color转换
            immerseForIconColor = ResUtil.getResColor(activity,mParams.immerseForIconColor);
        }else{
            immerseForIconColor = 0;
        }
        if(-1 != supportType){//外部设置开启状态栏操作
            if(0 == supportType){//沉浸式
                setImmerseBar(activity,color,alpha,supportNavBar);
                //只有沉浸模式才会适配padding
                if(oldPadding >= 0 && paddingChangedView!=null){
                    //指定了
                    addPadding(paddingChangedView,oldPadding);
                }
            }else if(1 == supportType){//着色
                setColorBar(activity,color,alpha,supportNavBar);
            }else if(2 == supportType){//全隐藏
                setHintBar(activity);
            }else{
                setColorBarForDrawer(activity,color,alpha,supportNavBar);
            }
            if(0 != changeIconType){
                boolean result;
                if(1 == changeIconType){
                    result = chaneIcon(activity,true);
                }else{
                    result = chaneIcon(activity,false);
                }
                if(!result){
                    //都没有改，也就是系统不支持，则采用着色模式去给它一个指定的颜色
                    if(immerseForIconColor != 0){//如果没有设置则若果没有指定颜色则不管
                        setColorBar(activity,immerseForIconColor,alpha,supportNavBar);
                    }
                }
            }
        }
        return this;
    }

    public static class Builder{
        private Params params;
        public Builder(Activity activity){
            params = new Params(activity);
        }
        public Builder setSupportType(int supportType){
            params.supportType = supportType;
            return this;
        }
        public Builder setChangeIconType(int changeIconType){
            params.changeIconType = changeIconType;
            return this;
        }
        public Builder setOldPadding(int oldPadding){
            params.oldPadding = oldPadding;
            return this;
        }
        public Builder setPaddingChangedView(View paddingChangedView){
            params.paddingChangedView = paddingChangedView;
            return this;
        }
        public Builder setSupportNavBar(boolean supportNavBar){
            params.supportNavBar = supportNavBar;
            return this;
        }
        public Builder setColor(int color){
            params.color = color;
            return this;
        }
        public Builder setAlpha(int alpha){
            params.alpha = alpha;
            return this;
        }
        public Builder setImmerseForIconColor(int immerseForIconColor){
            params.immerseForIconColor = immerseForIconColor;
            return this;
        }

        public StatusBarCompat builder(){
            return new StatusBarCompat(params);
        }
        public static class Params{
            public Params(Activity activity){
                this.activity = activity;
            }
            public Activity activity;
            /**支持类型
             * -1 表示不支持(可以系统级别统一打开或关闭状态栏操作)
             * 0 表示只沉浸模式
             * 1 表示只着色模式
             * 2 完全沉浸全部隐藏
             * 3 专门适配抽屉式菜单的
             */
            public int supportType = -1;
            public int changeIconType = 0;//动态改变图标标志 0 表示不要改变图标，1表示需要改为深色，2表示需要改为浅色
            public int oldPadding = 0;//<0 表示完全沉浸不需要去填充padding以适配高度
            public View paddingChangedView = null;//如果需要适配padding时，需要指定应该填充statusBar高度的View
            public boolean supportNavBar = false;//true 支持  false 不支持，这只是外部控制，具体有没有效果还要取决于系统本身
            public int color = -1;//颜色 R.color.
            public int alpha = 0;//透明度 0 表示没有透明度
            public int immerseForIconColor = R.color.color_immerse_icon_background;//要求图标改为深色，但是系统不支持时将采用这个背景颜色来进行状态栏着色
        }
    }
}