package gt.vidal.albacinema;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;


public class FullscreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private List<Bitmap>  _images;
    private LayoutInflater inflater;

    // constructor
    public FullscreenImageAdapter(Activity activity,
                                  List<Bitmap> images) {
        this._activity = activity;
        this._images = images;
    }

    @Override
    public int getCount()
    {
        return this._images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        TouchImageView imgDisplay;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewLayout = inflater.inflate(R.layout.layout_image, container, false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);

        imgDisplay.setImageBitmap(_images.get(position));

        // close button click event
        btnClose.setOnClickListener(v -> _activity.finish());

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }
}