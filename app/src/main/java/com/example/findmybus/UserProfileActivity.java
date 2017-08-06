package com.example.findmybus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class UserProfileActivity extends AppCompatActivity {
    EditText fromlocation, tolocation;
    String Url,alturl;
    TextView textView;
    Button bnSearch;
    ProgressDialog loading;
    String from,to;
    double tolat,tolng,fromlat,fromlng = 00.0000;
    JSONObject jsonObject;
    JSONArray jsonArray;
    static final String DATA_URL = "http://mysample.hol.es/volleySearch.php?from=";
    static final String ALT = "http://mysample.hol.es/alterSearch.php?from=";
    static  final String bus ="No Direct Bus Available. Check alternative route?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        fromlocation = (EditText) findViewById(R.id.fromLocation);
        tolocation = (EditText) findViewById(R.id.toLocation);
        bnSearch = (Button) findViewById(R.id.search);
        textView = (TextView) findViewById(R.id.noresult);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = fromlocation.getText().toString();
                to = tolocation.getText().toString();

                if (from.equals("") || to.equals("")) {
                    Toast.makeText(UserProfileActivity.this, "Please enter data", Toast.LENGTH_LONG).show();
                    return;
                }
                loading = ProgressDialog.show(UserProfileActivity.this,"Please wait...","Fetching...",false,false);

                String url = ALT+from+"&to="+to+"&fromlat="+fromlat+"&fromlng="+fromlng+"&tolat="+tolat+"&tolng="+tolng;
                Log.e("MSG","ALT URL: "+url);
                try {
                    URI uri = new URI(url.replace(" ", "%20"));
                    alturl = uri.toString();
                    Log.e("MSG","ALT URL 2: "+alturl);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                StringRequest stringRequest = new StringRequest(alturl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJSON(response);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UserProfileActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
                requestQueue.add(stringRequest);
            }
        });

        bnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = fromlocation.getText().toString();
                to = tolocation.getText().toString();

                if (from.equals("") || to.equals("")) {
                    Toast.makeText(UserProfileActivity.this, "Please enter data", Toast.LENGTH_LONG).show();
                    return;
                }
                loading = ProgressDialog.show(UserProfileActivity.this,"Please wait...","Fetching...",false,false);

                String url = DATA_URL+from+"&to="+to;
                Log.e("MSG","URL: "+url);

                try {
                    URI uri = new URI(url.replace(" ", "%20"));
                    Url = uri.toString();
                    Log.e("MSG","New URL: "+Url);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                StringRequest stringRequest = new StringRequest(Url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJSON(response);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UserProfileActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
                requestQueue.add(stringRequest);
            }
        });
    }

    private void showJSON(String response){

        try {
            jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONArray("result");
            Log.e("Res", "Res:"+jsonObject);

            if (jsonArray.length() == 0) {
                textView.setText(bus);
            }
            else {
                Intent intent = new Intent(UserProfileActivity.this, SearchResult.class);
                intent.putExtra("json_data", response);
                intent.putExtra("fromlat",fromlat);
                intent.putExtra("fromlng",fromlng);
                intent.putExtra("tolat",tolat);
                intent.putExtra("tolng",tolng);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void fromplace(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    public void toplace(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, 2);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                fromlat = place.getLatLng().latitude;
                fromlng = place.getLatLng().longitude;
                from = place.getName().toString().trim();
                fromlocation.setText(from);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Tag", "Error");
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                to = place.getName().toString().trim();
                tolat = place.getLatLng().latitude;
                tolng = place.getLatLng().longitude;
                tolocation.setText(to);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Tag", "Error");
            }
        }
    }
}
