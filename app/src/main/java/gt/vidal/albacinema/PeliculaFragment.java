package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

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
    public boolean bistro;
    private ArrayList<String> fechas = new ArrayList<>();
    public boolean estreno;

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
        if (estreno)
        {
            scrSinopsis.setVisibility(View.VISIBLE);
            lstHorarios.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            txtHeader.setText("Sinopsis");
            return;
        }

        setupTabs();
        if (fecha != null)
        {
            fetchHorarios();
        }
        else
        {
            this.estreno = true;
            fetchFechas();
        }
    }

    private void fetchFechas()
    {
        StringBuilder path = new StringBuilder("/peliculas/filtrarFechas?idcine=");
        path.append(cineId);
        path.append("&pelicula=");
        path.append(URLEncoder.encode(pelicula.get("Name").getAsString()));
        new BackgroundTask<JsonElement>(() -> Api.instance.getJson(path.toString()), (json, exception) ->
        {
            if (exception != null) {throw new RuntimeException(exception);}
            if (json != null)
            {
                for (JsonElement f : json.getAsJsonArray())
                {
                    fechas.add(f.getAsJsonObject().get("fecha").getAsString());
                }

                llenarFechas();
                fetchHorarios();
            }
        }).execute();
    }

    private void llenarFechas()
    {
        getBaseActivity().getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.spinner_toolbar, null);
        Spinner spinnerFechas = (Spinner) vi.findViewById(R.id.spin);
        ArrayAdapter<String> spinneradapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, fechas);
        spinnerFechas.setAdapter(spinneradapter);
        getBaseActivity().getSupportActionBar().setCustomView(vi);
        spinneradapter.notifyDataSetChanged();

        spinnerFechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                fecha = fechas.get(position);
                fetchHorarios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
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

        Log.d("meow", path.toString());

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
            LayoutInflater inflater = getBaseActivity().getLayoutInflater();

            if(v == null)
            {
                v = inflater.inflate(R.layout.row_horario, parent, false);
            }

            JsonObject horario = ((JsonElement) getItem(position)).getAsJsonObject();
            ((TextView)v.findViewById(R.id.txtSala)).setText(horario.get("Sala").getAsString());
            ((TextView)v.findViewById(R.id.txtAtributos))
                    .setText(horario.get("Attributes").getAsString().replace(";", " | "));

            SegmentedGroup sg = (SegmentedGroup) v.findViewById(R.id.sgmHoras);
            for (JsonElement h: horario.get("Horas").getAsJsonArray())
            {
                RadioButton button = (RadioButton) inflater.inflate(R.layout.segment, null);
                String hora = h.getAsJsonObject().get("hora").getAsString();
                hora = hora.substring(0, hora.lastIndexOf(":"));
                button.setText(hora);
                button.setEnabled(false);
                sg.addView(button);
                sg.updateBackground();
            }

            return v;
        }
    }
}
