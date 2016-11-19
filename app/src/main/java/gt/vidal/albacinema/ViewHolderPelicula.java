package gt.vidal.albacinema;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alejandroalvarado on 24/09/16.
 */
public class ViewHolderPelicula extends RecyclerView.ViewHolder
{
    public final TextView txtTitulo;
    public final ImageView imgPelicula;
    public final TextView txtRating;
    public final TextView txtGenero;
    public final TextView txtDuracion;
    public final TextView txtSinopsis;
    public final ImageButton btnExpand;
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
