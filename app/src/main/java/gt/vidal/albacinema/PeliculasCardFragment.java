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
        for (JsonElement p : peliculas)
        {
            p.getAsJsonObject().addProperty("Expanded", false);
        }
        recyclerPeliculas.setAdapter(new Adapter(peliculas));
        recyclerPeliculas.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
    }

    private void onItemSelected(int position)
    {
        PeliculaFragment f = new PeliculaFragment();
        f.pelicula = peliculas.get(position).getAsJsonObject();
        f.fecha = fechas.get(fechaSeleccionada);
        f.cineId = cineID;
        f.bistro = bistro;
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

    class ViewHolderPelicula extends RecyclerView.ViewHolder
    {
        private final TextView txtTitulo;
        private final ImageView imgPelicula;
        private final TextView txtRating;
        private final TextView txtGenero;
        private final TextView txtDuracion;
        private final TextView txtSinopsis;
        private final ImageButton btnExpand;
        public final View view;
        public ViewHolderPelicula(View view)
        {
            super(view);
            this.view = view;
            txtTitulo = (TextView)view.findViewById(R.id.txtTitulo);
            imgPelicula = (ImageView) view.findViewById(R.id.imgPelicula);
            txtSinopsis = (TextView) view.findViewById(R.id.txtSinopsis);
            txtRating = (TextView) view.findViewById(R.id.txtRating);
            txtGenero = (TextView) view.findViewById(R.id.txtGenero);
            txtDuracion = (TextView) view.findViewById(R.id.txtDuracion);
            btnExpand = (ImageButton) view.findViewById(R.id.btnExpand);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolderPelicula> {
        private JsonArray peliculas;

        Adapter(JsonArray peliculas)
        {
            this.peliculas = peliculas;
        }


        @Override
        public ViewHolderPelicula onCreateViewHolder(ViewGroup parent, int viewType)
        {
            ViewHolderPelicula vh = new ViewHolderPelicula(getBaseActivity().getLayoutInflater().inflate(R.layout.card_pelicula, parent, false));
            vh.view.setOnClickListener(clickListener);
            return vh;
        }

        public String cutSinopsis(String sinopsis, int maxlength)
        {
            if (sinopsis.length() > maxlength)
            {
                sinopsis = sinopsis.substring(0, maxlength);
                int lastSpace = sinopsis.lastIndexOf(' ');
                if (lastSpace > 0)
                    sinopsis = sinopsis.substring(0, lastSpace);
                sinopsis += "...";
            }
            return sinopsis;
        }

        public void setSinopsis(String sinopsis, ViewHolderPelicula holder, boolean expanded)
        {
            sinopsis = expanded ? sinopsis : cutSinopsis(sinopsis, 100);
            holder.txtSinopsis.setText(sinopsis);
        }

        @Override
        public void onBindViewHolder(ViewHolderPelicula holder, int position)
        {
            JsonObject pelicula = peliculas.get(position).getAsJsonObject();
            holder.txtTitulo.setText(pelicula.get("Name").getAsString());
            holder.txtDuracion.setText(pelicula.get("Duracion").getAsString() + " min.");
            holder.txtGenero.setText(pelicula.get("Genero").getAsString());
            holder.txtRating.setText(pelicula.get("Rating").getAsString());
            setSinopsis(pelicula.get("Sinopsis").getAsString(), holder, pelicula.get("Expanded").getAsBoolean());


            holder.btnExpand.setOnClickListener(v ->
            {
                boolean expanded = !pelicula.get("Expanded").getAsBoolean();
                setSinopsis(pelicula.get("Sinopsis").getAsString(), holder, expanded);
                pelicula.addProperty("Expanded", expanded);
                holder.btnExpand.setImageResource( expanded ? R.drawable.collapse : R.drawable.expand);
            });

            String imgurl = pelicula.get("Url").getAsString();
            new BackgroundTask<Bitmap>(() -> Images.get(imgurl), (b, e) ->
            {
                if (e != null) throw new RuntimeException(e);
                holder.imgPelicula.setImageBitmap(b);
            }).execute();
        }

        @Override
        public int getItemCount()
        {
            return peliculas.size();
        }



    }
}
