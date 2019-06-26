package com.core2plus.oalam.foodstudio.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.InsertResponse;
import com.core2plus.oalam.foodstudio.API.ProfImgResponse;
import com.core2plus.oalam.foodstudio.API.PurchasedAmountResponse;
import com.core2plus.oalam.foodstudio.Entity.Constants;
import com.core2plus.oalam.foodstudio.Entity.UserData;
import com.core2plus.oalam.foodstudio.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private TextView editProfile, profileNameTextView, profileEmailTextView, profilePhoneTextView, profileNameMainTextView, profilePurchasedAmountTextView;
    ImageView imageViewProfile;
    private ImageView imageView;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedpreferences;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    private Uri filePath;
    Bitmap bitmap;
    public static Uri SfileURI;
    public static Bitmap Sbitmap;

    public Bitmap getBitmap() {
        return Sbitmap;
    }

    public Uri getFileURI() {
        return SfileURI;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: 24-Jun-19 url
//    private String Img_URL="http://192.168.137.1/food/assets/images/users/";
    private String Img_URL = Constants.Img_URL_Users;
    private String userimg;
    ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        editProfile = view.findViewById(R.id.editProfile);
        imageLoader = ImageLoader.getInstance();
        imageViewProfile = view.findViewById(R.id.editProfileImage);
        imageView = view.findViewById(R.id.profileImage);
        profileNameTextView = view.findViewById(R.id.profileName);
        profileEmailTextView = view.findViewById(R.id.profileEmail);
        profilePhoneTextView = view.findViewById(R.id.profilePhone);
        profileNameMainTextView = view.findViewById(R.id.profileNameMain);
        profilePurchasedAmountTextView = view.findViewById(R.id.purchasedAmount);
        //sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedpreferences = getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
        String name = sharedpreferences.getString("name", null);
        String email = sharedpreferences.getString("email", null);
        String mobile = sharedpreferences.getString("mobile", null);
        final String userimg = sharedpreferences.getString("userimg", null);
        getUserPurchasedAmount();
        if (name != null && email != null && mobile != null && userimg != null) {

            profileNameMainTextView.setText(name);
            profileNameTextView.setText(name);
            profileEmailTextView.setText(email);
            profilePhoneTextView.setText(mobile);
            imageLoader.displayImage(userimg, imageView);
            Log.v("userimg", userimg);
        } else {
            getUserData();
            getUserImg();

        }
//        Bitmap bmp;
//        if (getArguments() != null) {
//            byte[] byteArray = getArguments().getByteArray("image");
//            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            if (bmp != null) {
//                setImageView(bmp);
//            }
//        } else {
//        }


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToFragment(new EditProfileFragment());
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.fragment_edit_profile, null);
                dialogBuilder.setView(dialogView);
                final EditText editTextName = dialogView.findViewById(R.id.profile_editName);
                final EditText editTextEmail = dialogView.findViewById(R.id.profile_editEmail);
                final EditText editTextPhone = dialogView.findViewById(R.id.profile_editMobile);
                imageViewProfile = dialogView.findViewById(R.id.editProfileImage);
                final Button buttonSave = dialogView.findViewById(R.id.profile_editSaveBtn);
                final Button buttonExit = dialogView.findViewById(R.id.profile_editExitBtn);
                final CountryCodePicker ccp = dialogView.findViewById(R.id.ccp);


                ccp.registerCarrierNumberEditText(editTextPhone);
                ccp.setNumberAutoFormattingEnabled(true);
                dialogBuilder.setCancelable(false);
                editTextName.setText(profileNameTextView.getText().toString());
                editTextEmail.setText(profileEmailTextView.getText().toString());
                editTextPhone.setText(profilePhoneTextView.getText().toString());
                imageLoader.displayImage(userimg, imageViewProfile);

                final AlertDialog b = dialogBuilder.create();
                b.show();
                imageViewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                });
                buttonExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b.dismiss();
                    }
                });
                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editTextName.getText().toString();
                        String email = editTextEmail.getText().toString();
                        String mobile = editTextPhone.getText().toString();
                        if (!validateEditText(editTextName, "Name is required")) {
                            return;
                        }
                        if (!validateEmail(editTextEmail, email)) {
                            return;
                        }
                        if (mobile.isEmpty()) {
                            editTextPhone.setError("Mobile number is required");
                            editTextPhone.requestFocus();
                            return;
                        }

                        Log.v("check", name);
                        Log.v("check", email);
                        Log.v("check", mobile);
                        updateUser(name, email, mobile);
                        uploadImage();
                        getUserData();
                        getUserImg();
                        // new DashboardActivity().getUserData();
                        //new DashboardActivity().getUserImg();
                        b.dismiss();

                    }
                });

            }
        });

        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private String convertToString() {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imgByte = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgByte, Base64.DEFAULT);
        }

        return null;
    }

    private boolean updateUser(String name, String email, String mobile) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        UserData userData = new UserData(name, email, mobile);
        databaseReference.setValue(userData);
        updateUserMySQL(userId, name, email, mobile);
        Toast.makeText(getContext(), "Profile Updated! ", Toast.LENGTH_SHORT).show();

        return true;

    }

    private void updateUserMySQL(String userid, String name, String email, String phone) {
        Call<InsertResponse> call = RetrofitClient.getInstance().getApi().updateUser(userid, name, email, phone);
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                InsertResponse insertResponse = response.body();
                if (insertResponse.getSuccess() != 0) {
                    Toast.makeText(getContext(), insertResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), insertResponse.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {

            }
        });
    }

    public void setImageView(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private void goToFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    private void getUserData() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData dbUserData = dataSnapshot.getValue(UserData.class);

                if ((dbUserData.getUserName() != null && !dbUserData.getUserName().isEmpty() && !dbUserData.getUserName().equals("null")) && (dbUserData.getUserEmail() != null && !dbUserData.getUserEmail().isEmpty() && !dbUserData.getUserEmail().equals("null")) && (dbUserData.getUserMobile() != null && !dbUserData.getUserMobile().isEmpty() && !dbUserData.getUserMobile().equals("null"))) {
                    profileNameMainTextView.setText(dbUserData.getUserName());
                    profileNameTextView.setText(dbUserData.getUserName());
                    profileEmailTextView.setText(dbUserData.getUserEmail());
                    profilePhoneTextView.setText(dbUserData.getUserMobile());
                    saveToSP();
                    Log.v("profileF", dbUserData.getUserName());
                    Log.v("profileF", dbUserData.getUserEmail());
                    Log.v("profileF", dbUserData.getUserMobile());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserImg() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Call<ProfImgResponse> call = RetrofitClient.getInstance().getApi().getUserImg(userId);
        call.enqueue(new Callback<ProfImgResponse>() {
            @Override
            public void onResponse(Call<ProfImgResponse> call, Response<ProfImgResponse> response) {
                ProfImgResponse profImgResponse = response.body();
                //setImageView(profImgResponse.getProfImgs().get(0).getImg());
                if (profImgResponse.getSuccess() != 0) {
                    userimg = Img_URL + profImgResponse.getProfImgs().get(0).getImg();
                    //imageView.setImageURI(Uri.parse(userimg));
                    imageLoader.displayImage(userimg, imageView);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("userimg", userimg);

                    editor.commit();
                    Log.v("profuri", userimg);
                }

            }

            @Override
            public void onFailure(Call<ProfImgResponse> call, Throwable t) {

            }
        });
    }

    private void getUserPurchasedAmount() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Call<PurchasedAmountResponse> call = RetrofitClient.getInstance().getApi().getUserPurchasedAmount(userId);
        call.enqueue(new Callback<PurchasedAmountResponse>() {
            @Override
            public void onResponse(Call<PurchasedAmountResponse> call, Response<PurchasedAmountResponse> response) {
                PurchasedAmountResponse purchasedAmountResponse = response.body();
                if (purchasedAmountResponse.getSuccess() != 0) {
                    // profilePurchasedAmountTextView.setText("Rs "+purchasedAmountResponse.getPurchasedAmount());
                    profilePurchasedAmountTextView.setText("Rs " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(Integer.parseInt(purchasedAmountResponse.getPurchasedAmount())));
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putString("userimg", userimg);
//
//                editor.commit();
                }
            }

            @Override
            public void onFailure(Call<PurchasedAmountResponse> call, Throwable t) {

            }
        });
    }

    private void getUserData2() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.v("profile", "userId: " + userId);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(userId)) {


                        UserData dbUserData = postSnapshot.getValue(UserData.class);

                        if ((dbUserData.getUserName() != null && !dbUserData.getUserName().isEmpty() && !dbUserData.getUserName().equals("null")) && (dbUserData.getUserEmail() != null && !dbUserData.getUserEmail().isEmpty() && !dbUserData.getUserEmail().equals("null")) && (dbUserData.getUserMobile() != null && !dbUserData.getUserMobile().isEmpty() && !dbUserData.getUserMobile().equals("null"))) {
                            profileNameMainTextView.setText(dbUserData.getUserName());
                            profileNameTextView.setText(dbUserData.getUserName());
                            profileEmailTextView.setText(dbUserData.getUserEmail());
                            profilePhoneTextView.setText(dbUserData.getUserMobile());


                            Log.v("profile", dbUserData.getUserName());
                            Log.v("profile", dbUserData.getUserEmail());
                            Log.v("profile", dbUserData.getUserMobile());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveToSP() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("name", profileNameTextView.getText().toString());
        editor.putString("email", profileEmailTextView.getText().toString());
        editor.putString("mobile", profilePhoneTextView.getText().toString());
        editor.commit();
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

    private boolean validateEmail(EditText editText, String email) {

        if (editText.getText().toString().trim().isEmpty()) {
            //inputLayoutEmail.setError("Enter your email");
            editText.setError("Enter your email");
            editText.requestFocus();
            return false;
        } else {
            // inputLayoutEmail.setErrorEnabled(false);

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //inputLayoutEmail.setError("Valid email is required");
            editText.setError("Valid email is required");
            editText.requestFocus();
            return false;
        } else {
            //inputLayoutName.setErrorEnabled(false);
        }
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null) {
//            filePath = data.getData();
//            SfileURI = filePath;
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
//                imageViewProfile.setImageBitmap(bitmap);
//                Log.v("bitmap",filePath.toString());
//                Log.v("bitmap",bitmap.toString());
//                //uploadImage();
//                Sbitmap = bitmap;
//                //startActivity(new Intent(this, SendPrescriptionActivity.class));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                Log.v("Imagebitmap", bitmap.toString());
                // imageLoader.displayImage(getImageUri(getContext(),bitmap),imageView);
                //imageLoader.displayImage(path.toString(),imageView);
                Log.v("Imagebitmap", path.toString());
                imageViewProfile.setImageBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage() {
        String image;
        if (convertToString() != null) {
            image = convertToString();
            //String image = "test";
            //String imageName = "test";
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Log.v("ImageuploadImage", userId);

            Call<InsertResponse> call = RetrofitClient.getInstance().getApi().uploadImage(userId, image);
            call.enqueue(new Callback<InsertResponse>() {
                @Override
                public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                    InsertResponse insertResponse = response.body();
                    if (insertResponse.getSuccess() != 0) {
                        Log.v("Image", insertResponse.getMessage());
                    } else {
                        Log.v("Image", insertResponse.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<InsertResponse> call, Throwable t) {
                    Log.v("ImageF", t.getMessage().toString());

                }
            });

        }


    }

    private void uploadImage2() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String img = uri.toString();

                                    //imagePath=img;
//                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
                                    Log.i("uri", "" + img);// This is the one you should store
//
//                                    ref.child("imageURL").setValue(img);

                                }
                            });

                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });


        }
        //  sendUrl();
    }
}
