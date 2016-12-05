package gt.vidal.albacinema;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URLEncoder;

import static android.R.attr.button;
import static gt.vidal.albacinema.Images.get;
import static gt.vidal.albacinema.R.drawable.bistro;
import static gt.vidal.albacinema.R.id.lstHorarios;
import static gt.vidal.albacinema.R.id.txtAtributos;
import static gt.vidal.albacinema.R.id.txtHeader;
import static gt.vidal.albacinema.R.id.txtSala;
import static gt.vidal.albacinema.R.id.txtTitulo;


/**
 * A simple {@link Fragment} subclass.
 */
public class BoletosFragment extends BaseFragment {
    public JsonObject pelicula;
    public String fecha;
    public String cineNombre = "";
    public int cineId;
    public JsonObject horario;
    public int horarioSeleccionado;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_boletos, container, false);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llenarHeader();
        fetchBoletos();
    }

    private void llenarHeader(){
        ((TextView) view.findViewById(txtHeader)).setText(cineNombre);
        ((TextView) view.findViewById(txtTitulo)).setText(pelicula.get("Name").getAsString());
        ((TextView) view.findViewById(txtSala)).setText(horario.get("Sala").getAsString());
        ((TextView) view.findViewById(txtAtributos)).setText(horario.get("Attributes").getAsString().replace(";", " | "));

        JsonElement h = horario.get("Horas").getAsJsonArray().get(horarioSeleccionado);
        String hora = h.getAsJsonObject().get("hora").getAsString();
        ((TextView) view.findViewById(R.id.txtHora)).setText(hora.substring(0, hora.lastIndexOf(":")));


        String imgurl = pelicula.get("Url").getAsString();
        new BackgroundTask<Bitmap>(() -> Images.get(imgurl), (b, e) ->
        {
            if (e != null) throw new RuntimeException(e);
            ((ImageView)view.findViewById(R.id.imgPelicula)).setImageBitmap(b);
        }).execute();
    }

    private void fetchBoletos()
    {
        StringBuilder path = new StringBuilder("/boletos/precio/");
        path.append(cineId + "/");
        path.append(horario.get("Horas").getAsJsonArray().get(horarioSeleccionado).getAsJsonObject().get("IDFuncion").getAsString());

        new BackgroundTask<JsonArray>(() -> Api.instance.getJson(path.toString()).getAsJsonArray(), (arr, e) ->
        {
            if (e != null) throw new RuntimeException(e);
            if (arr != null) onBoletosFetched(arr);
        }).execute();
    }

    private void onBoletosFetched(JsonArray arr)
    {
        ListView lstBoletos = (ListView) view.findViewById(R.id.lstBoletos);
        lstBoletos.setAdapter(new BoletosFragment.BoletosAdapter(arr));
    }

    @Override
    public String getTitle()
    {
        return "Boletos";
    }

    private class BoletosAdapter extends BaseAdapter{

        private final JsonArray data;

        BoletosAdapter(JsonArray data)
        {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            LayoutInflater inflater = getBaseActivity().getLayoutInflater();

            if(v == null)
            {
                v = inflater.inflate(R.layout.row_boleto, parent, false);
            }

            JsonObject tipoBoleto = ((JsonElement) getItem(position)).getAsJsonObject();
            ((TextView)v.findViewById(R.id.txtTipo)).setText(tipoBoleto.get("Categoria").getAsString());
            float precio = tipoBoleto.get("Precio").getAsFloat();

            ((TextView)v.findViewById(R.id.txtPrecio)).setText("Q." + String.format(java.util.Locale.US,"%.2f", precio));
            TextView txtCantidad = (TextView)v.findViewById(R.id.txtCantidad);
            txtCantidad.setText("0");

            TextView txtTotal = (TextView)view.findViewById(R.id.txtTotal);

            ImageButton btnMenos = (ImageButton)v.findViewById(R.id.btnMenos);
            ImageButton btnMas = (ImageButton)v.findViewById(R.id.btnMas);

            btnMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    if(cantidad != 0){
                        txtCantidad.setText(String.valueOf(--cantidad));

                        float total = Float.parseFloat(txtTotal.getText().toString());
                        total -= precio;
                        txtTotal.setText(String.format(java.util.Locale.US,"%.2f", total));
                    }
                }
            });

            btnMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    if(cantidad != 10){
                        txtCantidad.setText(String.valueOf(++cantidad));

                        float total = Float.parseFloat(txtTotal.getText().toString());
                        total += precio;
                        txtTotal.setText(String.format(java.util.Locale.US,"%.2f", total));
                    }
                }
            });

            return v;
        }
    }
}
