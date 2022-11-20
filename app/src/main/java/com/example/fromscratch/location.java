package com.example.fromscratch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
// classes needed to add a marker
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import com.mapbox.mapboxsdk.location.LocationComponent;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class location extends Fragment implements OnMapReadyCallback, PermissionsListener {

    private MapboxMap mapboxMap;
    private MapView mapView;
    private FragmentActivity myContext;
    private Fragment mMyFragment;
    private FirebaseDatabase rootNode;
    private DatabaseReference referenceMarchand;
    private DatabaseReference referenceProduit;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private FirebaseRecyclerOptions<produitPostsHelperClass> opt;
    private FirebaseRecyclerAdapter<produitPostsHelperClass, produitViewHolder> adapt;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    FusedLocationProviderClient mFusedLocationClient;

    double latitude;
    double longitude;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            String lat = String.valueOf(savedInstanceState.getDouble("lat"));
            Toast.makeText(getContext(), "Latitude is :" + lat, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootNode = FirebaseDatabase.getInstance();
        referenceMarchand = rootNode.getReference("Marchand");
        referenceProduit = rootNode.getReference("Produits");
        Mapbox.getInstance(Objects.requireNonNull(getContext()), "pk.eyJ1IjoidGFodG91aGEiLCJhIjoiY2sxNnRicjN4MHBhZDNqbjFmdnkwN2RhciJ9.Sz7M8JgUxFxwz9ATEpbApQ");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location, container, false);

        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");

        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {


                //final LocationComponent locationComponent = mapboxMap.getLocationComponent();

                if (latitude > 0.0 || latitude < 0.0) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(
                            new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    final Location location = task.getResult();
                                    if (location == null) {
                                        requestNewLocationData();
                                    } else {
                                        Toast.makeText(getContext(), String.valueOf(latitude) + String.valueOf(longitude), Toast.LENGTH_LONG).show();
                                        Point destinationPoint = Point.fromLngLat(longitude,latitude);
                                        Point originPoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                                        getRoute(originPoint, destinationPoint);
                                        //Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    );
                }

                referenceMarchand.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                          /*  Point destinationPoint = Point.fromLngLat(user.getLongitude(), user.getLatitude());
                            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                    locationComponent.getLastKnownLocation().getLatitude());*/
                            final MarchandHelperClass user = snapshot.getValue(MarchandHelperClass.class);
                            Query marchandsActif = referenceProduit.child(user.getUsername());
                            marchandsActif.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists())
                                    {

                                        mapboxMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(user.getLatitude(), user.getLongitude()))
                                                .title(user.getUsername()));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                       /* mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(35.7096487,-0.60820967)));*/


                        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull final Marker marker) {

                                final Dialog dialog = new Dialog(getContext());
                                dialog.setContentView(R.layout.pop_up_marchand);
                                final TextView name = dialog.findViewById(R.id.nom_marchand);
                                final RecyclerView produitsList = dialog.findViewById(R.id.listeProduits);
                                final Button showRoad = dialog.findViewById(R.id.showRoad);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                                produitsList.setLayoutManager(layoutManager);
                                produitsList.setHasFixedSize(true);
                                //final TextView adress = dialog.findViewById(R.id.adress);

                                final ArrayList<produitPostsHelperClass> produitList = new ArrayList<>();

                                referenceMarchand.child(marker.getTitle()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        MarchandHelperClass helper = dataSnapshot.getValue(MarchandHelperClass.class);
                                        if(dataSnapshot != null)
                                        {
                                            name.setText(helper.getUsername());
                                        }
                                       // phone.setText(helper.getTelephone());

                                        opt = new FirebaseRecyclerOptions.Builder<produitPostsHelperClass>().setQuery(referenceProduit.child(marker.getTitle()),produitPostsHelperClass.class).build();
                                        adapt = new FirebaseRecyclerAdapter<produitPostsHelperClass, produitViewHolder>(opt) {


                                            @Override
                                            protected void onBindViewHolder(@NonNull produitViewHolder holder, int position, @NonNull produitPostsHelperClass model) {
                                                holder.type.setText(model.getType());
                                                holder.quantity.setText(model.getQuant());
                                                holder.marqueProd.setText(model.getMarque());
                                                holder.joursRestants.setText(model.getJour());
                                                holder.prixProd.setText(model.getPrix()+getString(R.string.money));
                                            }

                                            @NonNull
                                            @Override
                                            public produitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produit_list_pop_up,parent,false);
                                                return new produitViewHolder(view);
                                            }
                                        };

                                        adapt.startListening();
                                        produitsList.setAdapter(adapt);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                showRoad.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //all this in button
                                        if (checkPermissions()) {
                                            if (isLocationEnabled()) {
                                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                    // TODO: Consider calling
                                                    //    ActivityCompat#requestPermissions
                                                    // here to request the missing permissions, and then overriding
                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    //                                          int[] grantResults)
                                                    // to handle the case where the user grants the permission. See the documentation
                                                    // for ActivityCompat#requestPermissions for more details.
                                                    return;
                                                }
                                                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                                                        new OnCompleteListener<Location>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Location> task) {
                                                                final Location location = task.getResult();
                                                                if (location == null) {
                                                                    requestNewLocationData();
                                                                } else {
                                                                    Point destinationPoint = Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                                                                    Point originPoint = Point.fromLngLat(location.getLongitude(),location.getLatitude());
                                                                    getRoute(originPoint,destinationPoint);
                                                                    //Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                        }
                                                );
                                            } else {
                                                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);
                                            }
                                        } else {
                                            requestPermissions();
                                        }
                                    }
                                });


                                dialog.setTitle("Title...");

                                // set the custom dialog components - text, image and button
                                dialog.show();
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                location.this.mapboxMap = mapboxMap;


                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
// Map is set up and the style has loaded. Now you can add data or make other map adjustments.


                    }
                });


            }
        });


    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(getContext())
                .accessToken("pk.eyJ1IjoidGFodG91aGEiLCJhIjoiY2sxNnRicjN4MHBhZDNqbjFmdnkwN2RhciJ9.Sz7M8JgUxFxwz9ATEpbApQ")
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        double currentLat = 1.33;
        outState.putDouble("lat", currentLat);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Toast.makeText(getContext(), "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            //Toast.makeText(getContext(),"user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            myContext.finish();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            /*latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");*/
            Toast.makeText(getContext(),"CALL BACK: "+ mLastLocation.getLatitude() + " + " + mLastLocation.getLongitude() ,Toast.LENGTH_LONG).show();
        }
    };

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(getActivity()
                ,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},44);
    }

}

