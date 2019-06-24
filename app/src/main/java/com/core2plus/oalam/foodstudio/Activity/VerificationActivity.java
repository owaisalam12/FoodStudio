package com.core2plus.oalam.foodstudio.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.InsertResponse;
import com.core2plus.oalam.foodstudio.Entity.UserData;
import com.core2plus.oalam.foodstudio.R;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {
    private String verificationId;
    private Pinview pinView;
    AlertDialog dialog;
    private String imagePath;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private Button btnVerifyCode;
    TextView textViewResendCode;
    private String mobile, name, email, pass;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    private SharedPreferences sharedpreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mAuth = FirebaseAuth.getInstance();
        pinView = findViewById(R.id.pinview);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        textViewResendCode = findViewById(R.id.resendCode);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .setTheme(R.style.Custom)
                .build();
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        pass = intent.getStringExtra("pass");

        sendVerificationCode(mobile);

        textViewResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mobile, mResendToken);
                Toast.makeText(VerificationActivity.this, "Code has been resent.", Toast.LENGTH_SHORT).show();
            }
        });

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String code = pinView.getValue().trim();
                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Please Enter Code", Toast.LENGTH_SHORT).show();
                    pinView.requestFocus();
                    return;
                }
                dialog.show();
                //signinUser(email,pass);
                verifyVerificationCode(code);
                //verifying the activity code
//                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
//                                isVerified=true;
//                                verifyVerificationCode(code);
//                            }else{
//                                dialog.dismiss();
//                                // Toast.makeText(PhoneVerifyActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
//                                dialogError("Error","Please verify your email first.");
//
//                            }
//
//
//                        }else{
//                            dialog.dismiss();
//
//                            //Toast.makeText(PhoneVerifyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            dialogError("Something went wrong",task.getException().getMessage());
//
//
//                        }
//                    }
//                });


//                if(isVerified){
//                    verifyVerificationCode(code);
//
//                }


                //verifying the code entered manually
            }
        });


    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            final String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code

            if (code != null) {
                pinView.setValue(code);
                //verifying the code
//                verifyVerificationCode(code);


            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            mResendToken = forceResendingToken;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            //signing the user
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            dialog.dismiss();
                            addUserData();
                            addUserDatamySql();

                            Toast.makeText(VerificationActivity.this, "Successfull!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VerificationActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            dialog.dismiss();
                            //Toast.makeText(PhoneVerifyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dialogError("Something went wrong", task.getException().getMessage());

                        }
                    }
                });
    }

    private void dialogError(String title, String msg) {

        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog

                .setIcon(R.drawable.pdlg_icon_close)
                .setIconTint(R.color.pdlg_color_red)
                .setAnimationEnabled(false)
                .setTitle(title)
                .setMessage(msg)

                .addButton(
                        "OK",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        }
                )
                .show();
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private void addUserData() {
        //uploadImage();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/");
        String usermobile = mobile;
        String username = name;
        String useremail = email;
        String userpass = pass;
        //String imageUrl=imagePath;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("token", "GetTokenResult result 1 = " + userId);

            if ((usermobile != null && !usermobile.isEmpty() && !usermobile.equals("null")) && (username != null && !username.isEmpty() && !username.equals("null")) && (userId != null && !userId.isEmpty() && !userId.equals("null")) && (useremail != null && !useremail.isEmpty() && !useremail.equals("null")) && (userpass != null && !userpass.isEmpty() && !userpass.equals("null"))) {
                UserData userData = new UserData(username, useremail, userpass, usermobile);
                myRef.child(userId).setValue(userData);
            }

        } else {

        }
    }

    private void addUserDatamySql() {
        //uploadImage();

        String usermobile = mobile;
        String username = name;
        String useremail = email;
        String userpass = pass;
        //String imageUrl=imagePath;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if ((usermobile != null && !usermobile.isEmpty() && !usermobile.equals("null")) && (username != null && !username.isEmpty() && !username.equals("null")) && (userId != null && !userId.isEmpty() && !userId.equals("null")) && (useremail != null && !useremail.isEmpty() && !useremail.equals("null")) && (userpass != null && !userpass.isEmpty() && !userpass.equals("null"))) {
                Date dt = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                String createdTime = dateFormat.format(dt);
                //createdTime = DateFormat.getDateTimeInstance().format("yyyy-MM-dd kk:mm:ss");
                Log.v("verify", userId);
                Log.v("verify", username);
                Log.v("verify", userpass);
                Log.v("verify", useremail);
                Log.v("verify", usermobile);
                Log.v("verify", createdTime);
                Call<InsertResponse> call = RetrofitClient.getInstance().getApi().insertdata(userId, username, useremail, userpass, usermobile, createdTime);
                call.enqueue(new Callback<InsertResponse>() {
                    @Override
                    public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                        InsertResponse insertResponse = response.body();
                        if (insertResponse.getSuccess() != 0) {
                            Toast.makeText(VerificationActivity.this, insertResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VerificationActivity.this, insertResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<InsertResponse> call, Throwable t) {

                    }
                });
            }

        } else {

        }
    }

    private Uri filePath = new SignUpActivity().getFileURI();

    private void uploadImage() {

        if (filePath != null) {
            //  final ProgressDialog progressDialog = new ProgressDialog(this);
            //    progressDialog.setTitle("Uploading...");
            //   progressDialog.setCancelable(false);
            // progressDialog.show();

            ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //  progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String img = uri.toString();
                                    imagePath = img;
//                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                                    Log.i("seeThisUri", ""+filePath);// This is the one you should store
//
//                                    ref.child("imageURL").setValue(img);

                                }
                            });

                            Toast.makeText(VerificationActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //   progressDialog.dismiss();
                            Toast.makeText(VerificationActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    String img = task.getResult().toString();
                    String a = ref.getDownloadUrl().toString();


                    // DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Image").child(mAuth.getCurrentUser().getUid());

                    // Log.i("seeThisUri", URL);// This is the one you should store

                    // ref.child("imageURL").setValue(a);

                }
            })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            //    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });


        }
        //  sendUrl();
    }
}
