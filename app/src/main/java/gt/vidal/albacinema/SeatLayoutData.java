package gt.vidal.albacinema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by alejandroalvarado on 23/11/16.
 */

public class SeatLayoutData
{

    private int rowCount = 16;
    private int columnCount = 20;
    private JsonArray rows;



    public SeatLayoutData(JsonObject rowData)
    {
        this.rowCount = rowData.get("RowCount").getAsInt();
        this.columnCount = rowData.get("ColumnCount").getAsInt();
        this.rows = rowData.get("Rows").getAsJsonArray();
    }

    public JsonObject getRowOrNull(int row)
    {
        JsonElement rowElement = rows.get(row);
        return rowElement.isJsonNull() ? null : rowElement.getAsJsonObject();
    }

    public int getColumnCount()
    {
        return columnCount;
    }

    public int getRowCount()
    {
        return rowCount;
    }


}
