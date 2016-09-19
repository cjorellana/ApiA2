package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by alejandro on 18/09/16.
 */
public class BistroFragment extends BaseFragment
{
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_bistro, container, false);
        ViewPager pager = (ViewPager)view.findViewById(R.id.viewpager);
        pager.setAdapter(new FragmentPagerAdapter(getBaseActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                {
                    CinesFragment f = new CinesFragment();
                    f.bistro = true;
                    return f;
                }

                return new GalleryFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Ubicaciones" : "Galería";
            }
        });

        ((TabLayout)view.findViewById(R.id.tabLayout)).setupWithViewPager(pager);
        return view;
    }
}
