package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    public EstrenosFragment()
    {

    }

    public int cineID;
    public String titulo;


    View view;
    ListView lstPeliculas;
    Spinner spinnerFechas;
    private JsonArray peliculas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_estrenos, container, false);
        lstPeliculas = (ListView) view.findViewById(R.id.lstPeliculas);
        ((TextView)view.findViewById(R.id.txtHeader)).setText(titulo);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fetchPeliculas();
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
        lstPeliculas.setAdapter(new PeliculasAdapter(peliculas));
        lstPeliculas.setOnItemClickListener((parent, view, position, id) -> onItemSelected(position));
    }

    private void onItemSelected(int position)
    {
        CinesFragment f = new CinesFragment();
        f.peliEstreno = peliculas.get(position).getAsJsonObject();
        getBaseActivity().changeFragment(f);
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
