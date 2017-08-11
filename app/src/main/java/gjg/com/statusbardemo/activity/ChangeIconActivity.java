package gjg.com.statusbardemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.listener.TabEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gjg.com.statusbardemo.R;
import gjg.com.statusbardemo.fragment.Blue2Fragment;
import gjg.com.statusbardemo.fragment.BlueFragment;
import gjg.com.statusbardemo.fragment.White2Fragment;
import gjg.com.statusbardemo.fragment.WhiteFragment;
import gjg.com.statusbardemo.util.FragmentChangeManager;
import gjg.com.statusbardemo.util.StatusBarCompat;

/**
 * @author : gongdaocai
 * @date : 2017/8/10
 * FileName:
 * @description:
 */


public class ChangeIconActivity extends AppCompatActivity {
    @InjectView(R.id.tab)
    CommonTabLayout mTabLayout;//新发现标签
    @InjectView(R.id.container)
    FrameLayout mContainer;//Fragment

    //fragment相关
    private List<Fragment> fragments;
    private List<String> fTags;

    private FragmentChangeManager mFragmentChangeManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_icon);
        ButterKnife.inject(this);
        initTab();
        fragments = new ArrayList<>();

        fragments.add(new BlueFragment());
        fragments.add(new WhiteFragment());
        fragments.add(new Blue2Fragment());
        fragments.add(new White2Fragment());

        fTags = new ArrayList<>();
        fTags.add(BlueFragment.class.getSimpleName());
        fTags.add(WhiteFragment.class.getSimpleName());
        fTags.add(Blue2Fragment.class.getSimpleName());
        fTags.add(White2Fragment.class.getSimpleName());

        mFragmentChangeManager = new FragmentChangeManager(getSupportFragmentManager(), R.id.container, fragments, fTags);

        changeTab(0);
    }

    private void initTab() {
        String[] mTitles = new String[]{
                "首页",
                "投资",
                "发现",
                "我的"
        };
        int[] mIconUnselectIds = {
                R.mipmap.radio_home,
                R.mipmap.radio_invest,
                R.mipmap.radio_found,
                R.mipmap.radio_account,
        };
        int[] mIconSelectIds = {
                R.mipmap.radio_home_check,
                R.mipmap.radio_invest_check,
                R.mipmap.radio_found_check,
                R.mipmap.radio_account_check,
        };
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mTabLayout.setTabData(mTabEntities);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                            changeTab(0);
                        break;
                    case 1:
                            changeTab(1);
                        break;
                    case 2:
                            changeTab(2);
                        break;
                    case 3:
                        changeTab(3);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }
    private void changeTab(int position) {
        mFragmentChangeManager.changeFragment(position);
        mTabLayout.setCurrentTab(position);
        changeIcon(position);
    }

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
}
