package com.example.findmybus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResult extends AppCompatActivity {
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String bus_id, from, to,waypoint1,waypoint2,time,mturl,distancemt,durationmt;
    double fromlat,fromlng,tolat,tolng,way1lat,way1lng,way2lat,way2lng;
    ArrayList<HashMap<String, String>> searchList;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.bus_list);

        fromlat = getIntent().getExtras().getDouble("fromlat");
        fromlng = getIntent().getExtras().getDouble("fromlng");
        tolat = getIntent().getExtras().getDouble("tolat");
        tolng = getIntent().getExtras().getDouble("tolng");

        time = "2 Hours 45 Min.";

        json_string = getIntent().getExtras().getString("json_data");
        Log.e("Error","Json "+json_string);
        try {
            jsonObject = new JSONObject(json_string);

            jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JO = jsonArray.getJSONObject(i);
                bus_id = JO.getString("bus_id");
                from = JO.getString("fromlocation");
                waypoint1 = JO.getString("waypoint1");
                waypoint2 = JO.getString("waypoint2");
                to = JO.getString("tolocation");
                way1lat = JO.getDouble("way1lat");
                way1lng = JO.getDouble("way1lng");
                way2lat = JO.getDouble("way2lat");
                way2lng = JO.getDouble("way2lng");

                String  matrix= "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" +
                        from + "&destinations="+ to +"&key=AIzaSyClIM__OMxGp5ZiMPHWDWtzi4US1z8FY-4";

                URI uri2 = null;
                try {
                    uri2 = new URI(matrix.replace(" ", "%20"));
                    mturl = uri2.toString();
                    Log.e("MSG","New URL: "+mturl);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jor = new JsonObjectRequest(mturl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try{

                                    JSONArray jr = response.getJSONArray("rows");
                                    JSONArray je = ((JSONObject) jr.get(0)).getJSONArray("elements");
                                    JSONObject jd = ((JSONObject) je.get(0)).getJSONObject("distance");
                                    distancemt = jd.getString("text");
                                    Log.e("ER", "dis "+distancemt);
                                    JSONObject jdis = ((JSONObject) je.get(0)).getJSONObject("duration");
                                    durationmt = jdis.getString("text");
                                    Log.e("ER", "dis "+durationmt);

                                }catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley","Error");

                            }
                        }
                );
                RequestQueue requestQueue = Volley.newRequestQueue(SearchResult.this);
                requestQueue.add(jor);

                HashMap<String, String> bus_detail = new HashMap<>();
                bus_detail.put("bus_id", bus_id);
                bus_detail.put("from", from);
                bus_detail.put("to", to);
                bus_detail.put("waypoint1", waypoint1 +"," + waypoint2);
                bus_detail.put("time",distancemt);
                Log.e("ER", "hm dis "+distancemt);

                searchList.add(bus_detail);
                ListAdapter adapter = new SimpleAdapter(SearchResult.this, searchList,
                        R.layout.list_item, new String[]{"bus_id", "from", "to", "waypoint1","time"},
                        new int[]{R.id.txt_BusId, R.id.txt_From, R.id.txt_To, R.id.txt_way1,R.id.txt_time});
                listView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error","Error in code");
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResult.this, BusMaps.class);
                intent.putExtra("bus_id",bus_id);
                intent.putExtra("from", from);
                intent.putExtra("to", to);
                intent.putExtra("waypoint1", waypoint1);
                intent.putExtra("waypoint2", waypoint2);
                intent.putExtra("fromlat",fromlat);
                intent.putExtra("fromlng",fromlng);
                intent.putExtra("tolat",tolat);
                intent.putExtra("tolng",tolng);
                intent.putExtra("way1lat",way1lat);
                intent.putExtra("way1lng",way1lng);
                intent.putExtra("way2lat",way2lat);
                intent.putExtra("way2lng",way2lng);
                intent.putExtra("distance",distancemt);
                intent.putExtra("duration",durationmt);
                startActivity(intent);
            }
        });
    }
}
