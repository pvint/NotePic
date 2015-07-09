package ca.dotslash.pvint.notepic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by pvint on 01/07/15.
 */
public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFFFF0000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    public String text;
    private Paint textPaint;
    private float textSize;


    public boolean placeText = false;
    public float textX, textY;

    private float brushSize, lastBrushSize;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        brushSize = 20;
        textSize = 20.0f;
        text = "";
        setupDrawing();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

/*        if (text != "")
        {
            canvas.drawText(text, 0, 0, textPaint);
            text = "";
        }*/
    }

    public void drawText( Context ctx ) {


        ImageView i = (ImageView) findViewById(R.id.imageTextView);
        TextDrawable textDrawable = new TextDrawable(ctx);
        textDrawable.setText(text);
        textDrawable.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        textDrawable.setTextColor(getPaintColor());
        // position textDrawable.set
        textDrawable.left = textX;
        textDrawable.top = textY;

        i.setImageDrawable(textDrawable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ( placeText == true )
                {
                    // return text coords
                    textX = touchX;
                    textY = touchY;
                    placeText = false;
                    drawPath.reset();
                    //drawText(text);



                    break;
                }
                else {
                    drawPath.moveTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setBrushSize(float s)
    {
        lastBrushSize = brushSize;
        brushSize = s;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setPaintColor(int c)
    {
        paintColor = c;
        drawPaint.setColor(paintColor);
    }

    public int getPaintColor()
    {
        return drawPaint.getColor();

    }
    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

//    public void drawText( String t )
//    {
//        drawCanvas.drawText("Some Text", 100, 125, drawPaint); // nope
//    }

    // Text stuff
    public void paintText( String text )
    {
        this.text = text;
        this.textPaint = new Paint();

        textPaint.setColor( paintColor );
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer( 6.0f, 0.0f, 0.0f, Color.BLACK);
        textPaint.setStyle( Paint.Style.FILL);
        textPaint.setTextAlign( Paint.Align.LEFT);


    }

}
