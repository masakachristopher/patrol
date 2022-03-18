package wktechsys.com.guardprotection.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import wktechsys.com.guardprotection.Activities.AttendenceActivity;
import wktechsys.com.guardprotection.Activities.DashboardActivity;
import wktechsys.com.guardprotection.Activities.LoginActivity;
import wktechsys.com.guardprotection.Activities.MissedActivity;
import wktechsys.com.guardprotection.Activities.ScanCheckpoint;
import wktechsys.com.guardprotection.Adapters.RoundAdapter;
import wktechsys.com.guardprotection.Adapters.TotalRoundAdapter;
import wktechsys.com.guardprotection.Models.CheckPointModel;
import wktechsys.com.guardprotection.Models.RoundModel;
import wktechsys.com.guardprotection.R;
import wktechsys.com.guardprotection.Utilities.ChronometerHelper;
import wktechsys.com.guardprotection.Utilities.Constant;
import wktechsys.com.guardprotection.Utilities.SessionManager;

public class HomeFragment extends Fragment {

    TextView strt,t_View, strtShift,endShift, nameC, descript, intime, duration, total, complete, check, gname;
    FloatingActionButton flash;
    RelativeLayout rounds;
    Dialog dialog;
    private Camera camera;
    boolean showingFirst = true;
    ImageView logoutbtn, cancel;
    SessionManager session;
    String guard_id, name;
    RelativeLayout viewattend;
    String endtime;
    ShimmerFrameLayout mShimmerViewContainer;
    RelativeLayout companyrr, missedrr;
    ProgressDialog progressDialog;
    Button startBtn,stopBtn;
    private int sec = 0;
    private boolean is_running;
    private boolean was_running;
    Chronometer chronometer;
    ChronometerHelper chronometerHelper = new ChronometerHelper();



    RecyclerView recyclerView;
    TotalRoundAdapter rAdapter;
    StringRequest stringRequest, stringRequest1;
    RequestQueue mRequestQueue, requestQueue, mRequestQueue1;
    private List<RoundModel> list = new ArrayList<>();
    public static final String TAG = "STag";

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("seconds", sec);
//        outState.putBoolean("running", is_running);
//        outState.putBoolean("was_running", was_running);
//        super.onSaveInstanceState(outState);
//    }
    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState)
    {
        savedInstanceState.putInt("seconds", sec);
        savedInstanceState.putBoolean("running", is_running);
        savedInstanceState.putBoolean("wasRunning", was_running);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Fabric.with(getActivity(), new Crashlytics());
        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            sec = savedInstanceState.getInt("seconds");
            is_running = savedInstanceState.getBoolean("running");
            was_running = savedInstanceState .getBoolean("wasRunning");
        }

        session = new SessionManager(getActivity());

        strt = v.findViewById(R.id.startScan);
        strtShift = v.findViewById(R.id.startShift);
        endShift = v.findViewById(R.id.endShift);
        nameC = v.findViewById(R.id.cname);
        gname = v.findViewById(R.id.textname);
        descript = v.findViewById(R.id.description);
        duration = v.findViewById(R.id.shiftD);
        intime = v.findViewById(R.id.intime);
        viewattend = v.findViewById(R.id.viewattendence);
        total = v.findViewById(R.id.tround);
        complete = v.findViewById(R.id.cround);
        check = v.findViewById(R.id.check);
        rounds = v.findViewById(R.id.r2);
        mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);
        companyrr = v.findViewById(R.id.companyinfo);
        missedrr = v.findViewById(R.id.missR);
        t_View = v.findViewById(R.id.time_view);
        chronometer = v.findViewById(R.id.stop_watch);
//        startStopWatch();
        if (chronometerHelper.getStartTime() != null) {
            getCurrentWatchTime();
        }
//        ChronometerHelper chronometerHelper = new ChronometerHelper();
//        startBtn = v.findViewById(R.id.start_button);
//        stopBtn = v.findViewById(R.id.stop_button);

//        stopStartButton = (Button) findViewById(R.id.startStopButton);

        logoutbtn = v.findViewById(R.id.logout);

        HashMap<String, String> users = session.getUserDetails();
        guard_id = users.get(session.KEY_ID);
        name = users.get(session.KEY_NAME);

        gname.setText(name);

        //session.checkLogin();
//        if (savedInstanceState != null) {
//            sec = savedInstanceState.getInt("seconds");
//            is_running = savedInstanceState.getBoolean("running");
//            was_running = savedInstanceState .getBoolean("wasRunning");
//        }
//        running_Timer();

        strt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ScanCheckpoint.class);
                startActivity(i);
            }
        });

        strtShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getContext(), ScanCheckpoint.class);
//                startActivity(i);
//                Toast.makeText(getActivity(), "start shift", Toast.LENGTH_SHORT).show();
//                StartShift();
//                onClickStart();
//                chronometer.start();
                startStopWatch();


//        if(is_running == false)
//        {
////            is_running = true;
////            setButtonUI("Click to End Shift", R.color.colorAccent, R.color.cardview_light_background);
//
////            startTimer();
////            StartShift();
////            sta
////            onClickStart();
//
////            running_Timer();
//
//        }
//        else
//        {
//            is_running = false;
////            setButtonUI("Click to Start Shift", R.color.colorAccent,R.color.cardview_light_background);
//
////            timerTask.cancel();
////            EndShift();
////            running_Timer();
////            onClickStop();
//        }
            }
        });

//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent i = new Intent(getContext(), ScanCheckpoint.class);
////                startActivity(i);
////                Toast.makeText(getActivity(), "start shift", Toast.LENGTH_SHORT).show();
////                StartShift();
//                onClickStart();
//            }
//        });
        endShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getContext(), ScanCheckpoint.class);
//                startActivity(i);
//                Toast.makeText(getActivity(), "start shift", Toast.LENGTH_SHORT).show();
                EndShift();
//                onClickStop();
            }
        });

        rounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
                dialog.setTitle("Rounds");
                dialog.setContentView(R.layout.dialog);
                dialog.setCanceledOnTouchOutside(false);

                recyclerView = (RecyclerView) dialog.findViewById(R.id.RoundRecycler);
                recyclerView.setHasFixedSize(false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                RoundInfo();

                cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Logout();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        flash = v.findViewById(R.id.fab);
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

        viewattend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AttendenceActivity.class);
                startActivity(i);
            }
        });

        missedrr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MissedActivity.class);
                startActivity(i);
            }
        });

        Aboutus();
        return v;
    }

//    @Override
//    public void onSaveInstanceState(
//            Bundle savedInstanceState)
//    {
//        savedInstanceState.putInt("seconds", sec);
//        savedInstanceState.putBoolean("running", is_running);
//        savedInstanceState.putBoolean("wasRunning", was_running);
//    }

    private void getCurrentWatchTime() {
        if (chronometerHelper.getStartTime() == null) {
            // If the start date is not defined, set it.
            long startTime = SystemClock.elapsedRealtime();
            chronometerHelper.setStartTime(startTime);
            chronometer.setBase(startTime);
        } else {
            // Otherwise set the chronometer's base to the original
            // starting time.
            chronometer.setBase(chronometerHelper.getStartTime());

        }

        chronometer.start();
    }

    private void startStopWatch() {
        if (chronometerHelper.getStartTime() == null) {
            // If the start date is not defined, set it.
            long startTime = SystemClock.elapsedRealtime();
            chronometerHelper.setStartTime(startTime);
            chronometer.setBase(startTime);
        } else {
            // Otherwise set the chronometer's base to the original
            // starting time.
            chronometer.setBase(chronometerHelper.getStartTime());

        }

        chronometer.start();

    }

    private void stopStopWatch() {

        long startTime = SystemClock.elapsedRealtime();
        chronometerHelper.setStartTime(startTime);
        chronometer.setBase(startTime);
        chronometer.stop();

    }
    private void setButtonUI(String start, int color, int bgColor)
    {
        //startstopBtn
        strtShift.setText(start);
        strtShift.setBackgroundColor(ContextCompat.getColor(getActivity(), bgColor));
        strtShift.setTextColor(ContextCompat.getColor(getActivity(), color));
    }
    protected void  StartShift(){

//        if(timerStarted == false)
//        {
//            timerStarted = true;
//            setButtonUI("STOP", R.color.red);
//
//            startTimer();
//        }
//        else
//        {
//            timerStarted = false;
//            setButtonUI("START", R.color.green);
//
//            timerTask.cancel();
//        }
//
//        if(is_running == false)
//        {
//            is_running = true;
//            setButtonUI("STOP", R.color.colorAccent);
//
////            startTimer();
////            onClickStart();
//        }
//        else
//        {
//            was_running = false;
//            setButtonUI("START", R.color.cardview_shadow_end_color);
//
////            timerTask.cancel();
//        }

        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

//        Toast.makeText(getActivity(), "Guard ID" + guard_id, Toast.LENGTH_SHORT).show();
        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.START_SHIFT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        Log.d("NetworkLog", "Request Sent");

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Matching server responce message to our text.
                        JSONObject j = null;
                        try {
                            j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            if (status.equals("200")) {
                                String suc = j.getString("msg");
//                                String data = j.getString("data");
//                                    String id = j.getString("id");
//                                    String name = j.getString("name");
//                                    String email = j.getString("email");
//                                    String agency = j.getString("agency");
//                                    String guardid = j.getString("guard_id");
//                                    String profile_photo = j.getString("profile_photo");
                                // If response matched then show the toast.
                                Toast.makeText(getActivity(), suc, Toast.LENGTH_LONG).show();

                                // Finish the current Login activity.
//                                    finish();
//                                    session.createLoginSession(mobile.getText().toString(), password.getText().toString(), id, name, email, agency, guardid,profile_photo);

                                // Opening the user profile activity using intent.
//                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

//                                    startActivity(intent);
//                                setButtonUI("STOP", R.color.colorAccent);
//                                onClickStart();
//                                setButtonUI("Click to End Shift", R.color.colorAccent, R.color.cardview_light_background);


                            }
                            else if(status.equals("201")) {
                                String suc = j.getString("msg");
//                                String data = j.getString("data");
//                                    String id = j.getString("id");
//                                    String name = j.getString("name");
//                                    String email = j.getString("email");
//                                    String agency = j.getString("agency");
//                                    String guardid = j.getString("guard_id");
//                                    String profile_photo = j.getString("profile_photo");
                                // If response matched then show the toast.

                                Toast.makeText(getActivity(), suc, Toast.LENGTH_LONG).show();
                            }
                            else {
                                String msg = j.getString("msg");
                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // NetworkDialog();
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

                // Adding All values to Params.
                // The firs argument should be same sa your MySQL database table columns.
//                    params.put("mobile", mobile.getText().toString());
//                    params.put("password", password.getText().toString());
                params.put("guard_id", guard_id);


                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

    protected void EndShift(){


        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

//        Toast.makeText(getActivity(), "Guard ID" + guard_id, Toast.LENGTH_SHORT).show();
        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.END_SHIFT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        Log.d("NetworkLog", "Request Sent");

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Matching server responce message to our text.
                        JSONObject j = null;
                        try {
                            j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            if (status.equals("200")) {
                                String suc = j.getString("msg");
//                                String data = j.getString("data");
//                                    String id = j.getString("id");
//                                    String name = j.getString("name");
//                                    String email = j.getString("email");
//                                    String agency = j.getString("agency");
//                                    String guardid = j.getString("guard_id");
//                                    String profile_photo = j.getString("profile_photo");
                                // If response matched then show the toast.
                                Toast.makeText(getActivity(), suc, Toast.LENGTH_SHORT).show();

                                // Finish the current Login activity.
//                                    finish();
//                                    session.createLoginSession(mobile.getText().toString(), password.getText().toString(), id, name, email, agency, guardid,profile_photo);

                                // Opening the user profile activity using intent.
//                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

//                                    startActivity(intent);
                                onClickStop();
                                setButtonUI("Click to Start Shift", R.color.colorAccent,R.color.cardview_light_background);


                            } else {
                                String msg = j.getString("msg");
                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();
                        // NetworkDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("apikey", "d29985af97d29a80e40cd81016d939af");
//                headers.put("apikey", "d29985af97d29a80e40cd81016d939af");

//                d29985af97d29a80e40cd81016d939af
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                // The firs argument should be same sa your MySQL database table columns.
//                    params.put("mobile", mobile.getText().toString());
//                    params.put("password", password.getText().toString());
                params.put("guard_id", guard_id);


                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

    public void RoundInfo() {

        final ProgressDialog showMe = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        mRequestQueue = Volley.newRequestQueue(getActivity());

        stringRequest = new StringRequest(Request.Method.POST, Constant.ROUND_URL,
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
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                startActivity(i);
                                getActivity().finish();
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                            if (status.equals("200")) {
                                JSONArray applist = j.getJSONArray("data");
                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {

                                        RoundModel model = new RoundModel();
                                        JSONObject getOne = applist.getJSONObject(i);

                                        model.setRname(getOne.getString("round_name"));
                                        model.setTime(getOne.getString("duration"));

                                        list.add(model);
                                        rAdapter = new TotalRoundAdapter(getActivity(), list);
                                        recyclerView.setAdapter(rAdapter);

                                    }
                                } else {
                                    showMe.dismiss();
                                    Toast toast = Toast.makeText(getActivity(), "" + j.getString("msg"), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                }
                            } else {

                                showMe.dismiss();
                                Toast toast = Toast.makeText(getActivity(), "" + j.getString("msg"), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //  nodata.setVisibility(View.VISIBLE);
                                //  recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            showMe.dismiss();

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
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("guard_id", guard_id);
                return headers;
            }

        };
        stringRequest.setTag(TAG);
        mRequestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void Aboutus() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ABOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        //showMe.dismiss();

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


                                nameC.setText(title);
                                descript.setText(descripts);
                                duration.setText(shiftTime);
                                intime.setText("IN time: " + intimee);
                                total.setText(tround);
                                complete.setText(cround);
                                check.setText(mround);
                                //  showMe.dismiss();

                                mShimmerViewContainer.stopShimmerAnimation();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                companyrr.setVisibility(View.VISIBLE);

                            }else if(status.equals("500")) {
                                String m = j.getString("msg");
                                session.logoutUser();
                                getActivity().finish();
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                startActivity(i);
                                Toast.makeText(getActivity(), m, Toast.LENGTH_SHORT).show();

                            }
                            else {
                                String failed = j.getString("msg");
                                // showMe.dismiss();
                                Toast.makeText(getActivity(), failed, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // showMe.dismiss();
                        if (getActivity() != null) {

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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void Logout() {
        final ProgressDialog showMe = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.LOGOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        JSONObject j = null;
                        try {
                            j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            if (status.equals("200")) {

                                session.logoutUser();
                                getActivity().finish();
                                Toast.makeText(getActivity(), "Successfully Logout", Toast.LENGTH_SHORT).show();
                                showMe.dismiss();

                            } else {
                                showMe.dismiss();
                                String msg = j.getString("msg");
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

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
                        // NetworkDialog1();
                        // Toast.makeText(getActivity(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("guard_id", guard_id);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(getActivity());
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                Aboutus();

            }
        });
        dialogs.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
        if (was_running) {
            is_running = true;
        }
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();

        was_running = is_running;
        is_running = false;
    }

    private void NetworkDialog1() {
        final Dialog dialogs = new Dialog(getActivity());
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                Logout();

            }
        });
        dialogs.show();
    }
//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        was_running = is_running;
//        is_running = false;
//    }
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        if (was_running) {
//            is_running = true;
//        }
//    }
    public void onClickStart()
    {
        is_running = true;
    }
    public void onClickStop()
    {
        is_running = false;
    }
    public void onClickReset()
    {
        is_running = false;
        sec = 0;
    }
    private void running_Timer()
    {
//        final TextView t_View = v.findViewById(R.id.time_view);
        final Handler handle = new Handler();
        handle.post(new Runnable() {
            @Override
            public void run()
            {
                int hrs = sec / 3600;
                int mins = (sec % 3600) / 60;
                int secs = sec % 60;
                String time_t = String .format(Locale.getDefault(), "    %d:%02d:%02d   ", hrs,mins, secs);
                t_View.setText(time_t);
                if (is_running) {
                    sec++;
                }
                handle.postDelayed(this, 1000);
            }
        });
    }

}
