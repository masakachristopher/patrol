package wktechsys.com.guardprotection.Fragments;

import static wktechsys.com.guardprotection.Utilities.Constant.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import wktechsys.com.guardprotection.Activities.DashboardActivity;
import wktechsys.com.guardprotection.R;
import wktechsys.com.guardprotection.Utilities.SessionManager;

public class ProfileFragment extends Fragment {

    TextView gname, gphone, gemail, gagency, gid;
    ImageView p_pic;
    SessionManager session;
    String id,name,number,email,agency,guardid, profile_photo;
    RelativeLayout back;
    LinearLayout ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        session = new SessionManager(getActivity());
        HashMap<String, String> users = session.getUserDetails();
      //  id = users.get(session.KEY_ID);
        name = users.get(SessionManager.KEY_NAME);
        email = users.get(session.KEY_EMAIL);
        number = users.get(session.KEY_NUMBER);
        agency = users.get(session.KEY_AGENCY);
//        guardid = users.get(session.KEY_GUARDID);
        guardid = users.get(session.KEY_ID);
        profile_photo = users.get(session.KEY_PROFILE_PHOTO);


        gid = (TextView) v.findViewById(R.id.idnumber);
        gname = (TextView) v.findViewById(R.id.name);
        gphone = (TextView) v.findViewById(R.id.phone);
        gemail = (TextView) v.findViewById(R.id.email);
        gagency = (TextView) v.findViewById(R.id.agency);
        p_pic = v.findViewById(R.id.profile_photo);

        back = v.findViewById(R.id.arrowback1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DashboardActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });

//        try {
////            ImageView i = (ImageView)findViewById(R.id.image);
////            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(B).getContent());
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("http://192.168.43.144/guardadmin/uploads/FT.jpeg").getContent());
//            p_pic.setImageBitmap(bitmap);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
         Picasso.with(container.getContext())
                .load(BASE_URL+"uploads/"+profile_photo)
                 .transform(new CircleTransform())
                 .placeholder(R.drawable.guards)
                .error(R.drawable.guards)
                .into(p_pic);

        gname.setText(name);
        gphone.setText(number);
        gemail.setText(email);
        gid.setText(guardid);
//        gid.setVisibility (View.GONE);
        gagency.setText(agency);

        return v;

    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size/2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}