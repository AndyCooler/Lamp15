package com.mythosapps.lamp15;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean isFlashlightOn;
    private Camera mCamera;
    private int camId = 0; // back camera = 0, front camera = 1
    ImageView cameraPreview;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(getString(R.string.app_name), "Hey this is Lamp15!");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasFlashlightFeature()) {
                    Snackbar.make(view, "Has flashlight: none", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (isFlashlightOn) {
                    isFlashlightOn = false;
                    turnOff(view);
                } else {
                    isFlashlightOn = true;
                    turnOn(view);
                }

            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    private void turnOn(View view) {
        if (mCamera == null) {
            try {
                releaseCameraAndPreview();
                /*if (camId == 0) {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }
                else {
                */
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                //}
            } catch (Exception e) {
                Snackbar.make(view, "Failed to open camera: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                Log.e(getString(R.string.app_name), "failed to open Camera");
                e.printStackTrace();
            }
        }

        if (mCamera != null) {

            final Camera.Parameters params = mCamera.getParameters();

            List<String> flashModes = params.getSupportedFlashModes();

            if (flashModes == null) {
                return;
            } else {
                if (count == 0) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    mCamera.startPreview();
                }

                String flashMode = params.getFlashMode();

                if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {

                    if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(params);
                    } else {
                        Snackbar.make(view, "Has flash mode torch: none", Snackbar.LENGTH_LONG).show();

                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        mCamera.setParameters(params);
                        try {
                            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                                public void onAutoFocus(boolean success, Camera camera) {
                                    count = 1;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void releaseCameraAndPreview() {
        cameraPreview = null;
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void turnOff(View view) {

            count = 0;
            releaseCameraAndPreview();

    }

    /*
        @Override
        protected void onResume() {
            super.onResume();

            FloatingActionButton fab = findViewById(R.id.fab);


        }
    */
    private boolean hasFlashlightFeature() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
