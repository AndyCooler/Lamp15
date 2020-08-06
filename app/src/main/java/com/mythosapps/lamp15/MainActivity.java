package com.mythosapps.lamp15;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean isFlashlightOn;
    private Camera mCamera;

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
                    Snackbar.make(view, "Your camera has no flashlight!", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (isFlashlightOn) {
                    isFlashlightOn = false;
                    turnOff(view);
                    Snackbar.make(view, "Lampe aus", Snackbar.LENGTH_LONG).show();
                } else {
                    isFlashlightOn = true;
                    turnOn(view);
                    Snackbar.make(view, "Höhlenlampenforschung", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        // ask for permission..
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // don't stop!! releaseCameraAndPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCameraAndPreview();
    }

    private void turnOn(View view) {
        if (mCamera == null) {
            try {
                releaseCameraAndPreview();
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (Exception e) {
                Snackbar.make(view, "Failed to open camera: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                Log.e(getString(R.string.app_name), "failed to open Camera", e);
            }
        }

        if (mCamera != null) {
            final Camera.Parameters params = mCamera.getParameters();
            List<String> flashModes = params.getSupportedFlashModes();

            if (flashModes == null) {
                return;
            } else {
                try {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);
                    mCamera.setPreviewTexture(new SurfaceTexture(0));
                    mCamera.startPreview();
                } catch (Exception e) {
                    Snackbar.make(view, "Failed to start flashlight: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    Log.e(getString(R.string.app_name), "failed to start Flashlight", e);
                }
            }
        }
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void turnOff(View view) {
        releaseCameraAndPreview();
    }

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
