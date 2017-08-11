package gjg.com.statusbardemo.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * @author hujiajun
 * @version V1.0
 * @Description: Fragment管理类
 * @date 17/02/08.
 */
public class FragmentChangeManager {
    private FragmentManager mFragmentManager;
    private int mContainerViewId;
    /**
     * Fragment切换数组
     */
    private List<String> mFragmentTags;
    private List<Fragment> mFragments;
    private Fragment currentShowFragment;

    public FragmentChangeManager(FragmentManager fm, int containerViewId, List<Fragment> fragments, List<String> fragmentTags) {
        this.mFragmentManager = fm;
        this.mContainerViewId = containerViewId;
        this.mFragments = fragments;
        this.mFragmentTags = fragmentTags;
    }

    public void changeFragment(int index) {
        if (mFragments != null && index < mFragments.size()) {
            Fragment fragment = mFragments.get(index);
            String tag = mFragmentTags.get(index);
            if (fragment != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                if (!fragment.isAdded()) {
                    ft.add(mContainerViewId, fragment, tag);
                }
                if (currentShowFragment != null) {
                    ft.hide(currentShowFragment);
                }
                ft.show(fragment);
                currentShowFragment = fragment;
                ft.commit();
            }
        }
    }

    public int getSize() {
        return mFragments != null ? mFragments.size() : 0;
    }
}
