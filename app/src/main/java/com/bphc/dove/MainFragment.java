package com.bphc.dove;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by charu on 08-01-2018.
 */

public class MainFragment extends Fragment implements View.OnClickListener {

    Spinner camera;
    Spinner lens;
    Spinner tripods;
    Spinner SDCards;
    Spinner accessories;
    Button submit;
    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);

        camera = rootView.findViewById(R.id.camera);
        lens = rootView.findViewById(R.id.lens);
        tripods = rootView.findViewById(R.id.tripods);
        SDCards = rootView.findViewById(R.id.sdCards);
        accessories = rootView.findViewById(R.id.accessories);
        submit = rootView.findViewById(R.id.submit);

        submit.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.submit:
                submit();
                break;

        }
    }

    private void submit() {

        MainActivity.db.collection("Arena 2k18").document(MainActivity.currentUser.getDisplayName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String currentTime = Calendar.getInstance().getTime().toString();

                Map<String, Object> map = new HashMap<>();
                map.put("Camera", documentSnapshot.getString("Camera") + " , " + camera.getSelectedItem().toString() + " at " + currentTime);
                map.put("Lens", documentSnapshot.getString("Lens") + " , " + lens.getSelectedItem().toString() + " at " + currentTime);
                map.put("Tripod", documentSnapshot.getString("Tripod") + " , " + tripods.getSelectedItem().toString() + " at " + currentTime);
                map.put("SD Cards", documentSnapshot.getString("SD Cards") + " , " + SDCards.getSelectedItem().toString() + " at " + currentTime);
                map.put("Accessories", documentSnapshot.getString("Accessories") + " , " + accessories.getSelectedItem().toString() + " at " + currentTime);

                MainActivity.db.collection("Arena 2k18")
                        .document(MainActivity.currentUser.getDisplayName())
                        .update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.w("Submitted", "Yes");
                                Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                camera.setSelection(0);
                                lens.setSelection(0);
                                tripods.setSelection(0);
                                SDCards.setSelection(0);
                                accessories.setSelection(0);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Log.w("Submitted", "No");
                                Toast.makeText(context, "Update Failed, please check network", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }


}
