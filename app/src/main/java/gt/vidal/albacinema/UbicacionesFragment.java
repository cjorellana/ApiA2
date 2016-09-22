package gt.vidal.albacinema;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Locale;

public class UbicacionesFragment extends BaseFragment
{


    private ListView lstUbicaciones;
    private JsonArray cines;

    public UbicacionesFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ubicaciones, container, false);
        lstUbicaciones = (ListView) v.findViewById(R.id.lstUbicaciones);
        return v;
    }


    private void onItemSelected(int position)
    {
        JsonObject cine = cines.get(position).getAsJsonObject();
        float latitude = cine.get("Latitude").getAsFloat();
        float longitude = cine.get("Longitude").getAsFloat();
        String name = cine.get("Name").getAsString();
        final String uri = String.format(Locale.ENGLISH, "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", name, latitude, longitude), "UTF-8");
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        new BackgroundTask<JsonElement>(() -> Api.instance.getJson("/cines"), (json, exception) ->
        {
            if (exception != null) {throw new RuntimeException(exception);}
            if (json != null) onInfoFetched(json);
        }).execute();
    }

    private void onInfoFetched(JsonElement json)
    {
        cines = json.getAsJsonArray();
        lstUbicaciones.setAdapter(new CinesAdapter(cines));
        lstUbicaciones.setOnItemClickListener((parent, view, position, id) -> onItemSelected(position));
    }

    @Override
    public String getTitle()
    {
        return "Ubicaciones";
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
                v = inflater.inflate(R.layout.row_ubicacion, parent, false);
            }

            JsonElement cine = (JsonElement) getItem(position);
            ((TextView) v.findViewById(R.id.txtNombre)).setText(cine.getAsJsonObject().get("Name").getAsString());
            ((TextView) v.findViewById(R.id.txtAddress1)).setText(cine.getAsJsonObject().get("Address1").getAsString());
            ((TextView) v.findViewById(R.id.txtAddress2)).setText(cine.getAsJsonObject().get("Address2").getAsString());
            ((TextView) v.findViewById(R.id.txtParking)).setText(cine.getAsJsonObject().get("ParkingInfo").getAsString());

            return v;
        }
    }

}
