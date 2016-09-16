package gt.vidal.albacinema;

import android.graphics.Bitmap;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by alejandroalvarado on 14/09/16.
 */
public class Images
{
    public static Api imagesApi;
    public static HashMap<String, Bitmap> images = new HashMap<>();

    public static void init()
    {
        new BackgroundTask<JsonElement>(() -> Api.instance.getJson("/urlimg"), (j, e) ->
        {
            Images.imagesApi = new Api(j.getAsString());
        }).execute();
    }

    public static Bitmap get(String name) throws Exception
    {
        if (images.containsKey(name))
            return images.get(name);

        Bitmap b = imagesApi.getImage(name);
        images.put(name, b);
        return b;
    }
}
