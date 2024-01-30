package com.example.my;

import static android.view.PixelCopy.request;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private String accessToken;

    private Button button;
    final String URI = "http://192.168.3.3:8081/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.senha);
        button = findViewById(R.id.onClickLogin);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    httpClient();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void httpClient() throws IOException {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String accessToken = jsonResponse.getString("access_token");
                            Log.e("TAG", "Erro na solicitação autenticada: " + accessToken);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                usernameEditText.setText("That didn't work!");
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parâmetros necessários para autenticação (por exemplo, nome de usuário e senha)
                Map<String, String> params = new HashMap<>();
                params.put("login", usernameEditText.toString());
                params.put("senha", passwordEditText.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Adiciona o cabeçalho de autenticação (por exemplo, Basic Authentication)
                Map<String, String> headers = new HashMap<>();
                String credendial = usernameEditText.toString() + ":" + passwordEditText.toString();
                headers.put("Authorization", "Basic " + Base64.encodeToString(credendial.getBytes(), Base64.NO_WRAP));
                return headers;
            }
        };

        queue.add(stringRequest);

    }

}