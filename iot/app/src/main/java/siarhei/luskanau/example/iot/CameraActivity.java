package siarhei.luskanau.example.iot;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;

import com.google.gson.Gson;

import timber.log.Timber;

public abstract class CameraActivity extends Activity {

    private static final int PERMISSIONS_REQUEST = 200;
    private final Gson GSON = new Gson();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("Starting CameraActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionsGranted()) {
            cameraInfo();
        } else {
            requestPermissions();
        }
    }

    public boolean isPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        Timber.d("requestPermissions");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted();
                } else {
                    onPermissionsNotGranted();
                }
                break;

            default:
        }
    }

    protected void onPermissionsGranted() {
        Timber.d("onPermissionsGranted");
        cameraInfo();
    }

    protected void onPermissionsNotGranted() {
        Timber.d("onPermissionsNotGranted");
        finish();
    }

    private void cameraInfo() {
        try {
            final CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            final String[] cameraIdList = cameraManager.getCameraIdList();
            Timber.i("CameraIdList: " + GSON.toJson(cameraIdList));

            for (final String cameraId : cameraIdList) {
                Timber.d("Using camera id " + cameraId);
                try {
                    final CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    final StreamConfigurationMap configs = characteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (configs != null) {
                        final int[] outputFormats = configs.getOutputFormats();
                        Timber.i("OutputFormats: " + GSON.toJson(outputFormats));
                        for (final int format : outputFormats) {
                            Timber.d("Getting sizes for format: " + format);
                            for (final Size s : configs.getOutputSizes(format)) {
                                Timber.d("\t" + s);
                            }
                        }
                        final int[] effects = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
                        Timber.i("effects: " + GSON.toJson(effects));
                    }
                } catch (final CameraAccessException e) {
                    Timber.d("Cam access exception getting characteristics.");
                }
            }
        } catch (final CameraAccessException e) {
            Timber.d("Camera access exception getting IDs");
        }
    }
}
