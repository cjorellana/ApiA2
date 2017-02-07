package gt.vidal.albacinema;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alejandroalvarado on 5/09/16.
 */
public class Api
{

    private String url;
    JsonParser parser;

    public static Api instance;

    public Api(String url)
    {
        this.url = url;
        parser = new JsonParser();
    }


    public JsonElement getJson(String path) throws Exception
    {
        StringBuffer chaine = new StringBuffer("");
        URL url = new URL(this.url + path);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("User-Agent", "");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        while ((line = rd.readLine()) != null) {
            chaine.append(line);
        }
        return parser.parse(chaine.toString());
    }

    public Bitmap getImage(String name) throws Exception
    {
        return getImage(name, true);
    }

    public Bitmap getImage(String path, boolean useBaseUrl) throws Exception
    {
        path = path.replace(" ","%20");
        String address = useBaseUrl ? this.url + path : path;
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        return myBitmap;
    }
}
