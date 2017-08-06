package com.example.findmybus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    String REGISTER_URL = "http://mysample.hol.es/volleyRegister.php";
    EditText editTextUsername, editTextEmail, editTextPassword;
    String KEY_USERNAME = "username", KEY_PASSWORD = "password", KEY_EMAIL = "email";
    String username = "",password = "", email = "";
    Button buttonRegister, buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                email = editTextEmail.getText().toString();

                if (username.equals("") || password.equals("") || email.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Value", Toast.LENGTH_LONG).show();
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put(KEY_USERNAME,username);
                        params.put(KEY_PASSWORD, password);
                        params.put(KEY_EMAIL, email);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                requestQueue.add(stringRequest);
            }
        });
    }
}
