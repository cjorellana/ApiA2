package gt.vidal.albacinema;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
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
        URL url = new URL(this.url + name);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        return myBitmap;
    }
}
