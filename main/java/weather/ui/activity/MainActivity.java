package ezike.tobenna.myweather.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import ezike.tobenna.myweather.R;
import ezike.tobenna.myweather.databinding.ActivityMainBinding;
import ezike.tobenna.myweather.utils.LocationHandler;
import ezike.tobenna.myweather.utils.Utilities;
import timber.log.Timber;

/**
 * @author tobennaezike
 * @since 16/03/19
 */
public class MainActivity extends AppCompatActivity  {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 98;

    @Inject
    LocationManager mLocationManager;

    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
        }
    };

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(binding.bottomNav, mNavController);

        checkLocationPermission();

        checkGpsEnabled();

        MobileAds.initialize(this, getString(R.string.ad_id));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, (DrawerLayout) null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean navigated = NavigationUI.onNavDestinationSelected(item, mNavController);
        return navigated || super.onOptionsItemSelected(item);
    }

    private void checkGpsEnabled() {
        if (Utilities.isLocationProviderEnabled(mLocationManager)) {
            Timber.d("gps enabled");
            startLocationUpdates();
        } else {
            Timber.d("gps disabled");
            Utilities.enableLocationProvider(this, getString(R.string.enable_gps),
                    getString(R.string.gps_enable_prompt));
        }
    }

    private void startLocationUpdates() {
        LocationHandler.getLocationHandler(this, mLocationCallback);
    }

    public void checkLocationPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                !isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Utilities.showDialog(this, getString(R.string.location_permission_dialog_title),
                        getString(R.string.location_permission_prompt),
                        (dialog, i) -> requestPermission(permissions),
                        (dialog, i) -> Utilities.showToast(this,
                                getString(R.string.set_custom_location),
                                Toast.LENGTH_LONG));
            } else {
                requestPermission(permissions);
            }
        } else {
            Timber.d("Permission granted");
            startLocationUpdates();
        }
    }

    private void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_ACCESS_COARSE_LOCATION);
    }

    private boolean isPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                    Timber.d("permission granted");
                } else {
                    Timber.d("permission not granted");
                }
            }
        }
    }
}
