package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by alejandroalvarado on 14/09/16.
 */
public class PeliculasCineFragment extends BaseFragment
{
    public int cineID;
    public String titulo;


    View view;
    ListView lstPeliculas;
    Spinner spinnerFechas;
    ArrayList<String> fechas = new ArrayList<>();
    int fechaSeleccionada = 0;
    private boolean bistro;
    private JsonArray peliculas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_peliculas_cine, container, false);
        lstPeliculas = (ListView) view.findViewById(R.id.lstPeliculas);
        ((TextView)view.findViewById(R.id.txtHeader)).setText(titulo);
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
                for (JsonElement f : json.getAsJsonArray())
                {
                    fechas.add(f.getAsJsonObject().get("fecha").getAsString());
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
        lstPeliculas.setAdapter(new PeliculasAdapter(peliculas));
        lstPeliculas.setOnItemClickListener((parent, view, position, id) -> onItemSelected(position));
    }

    private void onItemSelected(int position)
    {
        PeliculaFragment f = new PeliculaFragment();
        f.pelicula = peliculas.get(position).getAsJsonObject();
        f.fecha = fechas.get(fechaSeleccionada);
        f.cineId = cineID;
        getBaseActivity().changeFragment(f);
    }

    private void llenarFechas()
    {
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

    class PeliculasAdapter extends BaseAdapter
    {
        private final JsonArray data;

        PeliculasAdapter(JsonArray data)
        {
            this.data = data;
        }

        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;

            if(v == null)
            {
                LayoutInflater inflater = getBaseActivity().getLayoutInflater();
                v = inflater.inflate(R.layout.row_pelicula, parent, false);
            }

            JsonObject pelicula = ((JsonElement) getItem(position)).getAsJsonObject();
            ((TextView) v.findViewById(R.id.txtTitulo)).setText(pelicula.get("Name").getAsString());
            ((TextView) v.findViewById(R.id.txtDuracion)).setText(pelicula.get("Duracion").getAsString() + " min.");
            ((TextView) v.findViewById(R.id.txtGenero)).setText(pelicula.get("Genero").getAsString());
            ((TextView) v.findViewById(R.id.txtRating)).setText(pelicula.get("Rating").getAsString());

            String sinopsis = pelicula.get("Sinopsis").getAsString();

            int maxlength = 100;
            if (sinopsis.length() > maxlength)
            {
                sinopsis = sinopsis.substring(0, maxlength);
                int lastSpace = sinopsis.lastIndexOf(' ');
                if (lastSpace > 0)
                    sinopsis = sinopsis.substring(0, lastSpace);
                sinopsis += "...";
            }

            ((TextView) v.findViewById(R.id.txtSinopsis)).setText(sinopsis);
            final View tv = v;

            String imgurl = pelicula.get("Url").getAsString();
            new BackgroundTask<Bitmap>(() -> Images.get(imgurl), (b, e) ->
            {
                if (e != null) throw new RuntimeException(e);
                ((ImageView)tv.findViewById(R.id.imgPelicula)).setImageBitmap(b);
            }).execute();

            return v;
        }
    }
}
