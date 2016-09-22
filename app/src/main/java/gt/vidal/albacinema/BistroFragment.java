package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ViewPager pager = (ViewPager)view.findViewById(R.id.viewpager);
        pager.setAdapter(new BistroPagerAdapter(getChildFragmentManager()));

        ((TabLayout)view.findViewById(R.id.tabLayout)).setupWithViewPager(pager);
        pager.getAdapter().notifyDataSetChanged();
    }

    class BistroPagerAdapter extends FragmentStatePagerAdapter
    {

        public BistroPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
            {
                CinesFragment f = new CinesFragment();
                f.bistro = true;
                f.setRetainInstance(false);
                return f;
            }

            return GalleryFragment.newInstance("/Bistro");
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Ubicaciones" : "Galería";
        }
    }
}
