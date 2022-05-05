package com.example.project_map_curr_location;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.traffic.TrafficLayer;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class YandexMapFragment extends Fragment implements Session.SearchListener, CameraListener {

    private final ArrayList<Motorcycle> motorcycleList;

    private MapView mapView;

    private Point start = new Point();
    private Point end = new Point();
    private Point actualPosition = new Point();

    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    private UserLocationLayer userLocationLayer;

    private Context context;

    private TrafficLayer traffic;

    private EditText searchEdit;
    private SearchManager searchManager;
    private Session searchSession;



//    private TrafficLevel trafficLevel = null;
//    private enum TrafficFreshness {Loading, OK, Expired};
//    private TrafficFreshness trafficFreshness;
//    private Point start = new Point(55.6692509, 37.2849947);
//    private Point end = new Point(55.733330, 37.587649);

//    private Point screen_center = new Point(
//            (start.getLatitude() + end.getLatitude()) / 2,
//            (start.getLongitude() + end.getLongitude()) / 2);

    public YandexMapFragment(ArrayList<Motorcycle> motorcycleList) {
        this.motorcycleList = motorcycleList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(getString(R.string.yandex_api));
        MapKitFactory.initialize(getContext());
        SearchFactory.initialize(getContext());
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_yandex_map, null);

        

        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        double lat = ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat));
        double lon = ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon));
        mapView.getMap().move(new CameraPosition(
                new Point(lat, lon), 14, 0, 0));

        traffic = MapKitFactory.getInstance().createTrafficLayer(mapView.getMapWindow());
        traffic.setTrafficVisible(false);


        userLocation();

        if (((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
            traffic.setTrafficVisible(false);

            mapObjects.clear();
            String text = ((MainActivity) context).loadDataString(getString(R.string.findLocationEditText));
            submitQuery(text);

            if (((MainActivity) context).loadDataInt(getString(R.string.car_or_moto)) == 0){

                printMotos();
            }




        }

    }

    private void printMotos() {

        for (Motorcycle motorcycle: motorcycleList) {
            mapView.getMap().getMapObjects().addPlacemark(new Point(motorcycle.getLatitude(), motorcycle.getLongitude()),ImageProvider.fromResource(getContext(), R.drawable.motopng),
                    new IconStyle().setAnchor(new PointF(0.1f, 0.1f))
                            .setRotationType(RotationType.NO_ROTATION)
                            .setZIndex(0.5f)
                            .setScale(0.5f))
            ;
        }
    }

    private class MyThread extends Thread{

        @Override
        public void run() {
//            super.run();



        }
    }

    private void submitQuery(String query) {
        searchSession = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()), new SearchOptions(), this);
    }


    @Override
    public void onCameraPositionChanged(
            Map map,
            CameraPosition cameraPosition,
            CameraUpdateReason cameraUpdateReason,
            boolean finished) {
        if (finished) {
            submitQuery(searchEdit.getText().toString());
        }
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            int i = 0;
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();

            if (resultLocation != null) {

                Point startLoc = new Point(((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat)), ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon)));
                Point destLoc = new Point(resultLocation.getLatitude(), resultLocation.getLongitude());
                submitRequest(startLoc, destLoc);

            }
            i ++;
            if (i == 1){
                break;
            }
        }
    }

    private void userLocation(){
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        UserLocationObjectListener listener = new UserLocationObjectListener() {
            @Override
            public void onObjectAdded(@NonNull UserLocationView userLocationView) {

                userLocationLayer.setAnchor(
                        new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                        new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

                userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                        context, R.drawable.user_arrow));

//                CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

//                pinIcon.setIcon(
//                        "icon",
//                        ImageProvider.fromResource(context, R.drawable.icon),
//                        new IconStyle().setAnchor(new PointF(0f, 0f))
//                                .setRotationType(RotationType.ROTATE)
//                                .setZIndex(0f)
//                                .setScale(1f)
//                );

//                pinIcon.setIcon(
//                        "pin",
//                        ImageProvider.fromResource(context, R.drawable.search_result),
//                        new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
//                                .setRotationType(RotationType.ROTATE)
//                                .setZIndex(1f)
//                                .setScale(0.5f)
//                );

                userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
            }

            @Override
            public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
            }

            @Override
            public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
            }
        };

        userLocationLayer.setObjectListener(listener);
    }


    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = "ну ты и балда";
        if (error instanceof RemoteError) {
            errorMessage = "ну ты и балда";
        } else if (error instanceof NetworkError) {
            errorMessage = "ну ты и балда";
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onPause();
    }

//search, suggest, jams
    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();

        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();

        mapView.onStart();
    }

    private void submitRequest(Point tripStart, Point tripEnd) {

        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                tripStart,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                tripEnd,
                RequestPointType.WAYPOINT,
                null));
        DrivingSession.DrivingRouteListener drivingRouteListener = new DrivingSession.DrivingRouteListener() {
            @Override
            public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
                int i = 0;
                for (DrivingRoute route : list) {
                    mapObjects.addPlacemark(tripEnd).setIcon(ImageProvider.fromResource(context, R.drawable.search_result),
                            new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                                    .setRotationType(RotationType.ROTATE)
                                    .setZIndex(1f)
                                    .setScale(0.5f));
                    mapObjects.addPolyline(route.getGeometry());
                    i++;
                    if (i == 1){
                        break;
                    }
                }
            }

            @Override
            public void onDrivingRoutesError(@NonNull Error error) {
                String errorMessage = "ну ты и балда";
                if (error instanceof RemoteError) {
                    errorMessage = "ну ты и балда";
                } else if (error instanceof NetworkError) {
                    errorMessage = "ну ты и балда";
                }

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        };
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, drivingRouteListener);
    }


}