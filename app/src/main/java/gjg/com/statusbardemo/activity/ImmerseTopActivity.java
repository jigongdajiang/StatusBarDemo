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


public class ImmerseTopActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_immerse_top);
        View top_warpper = findViewById(R.id.top_warpper);
        new StatusBarCompat.Builder(this).setSupportType(0).setPaddingChangedView(top_warpper).builder().apply();
    }
}
