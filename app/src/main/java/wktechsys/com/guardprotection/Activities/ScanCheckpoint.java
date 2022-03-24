package wktechsys.com.guardprotection.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.app.AlertDialog;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import wktechsys.com.guardprotection.Adapters.RoundAdapter;
import wktechsys.com.guardprotection.Models.CheckPointModel;
import wktechsys.com.guardprotection.R;
import wktechsys.com.guardprotection.Utilities.Constant;
import wktechsys.com.guardprotection.Utilities.FetchAddressIntentServices;
import wktechsys.com.guardprotection.Utilities.LocationHelper;
import wktechsys.com.guardprotection.Utilities.SessionManager;

//import android.support.v7.app.AppCompatActivity;
//import androidx.appcompat.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;

public class ScanCheckpoint extends AppCompatActivity {

    LocationHelper locationHelper = new LocationHelper();

    RecyclerView recyclerView;
    RoundAdapter rAdapter;
    RelativeLayout strt;
    SessionManager session;
    RelativeLayout rr, back;

    String endtime, guard_id;
    private Camera camera;
    boolean showingFirst = true;
    ImageView flash;

    StringRequest stringRequest, stringRequest1;
    RequestQueue mRequestQueue, mRequestQueue1;
    private List<CheckPointModel> list = new ArrayList<>();
    public static final String TAG = "STag";
    String id, code = "",geolocation = "";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    ProgressBar progressBar;
    TextView locationText, textLatLong, address, postcode, locaity, state, district, country;
    ResultReceiver resultReceiver;
    //
    ProgressDialog showMe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_scan_checkpoint);

        resultReceiver = new AddressResultReceiver(new Handler());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> users = session.getUserDetails();
        guard_id = users.get(session.KEY_ID);

        //


        Intent i = getIntent();
        code = i.getStringExtra("code");

        locationText =  findViewById(R.id.location_text);

        rr = (RelativeLayout) findViewById(R.id.r1);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        rAdapter = new RoundAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(rAdapter);

        HashMap<String, String> roles = session.getUserDetails();
        id = roles.get(SessionManager.KEY_ID);

        strt = (RelativeLayout) findViewById(R.id.strtscanning);
        strt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScanCheckpoint.this, ScanActivity.class);
                finish();
                startActivity(i);
            }
        });

        back = (RelativeLayout) findViewById(R.id.arrowback2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        flash = findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (showingFirst == true) {

                    showingFirst = false;
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();


                } else {

                    showingFirst = true;
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    camera.release();


                }
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanCheckpoint.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }


        CList();
        Aboutus();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    public static String getLocationName(Context context, double lat, double lon) {
        Geocoder g = new Geocoder(context);
        try {
            Address address = g.getFromLocation(lat, lon, 1).get(0);
            String address_line = "";
            int max_address = address.getMaxAddressLineIndex();
            for (int i = 0; i < max_address; i++) {

                address_line += address.getAddressLine(i) + " ";
            }
            return address_line;
        } catch (IOException | IndexOutOfBoundsException e) {
        }
        return null;
    }
    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ScanCheckpoint.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
//                Toast.makeText(ScanCheckpoint.this, strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
                Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private void getCurrentLocation() {
//        progressBar.setVisibility(View.VISIBLE);
        showMe= new ProgressDialog(ScanCheckpoint.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(ScanCheckpoint.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        showMe.dismiss();
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            String geolocation = String.format("%s %s",lati,longi);
                            locationText.setText(geolocation);
                            locationHelper.setCurrentLocation(geolocation);

//                            locationHelper.setCurrentLocation(getCompleteAddressString(lati,longi));

//                            locationHelper.setCurrentLocation(getLocationName(ScanCheckpoint.this, lati,longi));
                            Location location = new Location("providerNA");
                            location.setLongitude(longi);
                            location.setLatitude(lati);

//                            fetchaddressfromlocation(location);

                        } else {
//                            progressBar.setVisibility(View.GONE);
                            showMe.dismiss();

                        }
                    }
                }, Looper.getMainLooper());

    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constant.SUCCESS_RESULT) {
//                address.setText(resultData.getString(Constants.ADDRESS));
//                locaity.setText(resultData.getString(Constants.LOCAITY));
//                state.setText(resultData.getString(Constants.STATE));
//                district.setText(resultData.getString(Constants.DISTRICT));
//                country.setText(resultData.getString(Constants.COUNTRY));
//                postcode.setText(resultData.getString(Constants.POST_CODE));
            } else {
                Toast.makeText(ScanCheckpoint.this, resultData.getString(Constant.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }


    }

    private void fetchaddressfromlocation(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentServices.class);
        intent.putExtra(Constant.RECEVIER, resultReceiver);
        intent.putExtra(Constant.LOCATION_DATA_EXTRA, location);
        startService(intent);


    }

    public void Aboutus() {

//        Toast.makeText(ScanCheckpoint.this, "hello", Toast.LENGTH_SHORT).show();


        final ProgressDialog showMe = new ProgressDialog(ScanCheckpoint.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        stringRequest1 = new StringRequest(Request.Method.POST, Constant.ABOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        JSONObject j = null;
                        try {
                            j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            if (status.equals("200")) {


                                String title = j.getString("title");
                                String descripts = j.getString("description");
                                String shiftTime = j.getString("shift");
                                String intimee = j.getString("intime");
                                String tround = j.getString("total_rounds");
                                String cround = j.getString("completed_rounds");
                                String mround = j.getString("missed_checkpoints");
                                endtime = j.getString("end");
//                                Toast.makeText(ScanCheckpoint.this, "end time"+ intimee + endtime, Toast.LENGTH_SHORT).show();

//
//                                nameC.setText(title);
//                                descript.setText(descripts);
//                                duration.setText(shiftTime);
//                                intime.setText("IN time: " + intimee);
//                                total.setText(tround);
//                                complete.setText(cround);
//                                check.setText(mround);
                                //  showMe.dismiss();

//                                mShimmerViewContainer.stopShimmerAnimation();
//                                mShimmerViewContainer.setVisibility(View.GONE);
//                                companyrr.setVisibility(View.VISIBLE);



                            }else if(status.equals("500")) {
//                                showMe.dismiss();

                                String msg = j.getString("msg");
                                session.logoutUser();
                                Intent i = new Intent(ScanCheckpoint.this, LoginActivity.class);
                                startActivity(i);
                                finish();
//                                Toast.makeText(ScanCheckpoint.this, msg, Toast.LENGTH_SHORT).show();

                            }
                            else {
                                String failed = j.getString("msg");
//                                 showMe.dismiss();
//                                Toast.makeText(ScanCheckpoint.this, failed, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            showMe.dismiss();

                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                         showMe.dismiss();
                        if (ScanCheckpoint.this != null) {

                            // NetworkDialog();
                        }

                        //Toast.makeText(getActivity(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("apikey", "d29985af97d29a80e40cd81016d939af");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();
                params.put("guard_id", guard_id);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ScanCheckpoint.this);
        requestQueue.add(stringRequest1);
    }

    public void CList() {

        final ProgressDialog showMe = new ProgressDialog(ScanCheckpoint.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        mRequestQueue = Volley.newRequestQueue(ScanCheckpoint.this);

        stringRequest = new StringRequest(Request.Method.POST, Constant.SCAN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showMe.dismiss();
                        list.clear();
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                             if(status.equals("500")) {
                                session.logoutUser();
                                Intent i = new Intent(ScanCheckpoint.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                                Toast.makeText(ScanCheckpoint.this, msg, Toast.LENGTH_SHORT).show();
                            }
                           else if (status.equals("200")) {
                                JSONArray applist = j.getJSONArray("data");
                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {

                                        CheckPointModel model = new CheckPointModel();
                                        JSONObject getOne = applist.getJSONObject(i);

                                        model.setId(getOne.getString("id"));
                                        model.setDate(getOne.getString("date"));
                                        model.setTime(getOne.getString("time"));
                                        model.setCheckno(getOne.getString("name"));
                                        model.setLocation(getOne.getString("location"));

                                        list.add(model);
                                        rAdapter = new RoundAdapter(getApplicationContext(), list);
                                        recyclerView.setAdapter(rAdapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        rr.setVisibility(View.GONE);
                                        if (!msg.equals("success")) {

                                            Toast.makeText(ScanCheckpoint.this, msg, Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(ScanCheckpoint.this, "scan complete!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    showMe.dismiss();
                                    recyclerView.setVisibility(View.GONE);
                                    rr.setVisibility(View.VISIBLE);
                                    Toast.makeText(ScanCheckpoint.this, msg, Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(ScanCheckpoint.this, "test", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                showMe.dismiss();
                                recyclerView.setVisibility(View.GONE);
                                rr.setVisibility(View.VISIBLE);
                                Toast.makeText(ScanCheckpoint.this, msg, Toast.LENGTH_SHORT).show();
                                //  nodata.setVisibility(View.VISIBLE);
                                //  recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            showMe.dismiss();
                            recyclerView.setVisibility(View.GONE);
                            rr.setVisibility(View.VISIBLE);
                            Log.e("TAG", "Something Went Wrong");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showMe.dismiss();
                        // NetworkDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("apikey", "d29985af97d29a80e40cd81016d939af");
                return headers;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                if (code == null) {

                    params.put("qrcode", "0");

                } else {

                    params.put("qrcode", code);
                }

                if (geolocation == null) {

                    params.put("location", "");
//                    params.put("location", locationHelper.getCurrentLocation());

                } else {

//                    params.put("location", geolocation);


                    params.put("location", locationHelper.getCurrentLocation());

//                    params.put("location", locationText.getText().toString());
                }

//                headers.put("location", geolocation);
                params.put("guard_id", id);
                return params;
            }

        };
        stringRequest.setTag(TAG);
        mRequestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(ScanCheckpoint.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                CList();

            }
        });
        dialogs.show();
    }
}