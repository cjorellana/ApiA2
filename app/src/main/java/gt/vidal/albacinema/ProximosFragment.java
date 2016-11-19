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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class ProximosFragment extends BaseFragment
{
    View view;
    private JsonArray peliculas;
    private RecyclerView recyclerPeliculas;
    private View.OnClickListener clickListener;

    public ProximosFragment()
    {
        // Required empty public constructor
    }

    private void fetchPeliculas()
    {
        new BackgroundTask<JsonElement>(() -> Api.instance.getJson("/Proximos/"), (json, exception) ->
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
        clickListener = v -> {
            int position = recyclerPeliculas.getChildLayoutPosition(v);
            PeliculaFragment f = new PeliculaFragment();
            f.pelicula = peliculas.get(position).getAsJsonObject();
            f.estreno = true;
            getBaseActivity().changeFragment(f);
        };
        recyclerPeliculas.setAdapter(new PeliculasRecyclerAdapter(peliculas, getBaseActivity(), clickListener));
        recyclerPeliculas.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
    }

    @Override
    public String getTitle()
    {
        return "Próximamente";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_proximos, container, false);
        recyclerPeliculas = (RecyclerView) view.findViewById(R.id.recycler);
        ((TextView)view.findViewById(R.id.txtHeader)).setText("Próximamente");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fetchPeliculas();
    }

}
