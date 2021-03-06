package gjg.com.statusbardemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import gjg.com.statusbardemo.R;
import gjg.com.statusbardemo.util.StatusBarCompat;

/**
 * @author : gongdaocai
 * @date : 2017/8/10
 * FileName:
 * @description:
 */


public class ColourHalfActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour_half);
        new StatusBarCompat.Builder(this)
                .setSupportType(1)
                .setColor(R.color.colorAccent)
                .setAlpha(125)
                .builder()
                .apply();
    }
}
