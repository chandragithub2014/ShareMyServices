package com.hyd.bikepool.bikepooler.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.slidingmenu.SlidingMenuActivity;
import com.hyd.bikepool.bikepooler.utils.EmailValidator;
import com.hyd.bikepool.bikepooler.utils.PasswordValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int mContainerId = -1;
    LinearLayout loginLayout;
    EditText userName,passWord,retypePassword;
    Button signUp;

    private EmailValidator emailValidator;
    private PasswordValidator passwordValidator;
    SharedPreferencesUtils prefs;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
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
        emailValidator = new EmailValidator();
        passwordValidator = new PasswordValidator();
        prefs = new SharedPreferencesUtils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainerId = container.getId();
        View view =  inflater.inflate(R.layout.signup_layout, container, false);
        initViews(view);
        return  view;
    }


    private void initViews(View view){
        loginLayout = (LinearLayout)view.findViewById(R.id.signup_pooler);
        userName  = (EditText)loginLayout.findViewById(R.id.register_email);
        passWord = (EditText)loginLayout.findViewById(R.id.register_pwd);
        retypePassword = (EditText)loginLayout.findViewById(R.id.retype_pwd);
        signUp = (Button)loginLayout.findViewById(R.id.signup);
        signUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.signup:
              if(!TextUtils.isEmpty(userName.getText().toString())){
             boolean isEmailValid = validateEmail(userName.getText().toString());

                  if(isEmailValid){

                      if(!TextUtils.isEmpty(passWord.getText().toString())){
                            //Validate Password
                          String passWordToValidate =  passWord.getText().toString();
                          if(passWordToValidate.length()<6 || passWordToValidate.length()>20){
                            Toast.makeText(getActivity(),"Password must be  between 6 to 20 characters",Toast.LENGTH_LONG).show();
                          }else {
                              boolean isPassValid = validatePassword(passWord.getText().toString());
                              if(isPassValid){
                                      if(!TextUtils.isEmpty(retypePassword.getText().toString())){
                                          String password = passWord.getText().toString();
                                          String reTypePassword = retypePassword.getText().toString();
                                          if(password.equalsIgnoreCase(reTypePassword)){
                                              saveInSharedPreferences(userName.getText().toString(),password);
                                          }else{
                                              Toast.makeText(getActivity(),"Password not matched",Toast.LENGTH_LONG).show();
                                          }
                                      }else{
                                          Toast.makeText(getActivity(),"Re Type Password cannot be empty",Toast.LENGTH_LONG).show();
                                      }
                              }else{
                                  Toast.makeText(getActivity(),"Password must have atleast a lowercase,uppercase,digit and special symbol @#$% ",Toast.LENGTH_LONG).show();
                              }

                          }
                      }else{
                          Toast.makeText(getActivity(),"Password cannot be empty",Toast.LENGTH_LONG).show();
                      }
                  }else{
                      Toast.makeText(getActivity(),"Invalid Email",Toast.LENGTH_LONG).show();
                  }
              }else{
                  Toast.makeText(getActivity(),"Email cannot be empty",Toast.LENGTH_LONG).show();
              }



             /* if(!TextUtils.isEmpty(retypePassword.getText().toString())){

              }else{
                  Toast.makeText(getActivity(),"Re Type Password cannot be empty",Toast.LENGTH_LONG).show();
              }*/
              break;
      }
    }

    private boolean validateEmail(String email) {
        boolean isEmailValid  = emailValidator.validate(email);
        return isEmailValid;
    }


    private boolean validatePassword(String password){
        boolean isValidPwd = passwordValidator.validate(password);
        return isValidPwd;
    }

    private void saveInSharedPreferences(String email,String password){

        JSONObject emailPwdJson = new JSONObject();
        try {
            emailPwdJson.put("emailId", email);
            emailPwdJson.put("password", password);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        prefs.saveStringPreferences(getActivity(), "email", emailPwdJson.toString());
        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_LONG).show();
        prefs.saveStringPreferences(getActivity(), "loginType", "emailProfile");
       /* if(!TextUtils.isEmpty(prefs.getStringPreferences(getActivity(),"emailProfile"))){

            Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(i);
            getActivity().finish();
        }else{
            getFragmentManager().beginTransaction()
                    .replace(mContainerId,  ProfileFragment.newInstance("email", ""))
                    .commit();
        }
*/

        Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
