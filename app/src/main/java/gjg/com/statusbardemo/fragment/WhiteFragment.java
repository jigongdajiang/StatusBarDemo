package gjg.com.statusbardemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gjg.com.statusbardemo.R;
import gjg.com.statusbardemo.util.StatusBarCompat;

/**
 * @author : gongdaocai
 * @date : 2017/8/10
 * FileName:
 * @description:
 */


public class WhiteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_white,container,false);
        View top_warper = view.findViewById(R.id.top_warper);
//       new StatusBarCompat.Builder(getActivity())
//                .setSupportType(0).setChangeIconType(1).setImmerseForIconColor(R.color.DeepSkyBlue).setPaddingChangedView(top_warper).builder().apply();
        StatusBarCompat.addPadding(top_warper,0);
        return view;
    }
}
