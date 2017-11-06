package net.caiyun.cvandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;



public class MainActivity extends AppCompatActivity {

    private ImageView ivImage;
    private ImageView ivImageProcessed;

    private static final int SELECT_PHOTO = 0;
    private static final String TAG = "OpenCV";

    Mat imageSource;

    static{
        System.loadLibrary("opencv_java");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImageProcessed = (ImageView) findViewById(R.id.ivImageProcessed);


//        Button buttonMean = (Button)findViewById(R.id.btn_mean);
//        buttonMean.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_load_image) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initOpenCV(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mOpenCVCallback);
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mOpenCVCallback);

    }


    private BaseLoaderCallback mOpenCVCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.d(TAG, "OpenCV load success");
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }

            }

        }
    };

//    public static boolean initOpenCV(String Version, final Context AppContext,
//                                     final LoaderCallbackInterface Callback) {
//        AsyncServiceHelper helper = new AsyncServiceHelper(Version, AppContext,
//                Callback);
//        Intent intent = new Intent("org.opencv.engine.BIND");
//        intent.setPackage("org.opencv.engine");
//        if (AppContext.bindService(intent, helper.mServiceConnection,
//                Context.BIND_AUTO_CREATE)) {
//            return true;
//        } else {
//            AppContext.unbindService(helper.mServiceConnection);
//            InstallService(AppContext, Callback);
//            return false;
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap image = BitmapFactory.decodeStream(imageStream);

                        imageSource = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
                        Utils.bitmapToMat(image, imageSource);

//                        Imgproc.blur(imageSource, imageSource, new Size(3, 3));
//                        Imgproc.GaussianBlur(imageSource, imageSource, new Size(3,3), 0);
//                        Imgproc.medianBlur(imageSource, imageSource, 3);
                        Imgproc.cvtColor(imageSource, imageSource, Imgproc.COLOR_BGR2GRAY);
                        Imgproc.Canny(imageSource, imageSource, 10, 100);

                        Bitmap processedImage = Bitmap.createBitmap(imageSource.cols(),
                                imageSource.rows(), Bitmap.Config.ARGB_8888);

                        Utils.matToBitmap(imageSource, processedImage);

                        ivImage.setImageBitmap(image);
                        ivImageProcessed.setImageBitmap(processedImage);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    }
}
