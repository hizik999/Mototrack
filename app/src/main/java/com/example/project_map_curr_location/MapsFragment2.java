package com.example.project_map_curr_location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment2 extends Fragment implements GoogleMap.OnPolylineClickListener {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int request_Code = 101;
    private List<Motorcycle> motorcycleList = new ArrayList<>();
    private boolean animateCamera = false;
    private int pos;

    GoogleMap mMap;
    Context context;
    String lang;


    public MapsFragment2(ArrayList<Motorcycle> motorcycleList) {
        this.motorcycleList = motorcycleList;
    }

    private OnMapReadyCallback defaultCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            checkPerms(googleMap);
            googleMap.setTrafficEnabled(false);
            googleMap.setPadding(0, 900, 0, 900);
            googleMap.clear();
        }
    };


    private OnMapReadyCallback motoCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            checkPerms(googleMap);
            googleMap.setTrafficEnabled(false);
            googleMap.setPadding(0, 900, 0, 900);
            googleMap.clear();
            for (int i = 0; i < motorcycleList.size(); i++) {
                LatLng latLng1 = new LatLng(motorcycleList.get(i).getLongitude(), motorcycleList.get(i).getLatitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng1);
                markerOptions.title(motorcycleList.get(i).getName());
                googleMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_red_moped)));

            }
            LatLng start = new LatLng(28.5021359, 77.4054901);
            LatLng end = new LatLng(28.5151087, 77.3932163);
            //            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                    .clickable(true)
//                    .add(
//                            new LatLng(-35.016, 143.321),
//                            new LatLng(-34.747, 145.592),
//                            new LatLng(-34.364, 147.891),
//                            new LatLng(-33.501, 150.217),
//                            new LatLng(-32.306, 149.248),
//                            new LatLng(-32.491, 147.309)));


//            Route supportRoute = new Route();
////            LatLng source = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
////            LatLng dest= new LatLng(destLat, destLog);
//
//            supportRoute.drawRoute(googleMap, getActivity(), start, end, true, "en");


//            start = new LatLng(18.015365, -77.499382);
//            waypoint= new LatLng(18.01455, -77.499333);
//            end = new LatLng(18.012590, -77.500659);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(start);
            googleMap.addMarker(markerOptions);


//            Routing routing = new Routing.Builder()
//                    .travelMode(AbstractRouting.TravelMode.DRIVING)
//                    .waypoints(start, end)
//                    .key("AIzaSyDxC1i4d4ALU7FW7EiQV-rChpXBPuAUeF0")
//                    .build();
//            routing.execute();



//            buildRoute(googleMap, start, end);

        }
    };

    private OnMapReadyCallback zoomCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            checkPerms(googleMap);
            googleMap.setTrafficEnabled(false);
            googleMap.setPadding(0, 900, 0, 900);
            googleMap.clear();

            for (int i = 0; i < motorcycleList.size(); i++) {
                LatLng latLng1 = new LatLng(motorcycleList.get(i).getLongitude(), motorcycleList.get(i).getLatitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng1);
                markerOptions.title(motorcycleList.get(i).getName());
                googleMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_red_moped)));

            }

            zoomMap(googleMap);



        }
    };

//    private OnMapReadyCallback navigationCallback = new OnMapReadyCallback() {
//
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            checkPerms(googleMap);
//            googleMap.setTrafficEnabled(false);
//            googleMap.setPadding(0, 900, 0, 900);
//            googleMap.clear();
//
//            LatLng barcelona = new LatLng(41.385064, 2.173403);
//            googleMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Barcelona"));
//
//            LatLng madrid = new LatLng(40.416775, -3.70379);
//            googleMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));
//
//            LatLng zaragoza = new LatLng(41.648823, -0.889085);
//
//            //Define list to get all latlng for the route
//            List<LatLng> path = new ArrayList();
//
//
//            //Execute Directions API request
//            GeoApiContext context = new GeoApiContext.Builder()
//                    .apiKey(String.valueOf(R.string.google_map_key2))
//                    .build();
//
//            DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
//            try {
//                DirectionsResult res = req.await();
//
//                //Loop through legs and steps to get encoded polylines of each step
//                if (res.routes != null && res.routes.length > 0) {
//                    DirectionsRoute route = res.routes[0];
//
//                    if (route.legs != null) {
//                        for (int i = 0; i < route.legs.length; i++) {
//                            DirectionsLeg leg = route.legs[i];
//                            if (leg.steps != null) {
//                                for (int j = 0; j < leg.steps.length; j++) {
//                                    DirectionsStep step = leg.steps[j];
//                                    if (step.steps != null && step.steps.length > 0) {
//                                        for (int k = 0; k < step.steps.length; k++) {
//                                            DirectionsStep step1 = step.steps[k];
//                                            EncodedPolyline points1 = step1.polyline;
//                                            if (points1 != null) {
//                                                //Decode polyline and add points to list of route coordinates
//                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                                for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                    path.add(new LatLng(coord1.lat, coord1.lng));
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        EncodedPolyline points = step.polyline;
//                                        if (points != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                            for (com.google.maps.model.LatLng coord : coords) {
//                                                path.add(new LatLng(coord.lat, coord.lng));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception ex) {
//                Log.e("TAG", ex.getLocalizedMessage());
//            }
//
//            //Draw the polyline
//            if (path.size() > 0) {
//                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//                googleMap.addPolyline(opts);
//            }
//
//            googleMap.getUiSettings().setZoomControlsEnabled(true);
//
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));
//
//        }
//    };

    @NonNull
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void checkPerms(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_Code);
        }
        googleMap.setMyLocationEnabled(true);
    }

    public void zoomMap(GoogleMap googleMap) {
        Bundle arguments = getArguments();
        try {
            pos = arguments.getInt("position");
            LatLng latLng = new LatLng(motorcycleList.get(pos).getLongitude(), motorcycleList.get(pos).getLatitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        } catch (Exception e) {
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RVMotosAdapter rvMotosAdapter = new RVMotosAdapter(motorcycleList);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        Context context = view.getContext();


        if (mapFragment != null) {

            if (((MainActivity) context).loadDataBoolean("map_animation")) {
                mapFragment.getMapAsync(zoomCallback);
            } else if (((MainActivity) context).loadDataInt(getString(R.string.car_or_moto)) == 0) {
                mapFragment.getMapAsync(motoCallback);
            } else {
                mapFragment.getMapAsync(defaultCallback);
            }
            ((MainActivity) context).saveDataBoolean("map_animation", false);

        }


    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

//    private void buildRoute(GoogleMap map, LatLng start, LatLng end){
//
//
//        Routing routing = new Routing.Builder()
//                .travelMode(AbstractRouting.TravelMode.DRIVING)
//                .withListener()
//                .alternativeRoutes(true)
//                .waypoints(start, end)
//                .key("AIzaSyD4uStbluZBnwKADWRtCPalZoddDXdNQbs")  //also define your api key here.
//                .build();
//        routing.execute();
//
//    }


}