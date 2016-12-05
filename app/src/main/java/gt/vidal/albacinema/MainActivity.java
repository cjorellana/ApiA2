package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private static final String CURRENT_FRAGMENT_KEY = "actual";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Api.instance = new Api("http://cines.softwarecj.com");
        Api.instance = new Api("https://ccn.albacinema.com.gt");
        Images.init();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BaseFragment showMe = new CinesFragment();

        if (savedInstanceState != null)
        {
            BaseFragment old = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT_KEY);

            if (old != null)
            {
                showMe = old;
            }

        }

        changeFragment(showMe, false, false);

    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        BaseFragment f = null;
        if (id == R.id.nav_cartelera)
        {
            f = new CinesFragment();
        } else if (id == R.id.nav_bistro)
        {
            f = new BistroFragment();
        } else if (id == R.id.nav_estrenos)
        {
            f = new EstrenosFragment();
        } else if (id == R.id.nav_proximamente)
        {
            f = new ProximosFragment();
        } else if (id == R.id.nav_promo)
        {
            f = GalleryFragment.newInstance("/Promociones", "Promociones");
        } else if (id == R.id.nav_dulceria)
        {
            f = GalleryFragment.newInstance("/Dulceria", "Dulcería");
        } else if (id == R.id.nav_ubicaciones)
        {
            f = new UbicacionesFragment();
        }
        else if (id == R.id.nav_asientos)
        {
            f = new AsientosFragment();
        }

        changeFragment(f, false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getSupportFragmentManager() == null || currentFragment == null || outState == null || !currentFragment.isAdded())
            return;

        getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT_KEY, currentFragment);
    }
}
