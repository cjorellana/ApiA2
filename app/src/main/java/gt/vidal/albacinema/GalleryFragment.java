package gt.vidal.albacinema;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class GalleryFragment extends BaseFragment
{
    public String path;
    public String title;
    private View view;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private FullscreenImageAdapter adapter;
    private ViewPager viewPager;

    public static GalleryFragment newInstance(String path, String title)
    {
        GalleryFragment f = new GalleryFragment();
        f.path = path;
        f.title = title;
        return f;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (images.size() > 0)
        {
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return;
        }
        new BackgroundTask<JsonArray>(() -> Api.instance.getJson(path).getAsJsonArray(), (arr, ex) ->
        {
            if (ex != null) throw new RuntimeException(ex);
            if (arr!= null) onPathsFetched(arr);
        }).execute();
    }

    private void onPathsFetched(JsonArray arr)
    {
        adapter = new FullscreenImageAdapter(getBaseActivity(), images);
        viewPager.setAdapter(adapter);

        for (JsonElement imgPath : arr)
        {
            String path = imgPath.getAsString();
            new BackgroundTask<Bitmap>(() -> Api.instance.getImage(path, false), (bmp, ex) ->
            {
                if (ex != null) throw new RuntimeException(ex);
                if (bmp != null)
                {
                    images.add(bmp);
                    adapter.notifyDataSetChanged();
                }

            }).execute();
        }
    }
}
