package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alejandroalvarado on 14/09/16.
 */
public class BaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener
{
    protected BaseFragment currentFragment;

    public void changeFragment(BaseFragment f)
    {
        changeFragment(f, true, false);
    }

    public void changeFragment(BaseFragment f, boolean addToBackstack)
    {
        changeFragment(f, addToBackstack, !addToBackstack);
    }

    public void changeFragment(BaseFragment f, boolean addToBackstack, boolean clearBackstack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f);

        if (addToBackstack)
            trans.addToBackStack(null);

        if (clearBackstack)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        trans.commit ();

        this.setTitle(f.getTitle());
        currentFragment = f;
    }

    @Override
    public void onBackStackChanged()
    {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof BaseFragment)
        {
            setTitle(((BaseFragment) f).getTitle());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        super.getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

}
