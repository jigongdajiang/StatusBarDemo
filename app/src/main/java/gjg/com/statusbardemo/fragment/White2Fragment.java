package gjg.com.statusbardemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gjg.com.statusbardemo.R;

/**
 * @author : gongdaocai
 * @date : 2017/8/10
 * FileName:
 * @description:
 */


public class White2Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_white,container,false);
    }
}
