package gjg.com.statusbardemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gjg.com.statusbardemo.activity.ChangeIconActivity;
import gjg.com.statusbardemo.activity.ColourActivity;
import gjg.com.statusbardemo.activity.ColourHalfActivity;
import gjg.com.statusbardemo.activity.DrawerActivity;
import gjg.com.statusbardemo.activity.FullHideActivity;
import gjg.com.statusbardemo.activity.FullImmerseActivity;
import gjg.com.statusbardemo.activity.ImmerseHalfActivity;
import gjg.com.statusbardemo.activity.ImmerseTopActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void quanyincang(View view) {
        start(FullHideActivity.class);
    }

    public void quanchenjin(View view) {
        start(FullImmerseActivity.class);
    }

    public void chenjinTop(View view) {
        start(ImmerseTopActivity.class);
    }

    public void chenjinhalftranst(View view) {
        start(ImmerseHalfActivity.class);
    }

    public void zhuose(View view) {
        start(ColourActivity.class);
    }

    public void zhuosehalf(View view) {
        start(ColourHalfActivity.class);
    }

    public void changeIcon(View view) {
        start(ChangeIconActivity.class);
    }
    private void start(Class target){
        Intent intent = new Intent(this,target);
        startActivity(intent);
    }

    public void chouti(View view) {
        start(DrawerActivity.class);
    }
}
