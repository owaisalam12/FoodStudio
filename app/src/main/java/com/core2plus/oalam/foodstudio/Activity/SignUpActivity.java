package com.core2plus.oalam.foodstudio.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.core2plus.oalam.foodstudio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone, editTextPassword, editTextSelectImage;
    private CountryCodePicker ccp;
    private Button buttonSendCode;
    private Uri filePath;
    public static Uri SfileURI;
    public static Bitmap Sbitmap;

    public Bitmap getBitmap() {
        return Sbitmap;
    }

    public Uri getFileURI() {
        return SfileURI;
    }

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextName = findViewById(R.id.input_name);
        editTextEmail = findViewById(R.id.input_email);
        editTextPhone = findViewById(R.id.input_phone);
        editTextPassword = findViewById(R.id.input_password);
//        editTextSelectImage = findViewById(R.id.input_selectImage);
        buttonSendCode = findViewById(R.id.sendCodeButton);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editTextPhone);
        ccp.setNumberAutoFormattingEnabled(true);

//        editTextSelectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //chooseImage();
//            }
//        });
        buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mobile = editTextPhone.getText().toString().trim();
                final String email = editTextEmail.getText().toString().trim();
                final String name = editTextName.getText().toString().trim();
                final String pass = editTextPassword.getText().toString().trim();

                if (!validateEditText(editTextName, "Name is required")) {
                    return;
                }
                if (!validateEmail(email)) {
                    return;
                }
                if (!validateEditText(editTextPassword, "Password is required")) {
                    return;
                }
                if (mobile.isEmpty()) {
                    editTextPhone.setError("Mobile number is required");
                    editTextPhone.requestFocus();
                    return;
                }
                if (!ccp.isValidFullNumber()) {
                    editTextPhone.setError("Enter a valid mobile");
                    editTextPhone.requestFocus();
                    return;
                }
                String number = ccp.getFullNumberWithPlus();
                Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                intent.putExtra("mobile", number);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("pass", pass);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
//        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
//            if (data != null && data.getExtras() != null) {
//
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            SfileURI = filePath;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //imageView.setImageBitmap(bitmap);
                Sbitmap = bitmap;
                editTextSelectImage.setText(bitmap.toString());
                //startActivity(new Intent(this, SendPrescriptionActivity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private boolean validateEditText(EditText editText, String msg) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(msg);
            editText.requestFocus();

            return false;
        } else {
        }

        return true;
    }

    private boolean validateEmail(String email) {

        if (editTextEmail.getText().toString().trim().isEmpty()) {
            //inputLayoutEmail.setError("Enter your email");
            editTextEmail.setError("Enter your email");
            editTextEmail.requestFocus();
            return false;
        } else {
            // inputLayoutEmail.setErrorEnabled(false);

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //inputLayoutEmail.setError("Valid email is required");
            editTextEmail.setError("Valid email is required");
            editTextEmail.requestFocus();
            return false;
        } else {
            //inputLayoutName.setErrorEnabled(false);
        }
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
