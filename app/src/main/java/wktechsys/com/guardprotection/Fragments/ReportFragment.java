package wktechsys.com.guardprotection.Fragments;

import static wktechsys.com.guardprotection.Utilities.Constant.INCIDENT_URL;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
//import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import wktechsys.com.guardprotection.Activities.DashboardActivity;
import wktechsys.com.guardprotection.Activities.LoginActivity;
import wktechsys.com.guardprotection.Models.IncidentModel;
import wktechsys.com.guardprotection.R;
import wktechsys.com.guardprotection.Utilities.Constant;
import wktechsys.com.guardprotection.Utilities.SessionManager;

//import com.android.volley.JsonArrayRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RequestQueue mRequestQueue, requestQueue, mRequestQueue1;
    StringRequest mStringRequest,stringRequest,stringRequest1;
    public static String TAG = "ReportFragment";
    private List<IncidentModel> list  = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String guard_id, imageString = "";
    SessionManager session;
    Spinner spinner;
    EditText details;
    Button submitBtn, uploadBtn;
    ProgressDialog progressDialog;
    private Integer incident_type_id;
    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    ImageView uploadImage;
    RelativeLayout layout;

    ImageView select;
    RelativeLayout back;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

//        sendAndRequestResponse();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        session = new SessionManager(getActivity());
        spinner = v.findViewById(R.id.incident_spinner);
        details = v.findViewById(R.id.incident_details);
        submitBtn = v.findViewById(R.id.report_submitBtn);
        uploadBtn = v.findViewById(R.id.uploadingBtn);
        layout =v.findViewById(R.id.imageLayout1);
        uploadImage =v.findViewById(R.id.imageView);
        back = v.findViewById(R.id.arrowback3);
        select = v.findViewById(R.id.dateselect);

        HashMap<String, String> users = session.getUserDetails();
        guard_id = users.get(session.KEY_ID);
//        progressDialog = v.findViewById(R.id.progressBar);
//        name = users.get(session.KEY_NAME);
//        return inflater.inflate(R.layout.fragment_report, container, false);

//        sendAndRequestResponse();
        getIncidents();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),DashboardActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (details.getText().toString().isEmpty() || incident_type_id.toString().isEmpty()) {

                    Toast.makeText(getActivity(), " Please fill all the fields!!", Toast.LENGTH_SHORT).show();

                } else {
                    Submit(imageString);
//                                    Toast.makeText(getActivity(),"Response :" + incident_type.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                }
//                Toast.makeText(getActivity(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(),"Response :" + incident_type.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //method to upload the image
                showFileChooser();

            }
        });
//        fetchJsonDataFromUrl();
        return  v;
    }

    private void showFileChooser() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
//        pickImageIntent.putExtra("aspectX", 1);
//        pickImageIntent.putExtra("aspectY", 1);
//        pickImageIntent.putExtra("scale", true);
//        pickImageIntent.putExtra("outputFormat",
//                Bitmap.CompressFormat.JPEG.toString());


        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
//        startActivityForResult
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    public void Submit(final String image) {
//
        // Assigning Activity this to progress dialog.
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());



//        String image = getStringImage(Bitmap bitmap);
//        Toast.makeText(getActivity(), "submit now"+image,Toast.LENGTH_LONG).show();

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();

//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ADD_REPORT_URL,
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
//                                String suc1 = j.getString("details_validation");
//                                String suc2 = j.getString("image_validation");
//                                String suc3 = j.getString("guard_id_validation");
//                                String suc4 = j.getString("incident_type_validation");

//                                String id = j.getString("id");
//                                String name = j.getString("name");
//                                String email = j.getString("email");
//                                String agency = j.getString("agency");
//                                String guardid = j.getString("guard_id");
//                                String profile_photo = j.getString("profile_photo");
                                // If response matched then show the toast.
                                Toast.makeText(getActivity(), suc, Toast.LENGTH_SHORT).show();

                                // Finish the current Login activity.
//                                getActivity().finish();
                                details.setText("");
                                uploadImage.setImageDrawable(null);
                                uploadImage.setImageBitmap(null);
                                layout.setVisibility(View.GONE);
                                uploadImage.setVisibility(View.GONE);
//                                session.createLoginSession(mobile.getText().toString(), password.getText().toString(), id, name, email, agency, guardid,profile_photo);

                                // Opening the user profile activity using intent.
//                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

//                                startActivity(intent);

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
                params.put("details", details.getText().toString());
//                params.put("password", password.getText().toString());
                params.put("incident_type", incident_type_id.toString());
                params.put("guard_id", guard_id);
//                params.put("image", image);

                if(image.isEmpty()){
                    params.put("image", "");
                }else {
                    params.put("image", image);

                }


                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }
    private void getIncidents() {

        final ProgressDialog showMe = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(getActivity());
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.POST, INCIDENT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showMe.dismiss();
                list.clear();
                JSONObject j = null;
                try {
                    j = new JSONObject(response);

                    String status = j.getString("status");

                    if(status.equals("500")) {
                        String msg = j.getString("msg");
                        session.logoutUser();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                    else if (status.equals("200")) {
                        JSONArray incidentList = j.getJSONArray("data");
//
                        if (incidentList!= null && incidentList.length() > 0) {
                            for (int i = 0; i <incidentList.length(); i++) {

                                IncidentModel incident = new IncidentModel();
                                JSONObject getOne = incidentList.getJSONObject(i);

                                incident.setId(getOne.getInt("id"));
                                incident.setName(getOne.getString("name"));

                                list.add(incident);
                                initSpinner(list);
//                                aAdapter = new AttendanceAdapter(getActivity(),list);
//                                recyclerView.setAdapter(aAdapter);


//                                mShimmerViewContainer.stopShimmerAnimation();
//                                mShimmerViewContainer.setVisibility(View.GONE);

                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), ""+j.getString("msg"),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                } catch (JSONException e) {
                    showMe.dismiss();
                    Log.e("TAG", "Something Went Wrong");
                }

//                Toast.makeText(getActivity(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showMe.dismiss();
                Log.i(TAG,"Error :" + error.toString());
            }
        }){
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


        mRequestQueue.add(mStringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
//            Toast.makeText(getActivity(), data.getData().toString(),Toast.LENGTH_SHORT);
            try {
//                InputStream inputStream = getActivity().getContentResolver().openInputStram(filePath);
//                bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                layout.setVisibility(View.VISIBLE);
                uploadImage.setVisibility(View.VISIBLE);
                uploadImage.setImageBitmap(bitmap);
//                Bitmap lastBitmap = null;
//                lastBitmap = bitmap;

                //encoding image to string
                imageString = getStringImage(bitmap);
//                Log.d("image",image);
                //passing the image to volley
//                SendImage(image);
//                Submit(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
//            Uri filePath = data.getData();
//
//            try {
//                //getting image from gallery
////                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
//                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
////                Bitmap lastBitmap = null;
////                lastBitmap = bitmap;
//
//                //Setting image to ImageView
////                image.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }




    private void initSpinner(final List<IncidentModel> incidents) {
        if (incidents.size() > 0) {
            ArrayAdapter<IncidentModel> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, incidents);
//            ArrayAdapter<IncidentModel> adapter =  ArrayAdapter.createFromResource(this,R.array.planets_array, android.R.layout.simple_spinner_item);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    IncidentModel incident = incidents.get(position);
                    incident_type_id = incident.getId();
//                    String meta = "ID: " + incident.getId() + "\nNAME: " + incident.getName();
//                    textView.setText(meta);
//                    Toast.makeText(getActivity(),"Response :" + incident_type.toString(), Toast.LENGTH_LONG).show();//display the response on screen

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
}