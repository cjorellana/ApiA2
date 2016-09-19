package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by alejandroalvarado on 14/09/16.
 */
public class BaseFragment extends android.support.v4.app.Fragment
{
    public BaseActivity getBaseActivity()
    {
        return (BaseActivity)this.getActivity();
    }

    public String getTitle()
    {
        return "";
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getBaseActivity().getSupportActionBar().setDisplayShowCustomEnabled(false);
        setRetainInstance(true);
    }
}
