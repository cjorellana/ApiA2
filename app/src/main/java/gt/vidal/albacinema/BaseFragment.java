package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

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

    View emptyView;
    View contentView;
    TextView txtEmpty;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(getParentFragment() == null);
        getBaseActivity().getSupportActionBar().setDisplayShowCustomEnabled(false);
        emptyView = view.findViewById(R.id.emptyView);
        contentView = view.findViewById(R.id.contentView);
        txtEmpty = (TextView) view.findViewById(R.id.txtEmpty);
    }

    public void showEmptyView(String emptyMessage)
    {
        showEmptyView();
        txtEmpty.setText(emptyMessage);
    }

    public void showEmptyView()
    {
        emptyView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
    }

    public void hideEmptyView()
    {
        emptyView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }
}
