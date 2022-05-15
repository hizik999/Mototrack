package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.project_map_curr_location.database.DataBaseHelper;
import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;
import com.example.project_map_curr_location.domain.Moto1;
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
import com.yandex.mapkit.map.PlacemarkMapObject;
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

    private final List<Moto1> motorcycleList;

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

    private DataBaseHelper dataBaseHelper;

    private boolean thread;
    private MyThread123 myThread;
    private Handler handler = new Handler(Looper.getMainLooper());

    private PlacemarkMapObject placemarkMapObject;
    private AppCompatButton btnLocation;

//    private TrafficLevel trafficLevel = null;
//    private enum TrafficFreshness {Loading, OK, Expired};
//    private TrafficFreshness trafficFreshness;
//    private Point start = new Point(55.6692509, 37.2849947);
//    private Point end = new Point(55.733330, 37.587649);

//    private Point screen_center = new Point(
//            (start.getLatitude() + end.getLatitude()) / 2,
//            (start.getLongitude() + end.getLongitude()) / 2);

    public YandexMapFragment(List<Moto1> motorcycleList) {
        this.motorcycleList = motorcycleList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(getString(R.string.yandex_api));
        MapKitFactory.initialize(getContext());
        SearchFactory.initialize(getContext());
        super.onCreate(savedInstanceState);
        context = getContext();
        thread = true;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_yandex_map, null);
//        Toast.makeText(context, "OnCreateView", Toast.LENGTH_SHORT).show();
        mapView = mainView.findViewById(R.id.mapView);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        traffic = MapKitFactory.getInstance().createTrafficLayer(mapView.getMapWindow());
        traffic.setTrafficVisible(false);
        double lat = ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat));
        double lon = ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon));
        mapView.getMap().move(new CameraPosition(
                new Point(lat, lon), 14, 0, 0));

        userLocation();

        btnLocation = mainView.findViewById(R.id.btnLocation);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat));
                double lon = ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon));
                mapView.getMap().move(new CameraPosition(
                        new Point(lat, lon), 14, 0, 0));
            }
        });

        traffic.setTrafficVisible(false);

        if (((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
            traffic.setTrafficVisible(true);

            mapObjects.clear();
            String text = ((MainActivity) context).loadDataString(getString(R.string.findLocationEditText));
            submitQuery(text);

            if (((MainActivity) context).loadDataInt(getString(R.string.car_or_moto)) == 0) {

                myThread = new MyThread123();
                thread = true;
                myThread.setDaemon(true);
                myThread.start();
            }


        }




        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
//        Toast.makeText(context, "OnViewCreated", Toast.LENGTH_SHORT).show();


    }

    private List<PlacemarkMapObject> printMotos() {
        dataBaseHelper = new DataBaseHelper(getContext());

        List<Moto1> list = dataBaseHelper.getAllMoto();

        List<PlacemarkMapObject> placemarkMapObjects = new ArrayList<>();

        for (Moto1 moto : list) {

            Point point = new Point(moto.getLatitude(), moto.getLongitude());

            try {
                PlacemarkMapObject placemarkMapObject = mapObjects.addPlacemark(point, ImageProvider.fromResource(getContext(), R.drawable.motopng),
                        new IconStyle().setAnchor(new PointF(0.1f, 0.1f))
                                .setRotationType(RotationType.NO_ROTATION)
                                .setZIndex(0.5f)
                                .setScale(0.5f));
                placemarkMapObjects.add(placemarkMapObject);
            } catch (Exception e) {
                Log.d("DELETE_PLACEMARK", e.getMessage());
            }

//            try {
//                mapObjects.remove(placemarkMapObject);
//            } catch (Exception e) {
//                Log.d("DELETE_PLACEMARK", e.getMessage());
//            }
        }
        return placemarkMapObjects;
    }

    private void deleteMotos(List<PlacemarkMapObject> placemarkMapObjects) {

        for (PlacemarkMapObject marker : placemarkMapObjects) {
            marker.setVisible(false);
        }
    }

//    private void printMotosAAA() {
//
//        dataBaseHelper = new DataBaseHelper(getContext());
//
//        List<Moto1> list = dataBaseHelper.getAllMoto();
//
//        for (Moto1 moto : motorcycleList) {
//
//            Point point = new Point(moto.getLatitude(), moto.getLongitude());
//
//            mapObjects.
//        }
//
//    }

    private class MyThread123 extends Thread {

        private List<PlacemarkMapObject> placemarkMapObjects123;

        @Override
        public void run() {
            //super.run();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    placemarkMapObjects123 = printMotos();

                }
            };
            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    deleteMotos(placemarkMapObjects123);
                }
            };
            while (thread) {
                try {

                    handler.post(runnable);
                    sleep(4 * 1000);
                    handler.post(runnable1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


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
            i++;
            if (i == 1) {
                break;
            }
        }
    }

    private void userLocation() {
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        UserLocationObjectListener listener = new UserLocationObjectListener() {
            @Override
            public void onObjectAdded(@NonNull UserLocationView userLocationView) {
                //printMotos();
                userLocationLayer.setAnchor(
                        new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                        new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

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
        thread = false;
        super.onPause();

        if (myThread != null) {
            Thread dummy = myThread;
            myThread = null;
            dummy.interrupt();
        }
    }

    //search, suggest, jams
    @Override
    public void onStop() {
        mapView.onStop();
        thread = false;
        MapKitFactory.getInstance().onStop();

        super.onStop();

        if (myThread != null) {
            Thread dummy = myThread;
            myThread = null;
            dummy.interrupt();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        thread = true;
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
                    if (i == 1) {
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
        //printMotos();
    }


}