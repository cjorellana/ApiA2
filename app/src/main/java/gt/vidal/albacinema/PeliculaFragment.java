package gt.vidal.albacinema;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URLEncoder;

/**
 * Created by alejandroalvarado on 16/09/16.
 */
public class PeliculaFragment extends BaseFragment
{

    public JsonObject pelicula;
    public String fecha;
    public int cineId;

    private View view;
    private TabLayout tabLayout;
    private TextView txtHeader;
    private ListView lstHorarios;
    private ScrollView scrSinopsis;
    private boolean bistro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_pelicula, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        txtHeader = (TextView) view.findViewById(R.id.txtHeader);
        lstHorarios = (ListView) view.findViewById(R.id.lstHorarios);
        scrSinopsis = (ScrollView) view.findViewById(R.id.scrSinopsis);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        llenarHeader();
        setupTabs();

        if (fecha != null)
        {
            fetchHorarios();
        }
    }

    private void fetchHorarios()
    {
        String peliId = URLEncoder.encode(pelicula.get("Name").getAsString());
        StringBuilder path = new StringBuilder("/peliculas/xhorario2/");
        path.append(cineId + "/");
        path.append(peliId);
        path.append("?fecha=");
        path.append(fecha);
        path.append(bistro ? "&bistro=1" : "");

        new BackgroundTask<JsonArray>(() -> Api.instance.getJson(path.toString()).getAsJsonArray(), (arr, e) ->
        {
            if (e != null) throw new RuntimeException(e);
            if (arr != null) onHorariosFetched(arr);
        }).execute();
    }

    private void onHorariosFetched(JsonArray arr)
    {
        lstHorarios.setAdapter(new HorariosAdapter(arr));
    }

    private void llenarHeader()
    {
        ((TextView) view.findViewById(R.id.txtTitulo)).setText(pelicula.get("Name").getAsString());
        ((TextView) view.findViewById(R.id.txtDuracion)).setText("Duración: " + pelicula.get("Duracion").getAsString() + " minutos");
        ((TextView) view.findViewById(R.id.txtGenero)).setText("Género: " + Utils.firstCaptial(pelicula.get("Genero").getAsString()));
        ((TextView) view.findViewById(R.id.txtRating)).setText("Clasificación: " + pelicula.get("Rating").getAsString());
        ((TextView) view.findViewById(R.id.txtSinopsis)).setText(pelicula.get("Sinopsis").getAsString());

        String imgurl = pelicula.get("Url").getAsString();
        new BackgroundTask<Bitmap>(() -> Images.get(imgurl), (b, e) ->
        {
            if (e != null) throw new RuntimeException(e);
            ((ImageView)view.findViewById(R.id.imgPelicula)).setImageBitmap(b);
        }).execute();
    }

    @Override
    public String getTitle()
    {
        return "Horarios";
    }

    private void setupTabs()
    {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if (tab.getText().equals("Horarios"))
                {
                    lstHorarios.setVisibility(View.VISIBLE);
                    scrSinopsis.setVisibility(View.GONE);
                }
                else
                {
                    scrSinopsis.setVisibility(View.VISIBLE);
                    lstHorarios.setVisibility(View.GONE);
                }

                txtHeader.setText(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }


    class HorariosAdapter extends BaseAdapter
    {
        private final JsonArray data;

        HorariosAdapter(JsonArray data)
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
                v = inflater.inflate(R.layout.row_horario, parent, false);
            }

            JsonElement horario = (JsonElement) getItem(position);

            return v;
        }
    }
}
