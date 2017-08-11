package gjg.com.statusbardemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gjg.com.statusbardemo.R;
import gjg.com.statusbardemo.util.StatusBarCompat;

/**
 * @author : gongdaocai
 * @date : 2017/8/10
 * FileName:
 * @description:
 */


public class ColourActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour);
        new StatusBarCompat.Builder(this)
                .setSupportType(1)
                .setColor(R.color.colorAccent)
                .setSupportNavBar(true)
                .builder()
                .apply();
    }

    public void rechangeColor(View view) {
        new StatusBarCompat.Builder(this)
                .setSupportType(1)
                .setColor(R.color.SpringGreen)
                .setSupportNavBar(true)
                .builder()
                .apply();
    }
}
