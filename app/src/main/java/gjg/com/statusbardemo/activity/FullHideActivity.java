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


public class FullHideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_hide);
        new StatusBarCompat.Builder(this).setSupportType(2).builder().apply();
    }
}
