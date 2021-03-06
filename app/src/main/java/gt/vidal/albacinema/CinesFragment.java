package gt.vidal.albacinema;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alejandroalvarado on 7/09/16.
 */
public class CinesFragment extends BaseFragment
{
    View view;
    ListView lstCines;
    JsonArray cines;
    JsonObject peliEstreno;
    public boolean bistro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        /*if (getParentFragment() == null)
            setRetainInstance(true);*/
        view = inflater.inflate(R.layout.fragment_cines, container, false);
        lstCines = (ListView) view.findViewById(R.id.lstCines);
        return view;
    }

    private void onItemSelected(int position)
    {
        JsonObject cine = cines.get(position).getAsJsonObject();
        if (peliEstreno == null)
        {
            PeliculasCardFragment f = new PeliculasCardFragment();
            f.cineID = cine.get("ID").getAsInt();
            f.cineNombre = cine.get("Name").getAsString();
            f.titulo = cine.get("Name").getAsString();
            f.bistro = bistro;
            getBaseActivity().changeFragment(f);
        }
        else
        {
            PeliculaFragment f = new PeliculaFragment();
            f.pelicula = peliEstreno;
            f.cineId = cine.get("ID").getAsInt();
            f.cineNombre = cine.get("Name").getAsString();
            getBaseActivity().changeFragment(f);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        String path = peliEstreno == null ? "/cines" + (bistro ? "/bistro" : "") : "/cines/xpelicula/" + URLEncoder.encode(peliEstreno.get("Name").getAsString());

        new BackgroundTask<JsonElement>(() -> Api.instance.getJson(path), (json, exception) ->
                                        {
                                            if (exception != null) {throw new RuntimeException(exception);}
                                            if (json != null) onInfoFetched(json);
                                        }).execute();
    }

    private void onInfoFetched(JsonElement json)
    {
        cines = json.getAsJsonArray();
        lstCines.setAdapter(new CinesAdapter(cines));
        lstCines.setOnItemClickListener((parent, view, position, id) -> onItemSelected(position));
    }

    @Override
    public String getTitle()
    {
        return "Cartelera";
    }

    class CinesAdapter extends BaseAdapter
    {
        private final JsonArray data;

        CinesAdapter(JsonArray data)
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
                v = inflater.inflate(R.layout.row_cine, parent, false);
            }

            JsonElement cine = (JsonElement) getItem(position);
            ((TextView) v.findViewById(R.id.txtNombre)).setText(cine.getAsJsonObject().get("Name").getAsString());

            return v;
        }
    }
}
