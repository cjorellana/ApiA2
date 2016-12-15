package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by alejandroalvarado on 23/09/16.
 */
public class PeliculasCardFragment extends BaseFragment
{
    public int cineID;
    public String titulo;


    View view;
    RecyclerView recyclerPeliculas;
    Spinner spinnerFechas;
    ArrayList<String> fechas = new ArrayList<>();
    int fechaSeleccionada = 0;
    private JsonArray peliculas;
    public boolean bistro;
    private View.OnClickListener clickListener;
    public String cineNombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_peliculas_card, container, false);
        recyclerPeliculas = (RecyclerView) view.findViewById(R.id.recycler);
        ((TextView)view.findViewById(R.id.txtHeader)).setText(titulo);

        clickListener = v -> {
            int position = recyclerPeliculas.getChildLayoutPosition(v);
            PeliculaFragment f = new PeliculaFragment();
            f.pelicula = peliculas.get(position).getAsJsonObject();
            f.fecha = fechas.get(fechaSeleccionada);
            f.cineId = cineID;
            f.bistro = bistro;
            f.cineNombre = cineNombre;
            getBaseActivity().changeFragment(f);
        };
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fetchFechas();
    }

    private void fetchFechas()
    {
        new BackgroundTask<JsonElement>(() -> Api.instance.getJson("/peliculas/filtrarFechas?idcine=" + cineID), (json, exception) ->
        {
            if (exception != null) {throw new RuntimeException(exception);}
            if (json != null)
            {
                fechas.clear();
                for (JsonElement f : json.getAsJsonArray())
                {
                    fechas.add(f.getAsJsonObject().get("fecha").getAsString());
                }

                if (fechas.size() == 0)
                {
                    showEmptyView("No hay películas disponibles en este cine.");
                    return;
                }

                llenarFechas();
                fetchPeliculas();
            }
        }).execute();
    }

    private void fetchPeliculas()
    {
        StringBuilder path = new StringBuilder("/peliculas/xcine2/");
        path.append(cineID);
        path.append("?fecha=");
        path.append(fechas.get(fechaSeleccionada));
        path.append(bistro ? "&bistro=1" : "");

        new BackgroundTask<JsonElement>(() -> Api.instance.getJson(path.toString()), (json, exception) ->
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


    private void llenarFechas()
    {
        if (getBaseActivity() == null)
            return;

        getBaseActivity().getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.spinner_toolbar, null);
        spinnerFechas = (Spinner) vi.findViewById(R.id.spin);
        ArrayAdapter<String> spinneradapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, fechas);
        spinnerFechas.setAdapter(spinneradapter);
        getBaseActivity().getSupportActionBar().setCustomView(vi);
        spinneradapter.notifyDataSetChanged();

        spinnerFechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                fechaSeleccionada = position;
                fetchPeliculas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }




}
