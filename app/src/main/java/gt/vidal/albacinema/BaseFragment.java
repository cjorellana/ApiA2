package gt.vidal.albacinema;

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
}
