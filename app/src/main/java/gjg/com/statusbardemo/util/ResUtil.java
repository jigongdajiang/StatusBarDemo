package gjg.com.statusbardemo.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * @author gaojigong
 * @version V1.0
 * @Description: 获取资源信息
 * @date 17/1/9
 */
public class ResUtil {
    public static String getResString(Context context,int resId){
       return context.getResources().getString(resId);
    }
    public static String[] getResStringArray(Context context,int resId){
        return context.getResources().getStringArray(resId);
    }
    public static int getResColor(Context context,int resId){
        return context.getResources().getColor(resId);
    }
    public static float getResDimen(Context context,int resId){
        //获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘   返回float
        return context.getResources().getDimension(resId);
    }
    public static int getResDimensionPixelOffset(Context context,int resId){
        //获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘  返回int
        return context.getResources().getDimensionPixelOffset(resId);
    }
    public static float getResDimensionPixelSize(Context context,int resId){
        //则不管写的是dp还是sp还是px,都会乘以denstiy.
        return context.getResources().getDimensionPixelSize(resId);
    }
    public static boolean getResBoolean(Context context,int resId){
        //则不管写的是dp还是sp还是px,都会乘以denstiy.
        return context.getResources().getBoolean(resId);
    }
    public static Drawable getResDrawable(Context context,int resId){
        return context.getResources().getDrawable(resId);
    }
    public static ColorStateList getResColorStateList(Context context, int resId){
        return context.getResources().getColorStateList(resId);
    }
}
