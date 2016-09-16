package gt.vidal.albacinema;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alejandroalvarado on 14/09/16.
 */
public class BaseActivity extends AppCompatActivity
{
    public void changeFragment(BaseFragment f)
    {
        changeFragment(f, true);
    }

    public void changeFragment(BaseFragment f, boolean backstack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f);

        if (backstack)
            trans.addToBackStack(null);
        else
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        trans.commit ();

        this.setTitle(f.getTitle());
    }
}
