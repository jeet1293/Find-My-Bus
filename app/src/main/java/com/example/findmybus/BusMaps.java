package com.example.findmybus;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusMaps extends AppCompatActivity implements OnMapReadyCallback {

    LatLng FROM,TO,WAY1,WAY2;
    GoogleMap googleMap;
    double lat = 0.0,lng = 0.0;
    ArrayList<String> distanceJ= new ArrayList<>();
    ArrayList<String> durationJ = new ArrayList<>();
    TextView txtbus_id,txtfrom,txtto,txtwaypoint,txttime,txtdis,txtseat;
    Button seat;
    String bus_id,from, to,waypoint1,waypoint2,url,distance,duration;
    double fromlat,fromlng,tolat,tolng,way1lat,way1lng,way2lat,way2lng;
    String TAG = "Error";
    String Key = "AIzaSyClIM__OMxGp5ZiMPHWDWtzi4US1z8FY-4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busmaps);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);

        txtbus_id = (TextView) findViewById(R.id.txt_BusId);
        txtfrom = (TextView) findViewById(R.id.txt_From);
        txtto = (TextView) findViewById(R.id.txt_To);
        txtwaypoint = (TextView) findViewById(R.id.txt_way1);
        txttime = (TextView) findViewById(R.id.txt_Time);
        txtdis = (TextView) findViewById(R.id.txt_Dis);
        txtseat = (TextView) findViewById(R.id.txt_Seat);

        bus_id = getIntent().getExtras().getString("bus_id");
        from = getIntent().getExtras().getString("from");
        to = getIntent().getExtras().getString("to");
        waypoint1 = getIntent().getExtras().getString("waypoint1");
        waypoint2 = getIntent().getExtras().getString("waypoint2");
        distance = getIntent().getExtras().getString("distance");
        Log.e("er", "bus dis"+distance);
        duration = getIntent().getExtras().getString("duration");
        Log.e("er", "bus dur"+duration);

        fromlat = getIntent().getExtras().getDouble("fromlat");
        fromlng = getIntent().getExtras().getDouble("fromlng");
        tolat = getIntent().getExtras().getDouble("tolat");
        tolng = getIntent().getExtras().getDouble("tolng");
        way1lat = getIntent().getExtras().getDouble("way1lat");
        way1lng = getIntent().getExtras().getDouble("way1lng");
        way2lat = getIntent().getExtras().getDouble("way2lat");
        way2lng = getIntent().getExtras().getDouble("way2lng");

        txtbus_id.setText(bus_id);
        txtfrom.setText(from);
        txtto.setText(to);
        txtwaypoint.setText(waypoint1+","+waypoint2);
        txtseat.setText("35 Seats");

        FROM = new LatLng(fromlat,fromlng);
        TO = new LatLng(tolat,tolng);
        WAY1 = new LatLng(way1lat,way1lng);
        WAY2 = new LatLng(way2lat,way2lng);

        String url = getMapsApiDirectionsUrl();
        Log.e(TAG,"url "+url);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        seat = (Button) findViewById(R.id.btnSeat);

        seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusMaps.this,SeatBooking.class));
            }
        });
    }

    private String getMapsApiDirectionsUrl() {

        url = "origin=" + FROM.latitude + "," + FROM.longitude
                + "&destination=" + TO.latitude + "," + TO.longitude
                + "&waypoints=" + WAY1.latitude + "," + WAY1.longitude + "|"+ WAY2.latitude + "," + WAY2.longitude;

        return "https://maps.googleapis.com/maps/api/directions/json" + "?" + url+"&key=" + Key;
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        googleMap=gmap;

        googleMap.addMarker(new MarkerOptions().position(FROM)
                .title(from));

        googleMap.addMarker(new MarkerOptions().position(TO)
                .title(to));

        googleMap.addMarker(new MarkerOptions().position(WAY1)
                .title(waypoint1));
        googleMap.addMarker(new MarkerOptions().position(WAY2)
                .title(waypoint2));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FROM,11));
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
        class PathJSONParser{
            List<List<HashMap<String, String>>> parse(JSONObject jObject) {
                List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
                JSONArray jRoutes;
                JSONArray jLegs;
                JSONArray jSteps;
                JSONObject jDistance,jDuration;
                try {

                    jRoutes = jObject.getJSONArray("routes");

                    for (int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        for (int j = 0; j < jLegs.length(); j++) {

                            jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                            distanceJ.add(jDistance.getString("text"));

                            jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                            durationJ.add(jDuration.getString("text"));

                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline;
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                for (int l = 0; l < list.size(); l++) {
                                    HashMap<String, String> hm = new HashMap<>();
                                    hm.put("lat", Double.toString((list.get(l)).latitude));
                                    hm.put("lng", Double.toString((list.get(l)).longitude));
                                    path.add(hm);
                                }

                                //path.add(hmDistance);

                                //path.add(hmDuration);
                            }
                            routes.add(path);
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }


            private List<LatLng> decodePoly(String encoded) {

                List<LatLng> poly = new ArrayList<>();
                int index = 0, len = encoded.length();
                int lat = 0, lng = 0;

                while (index < len) {
                    int b, shift = 0, result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lat += dlat;

                    shift = 0;
                    result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

                    LatLng p = new LatLng((((double) lat / 1E5)),
                            (((double) lng / 1E5)));
                    poly.add(p);
                }
                return poly;
            }
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points;
            PolylineOptions polyLineOptions = null;

            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);
                if (path==null) break;

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    lat = Double.parseDouble(point.get("lat"));
                    lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(8);
                polyLineOptions.color(Color.RED);

            }

            googleMap.addPolyline(polyLineOptions);
            txtdis.setText(distance);
            txttime.setText(duration);
        }
    }
}
