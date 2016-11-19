package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by alejandroalvarado on 24/09/16.
 */
public class PeliculasRecyclerAdapter extends RecyclerView.Adapter<ViewHolderPelicula>
{
    private JsonArray peliculas;
    private BaseActivity ctx;
    private View.OnClickListener clickListener;

    PeliculasRecyclerAdapter(JsonArray peliculas, BaseActivity ctx, View.OnClickListener clickListener)
    {
        this.peliculas = peliculas;
        this.ctx = ctx;
        this.clickListener = clickListener;
    }


    @Override
    public ViewHolderPelicula onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ViewHolderPelicula vh = new ViewHolderPelicula(ctx.getLayoutInflater().inflate(R.layout.card_pelicula, parent, false));
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
