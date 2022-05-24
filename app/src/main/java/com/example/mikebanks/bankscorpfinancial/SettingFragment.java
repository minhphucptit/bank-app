package com.example.mikebanks.bankscorpfinancial;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SettingFragment extends Fragment {
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtCountry;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;

    SharedPreferences userPreferences;
    Gson gson;
    String json;
    Profile userProfile;
    long profileId=0;


    public SettingFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Update Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_profile, container, false);

        edtFirstName = rootView.findViewById(R.id.edt_first_name);
        edtLastName = rootView.findViewById(R.id.edt_last_name);
        edtCountry = rootView.findViewById(R.id.edt_country);
        edtUsername = rootView.findViewById(R.id.edt_username);
        edtPassword = rootView.findViewById(R.id.edt_password);
        edtPasswordConfirm = rootView.findViewById(R.id.edt_password_confirm);

        setValues();
        Button btnCreateAccount = rootView.findViewById(R.id.btn_create_account);
        btnCreateAccount.setText("Update profile");
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        return rootView;
    }

    private void setValues() {

        userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);

        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        edtFirstName.setText(userProfile.getFirstName());
        edtLastName.setText(userProfile.getLastName());
        edtCountry.setText(userProfile.getCountry());
        edtUsername.setText(userProfile.getUsername());
        edtPassword.setText(userProfile.getPassword());
        profileId = userProfile.getDbId();
        edtPasswordConfirm.setText("");

    }


    private void updateProfile() {

        ApplicationDB applicationDb = new ApplicationDB( getActivity().getApplicationContext());
        ArrayList<Profile> profiles = applicationDb.getAllProfiles();
        boolean usernameTaken = false;

        for (int iProfile = 0; iProfile < profiles.size(); iProfile++) {
            if (edtUsername.getText().toString().equals(profiles.get(iProfile).getUsername())) {
                usernameTaken = true;
            }
        }

        if (edtFirstName.getText().toString().equals("") || edtLastName.getText().toString().equals("") || edtCountry.getText().toString().equals("") ||
                edtUsername.getText().toString().equals("") || edtPassword.getText().toString().equals("") || edtPasswordConfirm.getText().toString().equals("")) {
            Toast.makeText(getActivity(), R.string.fields_blank, Toast.LENGTH_SHORT).show();
        }

        else if (!(edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString()))) {
            Toast.makeText(getActivity(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
        }
        else if (usernameTaken) {
            Toast.makeText(getActivity(), "A User has already taken that username", Toast.LENGTH_SHORT).show();
        }
        else {
            Profile userProfile = new Profile(edtFirstName.getText().toString(), edtLastName.getText().toString(), edtCountry.getText().toString(),
                    edtUsername.getText().toString(), edtPassword.getText().toString());

            userProfile.setDbId(profileId);
            applicationDb.overwriteProfile(userProfile);

            Bundle bundle = new Bundle();
            bundle.putString("Username", userProfile.getUsername());
            bundle.putString("Password", userProfile.getPassword());

            ((DrawerActivity) getActivity()).profileCreated(bundle);

        }
    }

}
