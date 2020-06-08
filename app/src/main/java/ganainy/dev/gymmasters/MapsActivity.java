package ganainy.dev.gymmasters;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import ganainy.dev.gymmasters.R;

import ganainy.dev.gymmasters.places_model.Candidate;
import ganainy.dev.gymmasters.places_model.Candidates;
import ganainy.dev.gymmasters.places_model.Geometry;
import ganainy.dev.gymmasters.places_model.PlacesApi;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private final float DEFAULT_ZOOM = 18f;
    @BindView(R.id.searchBar)
    MaterialSearchBar materialSearchBar;
    @BindView(R.id.imageView9)
    ImageView markerImage;
    @BindView(R.id.textViewName)
    TextView textViewName;
    @BindView(R.id.textView8)
    TextView textViewAddress;
    @BindView(R.id.textView12)
    TextView textViewOpeningHours;
    @BindView(R.id.textViewRate)
    TextView textViewRate;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient mPlacesClient;
    private List<AutocompletePrediction> placesPredictionList;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    private View mapView;
    private RippleBackground rippleBackground;
    private ConstraintLayout constraintLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //hide status bar sdk<16
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        View decorView = getWindow().getDecorView();
        // Hide the status bar sdk>16
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        //init bottomsheet
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //show map fragment on activity
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mapView = mMapFragment.getView();


        //initialize mFusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //initialize places sdk
        Places.initialize(this, getString(R.string.google_places_key)
        );
        mPlacesClient = Places.createClient(this);
        final AutocompleteSessionToken autocompleteSessionToken = AutocompleteSessionToken.newInstance();


        //material search bar
        materialSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialSearchBar.enableSearch();
            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                    materialSearchBar.clearSuggestions();
                }
            }
        });


        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: " + editable);
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setCountry(getCountry())
                        .setSessionToken(autocompleteSessionToken)
                        .setQuery(editable.toString())
                        .build();
                mPlacesClient.findAutocompletePredictions(predictionsRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: " + e.getMessage());
                    }
                });

            }
        });


        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                /**we only have place id (from  FindAutocompletePredictionsRequest) and we need latlng to be able to show place on map using places API*/
                if (position >= placesPredictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = placesPredictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);


                //wait one second before using clearSuggestions for it to work properly
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, (1000));


                //close soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                String place_id = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);//specify which field we are interested in
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(place_id, placeFields).build();
                mPlacesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        LatLng latLng = fetchPlaceResponse.getPlace().getLatLng(); //note anything other than latlng will be null since i specified in the request that i only need latlng
                        if (latLng != null) {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                                    (new LatLng(latLng.latitude, latLng.longitude), DEFAULT_ZOOM));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: " + e.getMessage());
                        if (e instanceof ApiException)
                            Log.i(TAG, "onFailure: status code: " + ((ApiException) e).getStatusCode());
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
    }

    private String getCountry() {
        /**will return null if device dont have sim card*/
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        return countryCode;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        /**show MyLocationButton on map*/
        mGoogleMap.setMyLocationEnabled(true);
        // mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        /**changing location of MyLocationButton because it was top right under searchview*/
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);//false
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);//true
            layoutParams.setMargins(0, 0, 16, 8);
        }


        /**check if gps is enabled and request it if not enabled*/
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                /**it means location is enabled on device*/
                Log.i(TAG, "onSuccess: ");
                getDeviceLocation();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: e instanceof ResolvableApiException" + (e instanceof ResolvableApiException));
                /**location is disabled*/
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        /**this will show dialogue asking user to enable location (GPS) and then onActivityResult will be called*/
                        resolvableApiException.startResolutionForResult(MapsActivity.this, 101);
                    } catch (IntentSender.SendIntentException ex) {
                        Log.i(TAG, "onFailure: " + ex.getMessage());
                    }
                }
            }
        });


        /**reset search view when i click my location button*/
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible()) materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled()) materialSearchBar.disableSearch();
                return false;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Log.i(TAG, "onActivityResult: " + resultCode);
            if (resultCode == RESULT_OK) {
                /**user enabled location*/
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Can't get your location , Please enable GPS", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getDeviceLocation() {

        /**get last known location*/

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastKnownLocation = location;
                if (mLastKnownLocation != null) {
                    /**successful move camera to result location*/
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                } else {
                    /**last known location not found*/
                    final LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if (locationRequest != null) {
                                mLastKnownLocation = locationResult.getLastLocation();
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback); //to get location only once and remove updates
                            }
                        }
                    };

                    mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this, "Error getting location ", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: " + e.getMessage());
            }
        });
    }


    @OnClick(R.id.button2)
    public void handleFindGymClick() {
        findNearbyGyms();
    }

    private void findNearbyGyms() {
        LatLng latLng = mGoogleMap.getCameraPosition().target;
        Log.i(TAG, "findNearbyGyms: " + latLng.latitude + " , " + latLng.longitude);
        rippleBackground = findViewById(R.id.rippleBackground);
        rippleBackground.startRippleAnimation();


        String mBaseUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();

        PlacesApi placesApi = retrofit.create(PlacesApi.class);


        /**call places api to find gyms in radius of 5km from my location*/
        Call<Candidates> call = placesApi.getParentObject("gym", "textquery",
                "geometry/location,formatted_address,name,opening_hours,rating", "circle:5000@" + latLng.latitude + "," + latLng.longitude, getString(R.string.google_places_key)
        );
        call.enqueue(new Callback<Candidates>() {
            @Override
            public void onResponse(Call<Candidates> call, Response<Candidates> response) {


                try {
                Candidates body = response.body();
                Candidate candidate = body.getCandidates().get(0);
                Geometry geometry = candidate.getGeometry();
                Double lat = geometry.getLocation().getLat();
                Double lng = geometry.getLocation().getLng();
                String formatted_address = candidate.getFormattedAddress();
                String name = candidate.getName();
                Boolean openNow = candidate.getOpeningHours().getOpenNow();
                Double rating = candidate.getRating();


                rippleBackground.stopRippleAnimation();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom
                        (new LatLng(lat, lng), DEFAULT_ZOOM));


                /**show info card*/

                textViewAddress.setText(formatted_address);
                textViewName.setText(name);
                textViewOpeningHours.setText(openNow ? "Open right now" : "Closed right now");
                textViewRate.setText(rating + "/5");

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } catch (Exception e) {
                    rippleBackground.stopRippleAnimation();
                    Toast.makeText(MapsActivity.this, "Sorry , couldn't find any nearby gyms", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Exception: " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<Candidates> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
                rippleBackground.stopRippleAnimation();
                Toast.makeText(MapsActivity.this, "Sorry , couldn't find any nearby gyms", Toast.LENGTH_LONG).show();

            }
        });

    }


    @OnClick(R.id.closeImage)
    public void onViewClicked() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
