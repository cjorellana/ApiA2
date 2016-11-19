package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;


public class EstrenosFragment extends BaseFragment
{
    private View.OnClickListener clickListener;
    private RecyclerView recyclerPeliculas;
    View view;
    private JsonArray peliculas;

    public EstrenosFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_peliculas_card, container, false);
        recyclerPeliculas = (RecyclerView) view.findViewById(R.id.recycler);
        ((TextView)view.findViewById(R.id.txtHeader)).setText("Estrenos");

        clickListener = v -> {
            int position = recyclerPeliculas.getChildLayoutPosition(v);
            CinesFragment f = new CinesFragment();
            f.peliEstreno = peliculas.get(position).getAsJsonObject();
            getBaseActivity().changeFragment(f);
        };
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fetchPeliculas();
    }

    @Override
    public String getTitle()
    {
        return "Estrenos";
    }

    private void fetchPeliculas()
    {
        new BackgroundTask<JsonElement>(() -> Api.instance.getJson("/peliculas/estrenos"), (json, exception) ->
        {
            if (exception != null) {throw new RuntimeException(exception);}
            if (json != null) onPeliculasFetched(json);
        }).execute();
    }

    private void onPeliculasFetched(JsonElement json)
    {
        peliculas = json.getAsJsonArray();
        for (JsonElement p : peliculas)
        {
            p.getAsJsonObject().addProperty("Expanded", false);
        }
        recyclerPeliculas.setAdapter(new PeliculasRecyclerAdapter(peliculas, getBaseActivity(), clickListener));
        recyclerPeliculas.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
    }


}
