package gt.vidal.albacinema;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by alejandroalvarado on 23/11/16.
 */

public class AsientosView extends View
{
    private Paint seatTextPaint;

    private Paint backgroundPaint;

    public SeatLayoutData layoutData;

    private float rowHeight = 0;
    private float columnWidth = 0;
    private float drawingHeight, drawingWidth;
    private float padx = 5;
    private float pady = 5;
    private float seatSize;
    private float scaleFactor = 1.0f;


    private RectF backgroundRect;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    private Bitmap bmapRed;
    private Bitmap bmapGreen;
    private Bitmap bmapGray;
    private PointF mid = new PointF();

    int NONE = 0;
    int DRAG = 1;
    int ZOOM = 2;

    private int mode = NONE;
    private boolean dragged;
    private float startX = 0f;
    private float startY = 0f;
    private float translateX = 0f;
    private float translateY = 0f;
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;
    private Paint redPaint;

    public AsientosView(Context context)
    {
        this(context, null);
    }

    public AsientosView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
        init();
    }

    private void init()
    {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.BLACK);

        seatTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        seatTextPaint.setColor(Color.BLACK);
        seatTextPaint.setTextSize(10f);

        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);

        bmapRed = BitmapFactory.decodeResource(getResources(), R.drawable.chairred2);
        bmapGreen = BitmapFactory.decodeResource(getResources(), R.drawable.chairgreen2);
        bmapGray = BitmapFactory.decodeResource(getResources(), R.drawable.chairgray);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


       /* if (rowHeight != 0 && columnWidth != 0)
        {
            super.onMeasure(MeasureSpec.makeMeasureSpec((int)Math.ceil(drawingWidth + padx * 2), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec((int)Math.ceil(drawingHeight + pady * 2), MeasureSpec.EXACTLY));
        }
        else
        {*/
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //}



    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d("Cines", "Old " + oldw + " " + oldh + " new " + w + " " + h);
        if (w > h)
        {
            drawingHeight = h - pady * 2;

            this.columnWidth = drawingHeight / (layoutData.getRowCount() + 1);
            drawingWidth = (layoutData.getColumnCount() + 1) * columnWidth;

            padx = (w - drawingWidth) / 2;
        }
        else
        {
            drawingHeight = h - pady * 2;
            drawingWidth = w - padx * 2;

            this.columnWidth = drawingWidth / (layoutData.getColumnCount() + 1);
            drawingHeight= (layoutData.getRowCount() + 1) * columnWidth;
        }



        //noinspection SuspiciousNameCombination

        this.rowHeight = columnWidth;

        this.backgroundRect = new RectF(padx, pady, drawingWidth + padx, drawingHeight + pady);

        this.seatSize = columnWidth * 0.8f;

        Log.d("Cines", "sizeC " + drawingWidth +  " " + drawingHeight);

        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                requestLayout();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;

                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                startX = event.getX() - previousTranslateX;
                startY = event.getY() - previousTranslateY;

                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == ZOOM)
                    break;

                translateX = event.getX() - startX;
                translateY = event.getY() - startY;

                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) +
                        Math.pow(event.getY() - (startY + previousTranslateY), 2)
                );

                if(distance > 0) {
                    dragged = true;
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                midPoint(mid, event);
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                dragged = false;

                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslate
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;

                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
        }

        if ((mode == DRAG && scaleFactor != 1f && dragged) || mode == ZOOM) {
            this.invalidate();
        }

        boolean retVal = scaleDetector.onTouchEvent(event);
        //retVal = gestureDetector.onTouchEvent(event) || retVal;
        return true;
    }
    // calculate the mid point of the first two fingers
    private void midPoint(PointF point, MotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.save();

/*
        translateX = clamp(translateX, drawingWidth * (1/scaleFactor - 1)/2, drawingWidth * (1 - 1/scaleFactor)/2);
        translateY = clamp(translateY, drawingHeight* (1/scaleFactor - 1)/2, drawingHeight * (1 - 1/scaleFactor)/2);
*/


        canvas.scale(scaleFactor, scaleFactor, mid.x, mid.y);
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor);

        drawBackground(canvas);
        drawSeats(canvas);
        canvas.drawCircle(mid.x, mid.y, 5, redPaint);

        canvas.restore();
    }

    private void drawSeats(Canvas canvas)
    {
        float y = pady + rowHeight / 2;

        for (int rowIndex = 0; rowIndex < layoutData.getRowCount(); rowIndex++, y += rowHeight)
        {
            float x = Math.abs((drawingWidth - columnWidth * (layoutData.getColumnCount())) / 2 + padx);

            JsonObject row = this.layoutData.getRowOrNull(rowIndex);

            if (row == null)
                continue;

            JsonArray seats = row.get("Seats").getAsJsonArray();

            for (int colIndex = 0; colIndex < layoutData.getColumnCount(); colIndex++, x += columnWidth)
            {
                JsonElement seatElement = seats.get(colIndex);
                if (seatElement.isJsonNull())
                    continue;
                JsonObject seat = seatElement.getAsJsonObject();
                String status = seat.get("Status").getAsString();
                String name = row.get("PhysicalName").getAsString() + "-" + seat.get("Id").getAsInt();

                drawSeat(canvas, y, x, status, name);
            }
        }
    }

    private void drawSeat(Canvas canvas, float y, float x, String status, String name)
    {
        Bitmap bm = bmapGray;

        if (status.equals("Sold"))
            bm = bmapGreen;
        else if (status.equals("Reserved"))
            bm = bmapRed;

        RectF rect = new RectF(x, y, x + seatSize, y + seatSize);

        canvas.drawBitmap(bm, null, rect, null);

        float textWidth = seatTextPaint.measureText(name);
        float textMargin = (seatSize - textWidth) / 2;
        canvas.drawText(name, x + textMargin, y + seatSize*0.8f, seatTextPaint);


    }

    private void drawBackground(Canvas canvas)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            canvas.drawRoundRect(backgroundRect, 8, 8, backgroundPaint);
        }
        else
        {
            canvas.drawRect(backgroundRect, backgroundPaint);
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            scaleFactor *= detector.getScaleFactor();

            scaleFactor = Math.max(1f, Math.min(scaleFactor, 2.5f));

            invalidate();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {

    }

    float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
