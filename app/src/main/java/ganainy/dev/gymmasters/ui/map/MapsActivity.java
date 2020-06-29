package ganainy.dev.gymmasters.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Gym;
import ganainy.dev.gymmasters.utils.SharedPrefUtils;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static ganainy.dev.gymmasters.utils.SharedPrefUtils.IS_FIRST_SHOWING_MAP;

public class MapsActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks, OnMapReadyCallback {

    private static final int RC_LOCATION = 1;
    public static final int REQUEST_GPS_CODE = 101;
    private final float DEFAULT_ZOOM = 18f;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private MapViewModel mViewModel;

    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient mPlacesClient;
    private List<AutocompletePrediction> placesPredictionList;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    private View mapView;
    private BottomSheetBehavior mBottomSheetBehavior;

    @BindView(R.id.rippleBackground)
     RippleBackground rippleBackground;

    @BindView(R.id.searchBar)
    MaterialSearchBar materialSearchBar;

    @BindView(R.id.imageView9)
    ImageView markerImage;

    @BindView(R.id.nameShimmer)
    TextView textViewName;

    @BindView(R.id.textView8)
    TextView textViewAddress;

    @BindView(R.id.textView12)
    TextView textViewOpeningHours;

    @BindView(R.id.textViewRate)
    TextView textViewRate;

    @BindView(R.id.bottom_sheet)
    View bottomSheet;

    @BindView(R.id.hint_layout)
    ConstraintLayout hintLayout;

    @OnClick(R.id.button_find_nearby_gym)
    public void onFindNearbyGymClick() {
        mViewModel.findNearbyGyms(
                getString(R.string.google_places_key),mGoogleMap.getCameraPosition().target
        );
    }

    @OnClick(R.id.closeImage)
    public void onViewClicked() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.button_hint)
    public void closeHint() {
        hintLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        checkShowHint();
        enableFullScreen();
        initMaterialSearchBar();
        initViewModel();
        initPlacesApi();
        initBottomSheet();
        showMapFragment();
        checkLocationPermission();

    }

    /**only show hint about the map activity if this is first time*/
    private void checkShowHint() {
        if (SharedPrefUtils.getBoolean(this,IS_FIRST_SHOWING_MAP)){
            //this is first time
            hintLayout.setVisibility(View.VISIBLE);
            SharedPrefUtils.putBoolean(this,false,IS_FIRST_SHOWING_MAP);
        }
    }

    private void initBottomSheet() {
        //init bottom sheet
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    private void showMapFragment() {
        //show map fragment on activity
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
            mapView = mMapFragment.getView();
        }
    }

    private void initPlacesApi() {
        //initialize places sdk
        Places.initialize(this, getString(R.string.google_places_key)
        );

        mPlacesClient = Places.createClient(this);
    }

    private void initViewModel() {
        mViewModel =new ViewModelProvider(this).get(MapViewModel.class);

        mViewModel.getGymInfoLiveData().observe(this, gymEvent -> {
            Gym gym = gymEvent.getContentIfNotHandled();
            if (gym!=null){
                textViewRate.setText(getString(R.string.gym_rate,String.format("%.1f", gym.getRate())));
                textViewOpeningHours.setText(gym.getOpeningHours());
                textViewAddress.setText(gym.getAddress());
                textViewName.setText(gym.getName());
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        mViewModel.getLocationLiveData().observe(this, locationEvent -> {
            LatLng location = locationEvent.getContentIfNotHandled();
            if (location!=null){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom
                        (new LatLng(location.latitude, location.longitude), DEFAULT_ZOOM));
            }
        });

        mViewModel.getToastLiveData().observe(this, toastEvent -> {
            String toast = toastEvent.getContentIfNotHandled();
            if (toast!=null){
                Toast.makeText(this,toast,Toast.LENGTH_LONG).show();
            }
        });

        mViewModel.getRippleLoadingLiveData().observe(this,showRipple->{
            if (showRipple){
                rippleBackground.startRippleAnimation();
            }else {
                rippleBackground.stopRippleAnimation();
            }
        });
    }

    /**get device country or return null if device don't have sim card*/
    private String getCountry() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimCountryIso();
    }

    /**hide status bar*/
    private void enableFullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initMaterialSearchBar() {

        materialSearchBar.setOnClickListener(view -> materialSearchBar.enableSearch());

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                findPlaceByName(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                /*handle back button of material search bar*/
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                    materialSearchBar.clearSuggestions();
                }
            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                /*we only have place id (from  FindAutocompletePredictionsRequest) and we need lat lng to be able to show place on map using places API*/
                if (position >= placesPredictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = placesPredictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);


                //wait one second before using clearSuggestions for it to work properly
                new Handler().postDelayed(() -> materialSearchBar.clearSuggestions(), (1000));


                //close soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                String place_id = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);//specify which field we are interested in
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(place_id, placeFields).build();
                mPlacesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                    LatLng latLng = fetchPlaceResponse.getPlace().getLatLng();
                    //note anything other than lat lng will be null since i specified in the request that i only need latlng
                    if (latLng != null) {
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                                (new LatLng(latLng.latitude, latLng.longitude), DEFAULT_ZOOM));
                    }
                }).addOnFailureListener(e -> {
                    Log.i(TAG, "onFailure: " + e.getMessage());
                    if (e instanceof ApiException)
                        Log.i(TAG, "onFailure: status code: " + ((ApiException) e).getStatusCode());
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
    }

    private void findPlaceByName(String placeName) {
        AutocompleteSessionToken autocompleteSessionToken = AutocompleteSessionToken.newInstance();
        Log.i(TAG, "afterTextChanged: " + placeName);
        FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountry(getCountry())
                .setSessionToken(autocompleteSessionToken)
                .setQuery(placeName)
                .build();
        mPlacesClient.findAutocompletePredictions(predictionsRequest)
                .addOnSuccessListener(findAutocompletePredictionsResponse -> {
            if (findAutocompletePredictionsResponse != null) {
                placesPredictionList = findAutocompletePredictionsResponse.getAutocompletePredictions();
                List<String> placesPredictionStringList = new ArrayList<>();
                for (int i = 0; i < placesPredictionList.size(); i++) {
                    String place = placesPredictionList.get(i).getFullText(null).toString();
                    placesPredictionStringList.add(place);
                }
                materialSearchBar.updateLastSuggestions(placesPredictionStringList);
                if (!materialSearchBar.isSuggestionsVisible()) {
                    materialSearchBar.showSuggestionsList();
                }
            }
        }).addOnFailureListener(e ->
                Log.i(TAG, "onFailure: " + e.getMessage()));
    }

    private void checkLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            Log.d(TAG, "methodRequiresPermission: " + getString(R.string.all_good_get_location));
            getDeviceLocation();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.location_rationale),
                    RC_LOCATION, perms);
            Log.d(TAG, "methodRequiresTwoPermission: " + getString(R.string.req_perm));
        }
    }

    /**try to get last known location and if it fails get current location*/
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                mLastKnownLocation = location;
                if (mLastKnownLocation != null) {
                    /*last known location found move camera on it*/
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                } else {
                    /*last known location not found*/
                    final LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            mLastKnownLocation = locationResult.getLastLocation();
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback); //to get location only once and remove updates
                        }
                    };


                    mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);

                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MapsActivity.this, "Error getting location ", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onFailure: " + e.getMessage());
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);


        /*Move my location button from under searchView*/
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);//false
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);//true
            layoutParams.setMargins(0, 0, 16, 8);
        }


        /*check if gps is enabled and request it if not enabled*/
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            /*GPS is enabled on device*/
            Log.i(TAG, "onSuccess: ");
            getDeviceLocation();
        }).addOnFailureListener(e -> {
            Log.i(TAG, "onFailure: e instanceof ResolvableApiException" + (e instanceof ResolvableApiException));
            /*GPS is disabled*/
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                try {
                    /*ask user to enable GPS and then onActivityResult will be called*/
                    resolvableApiException.startResolutionForResult(MapsActivity.this, REQUEST_GPS_CODE);
                } catch (IntentSender.SendIntentException ex) {
                    Log.i(TAG, "onFailure: " + ex.getMessage());
                }
            }
        });


        /*on my location button hide search bar and close its places suggestions*/
        mGoogleMap.setOnMyLocationButtonClickListener(() -> {
            if (materialSearchBar.isSuggestionsVisible()) materialSearchBar.clearSuggestions();
            if (materialSearchBar.isSearchEnabled()) materialSearchBar.disableSearch();
            return false;
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            checkLocationPermission();
        }
        if (requestCode == REQUEST_GPS_CODE) {
            if (resultCode == RESULT_OK) {
                /*user enabled GPS*/
                getDeviceLocation();
            } else {
                Toast.makeText(this, R.string.please_enable_gps, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: "+getString(R.string.perm_granted));
        getDeviceLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied: "+getString(R.string.perm_denied));
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
            Log.d(TAG, "onPermissionsDenied: "+getString(R.string.perm_permanently_denied_go_to_settings));
        }else {
            Toast.makeText(this, R.string.cant_get_location_search_instead,Toast.LENGTH_LONG).show();
        }
    }
}
