package ca.dotslash.pvint.notepic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    // Camera stuff
    private Camera mCamera;
    private CameraPreview mCPreview;
    private android.hardware.Camera.PictureCallback mPicture;
    private Button capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private Preview mPreview;

    private ContentValues values;
    private Intent photoIntent;
    private ImageView imageView;

    private int screenWidth;
    private int screenHeight;

    public String photoFilename = "photo.jpeg";
    public final String APP_TAG = "NotePic";



    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Uri imageUri;

    // drawing stuff
    private DrawingView drawView;
    private HSVColorPicker cpd;
    private float brushSize, lastBrushSize;
    private float smallBrush, mediumBrush, largeBrush, xlargeBrush;

    public Dialog brushDialog, colorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myContext = this;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
//        mPreview = new Preview(this);
//        setContentView(mPreview);
        //initialize();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        imageView = (ImageView) findViewById(R.id.imageView);
        Drawable d = Drawable.createFromPath("/storage/sdcard0/test.jpg");
        //imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageDrawable(d);




//create parameters for Intent with filename
        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, photoFilename);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//create new Intent

        photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment
                .getExternalStorageDirectory(),
                "test.jpg");
        imageUri = Uri.fromFile(file);

        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                imageUri);



        drawView = (DrawingView) findViewById(R.id.drawing);
        //drawView.setColor(color);



        // set up the brush button listeners etc
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        xlargeBrush = getResources().getInteger(R.integer.xlarge_size);
        colorDialog = new Dialog(this);
        colorDialog.setContentView(R.layout.color_picker);
        brushDialog = new Dialog(this);
        brushDialog.setTitle("Brush size:");
        brushDialog.setContentView(R.layout.brush_chooser);
        ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton medBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        medBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton xlargeBtn = (ImageButton) brushDialog.findViewById(R.id.xlarge_brush);
        xlargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setBrushSize(xlargeBrush);
                brushDialog.dismiss();
            }
        });

        // set up color picker button listeners
        ImageButton colorButton1 = (ImageButton) colorDialog.findViewById(R.id.color1);
        colorButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });
        ImageButton colorButton2 = (ImageButton) colorDialog.findViewById(R.id.color2);
        colorButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });

        ImageButton colorButton3 = (ImageButton) colorDialog.findViewById(R.id.color3);
        colorButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });
        ImageButton colorButton4 = (ImageButton) colorDialog.findViewById(R.id.color4);
        colorButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });

        ImageButton colorButton5 = (ImageButton) colorDialog.findViewById(R.id.color5);
        colorButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton6 = (ImageButton) colorDialog.findViewById(R.id.color6);
        colorButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton7 = (ImageButton) colorDialog.findViewById(R.id.color7);
        colorButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton8 = (ImageButton) colorDialog.findViewById(R.id.color8);
        colorButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton9 = (ImageButton) colorDialog.findViewById(R.id.color9);
        colorButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton10 = (ImageButton) colorDialog.findViewById(R.id.color10);
        colorButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });
        ImageButton colorButton11 = (ImageButton) colorDialog.findViewById(R.id.color11);
        colorButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });

        ImageButton colorButton12 = (ImageButton) colorDialog.findViewById(R.id.color12);
        colorButton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });

        ImageButton colorButton13 = (ImageButton) colorDialog.findViewById(R.id.color13);
        colorButton13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton14 = (ImageButton) colorDialog.findViewById(R.id.color14);
        colorButton14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });

        ImageButton colorButton15 = (ImageButton) colorDialog.findViewById(R.id.color15);
        colorButton15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });


        ImageButton colorButton16 = (ImageButton) colorDialog.findViewById(R.id.color16);
        colorButton16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor(Color.parseColor(v.getTag().toString()));
                colorDialog.dismiss();
            }

        });
    }

        // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(APP_TAG, RESULT_OK + " Result: " + resultCode);
        Log.d(APP_TAG, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE + " Request: " + requestCode);

        if (requestCode == 0) { //CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Drawable d = Drawable.createFromPath("/storage/sdcard0/test.jpg");
                //imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageDrawable( d );
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled - Picture was not taken", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth,
                                             int reqHeight) {

        Bitmap bm = null;

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        return bm;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        switch (id) {
            /*case R.id.action_settings:
                // Settings menu
                break;
*/
            case R.id.action_palette:
                // Colour picker
                /*cpd.show();  // This is not working correctly... just using a simple 16 colour palette for now
                int c = cpd.getColor();
                drawView.setPaintColor(c);*/
                colorDialog.show();
                break;
            case R.id.action_toolbox:
                // Open toolbox menu


                brushDialog.show();
                break;

            case R.id.action_undo:
                // undo last action
                break;

            case R.id.action_camera:
                // prompt to take new picture

                startActivityForResult(photoIntent, 0); //CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;

            case R.id.action_save:
                // Save image
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Save drawing to device Gallery?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        //save drawing
                        drawView.setDrawingCacheEnabled(true);
                        imageView.setDrawingCacheEnabled(true);

//                        String imgSaved = MediaStore.Images.Media.insertImage(
//                                getContentResolver(), drawView.getDrawingCache(),
//                                UUID.randomUUID().toString()+".png", "drawing");

//                        Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
//                                imageData.length, options);
//                        Bitmap mutableBitmap = myImage.copy(Bitmap.Config.ARGB_8888, true);

                        Bitmap mBitmap = imageView.getDrawingCache();
                        Paint paint = new Paint();
                        Canvas canvas = new Canvas(mBitmap);

                        drawView.setDrawingCacheEnabled(true);
                        Bitmap viewCapture = Bitmap.createBitmap(drawView.getDrawingCache());
                        drawView.setDrawingCacheEnabled(false);
                        canvas.drawBitmap(viewCapture, 0, 0, paint);

                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream("/storage/sdcard0/NotePic_OUT.jpeg");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);

                        /*if(imgSaved!=null) {
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else {
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }*/
                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;

            case R.id.action_refresh:
                // clear the canvas
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }




    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                //mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                //mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void openCamera() {
        mCamera = Camera.open();
        mPicture = getPictureCallback();
        //mPreview.refreshCamera(mCamera);
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                //mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "NotePic");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    class Preview extends SurfaceView implements SurfaceHolder.Callback {
        SurfaceHolder mHolder;
        Camera mCamera;

        Preview(Context context) {
            super(context);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
                // TODO: add more exception handling logic here
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }


        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.05;
            double targetRatio = (double) w / h;
            if (sizes == null) return null;

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            Log.d("NotePic", optimalSize.width + "x" + optimalSize.height);
            return optimalSize;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, w, h);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }
}
