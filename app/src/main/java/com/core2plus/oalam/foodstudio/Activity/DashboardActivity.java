package com.core2plus.oalam.foodstudio.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.ProfImgResponse;
import com.core2plus.oalam.foodstudio.Entity.Constants;
import com.core2plus.oalam.foodstudio.Entity.UserData;
import com.core2plus.oalam.foodstudio.Fragment.AboutUsFragment;
import com.core2plus.oalam.foodstudio.Fragment.DonateFragment;
import com.core2plus.oalam.foodstudio.Fragment.HomeFragment;
import com.core2plus.oalam.foodstudio.Fragment.PrivacyPolicyFragment;
import com.core2plus.oalam.foodstudio.Fragment.ProfileFragment;
import com.core2plus.oalam.foodstudio.Fragment.PurchasesFragment;
import com.core2plus.oalam.foodstudio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.model.HelpLiveo;
import br.liveo.navigationliveo.NavigationLiveo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends NavigationLiveo implements OnItemClickListener {
    private HelpLiveo mHelpLiveo;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String name2, email2;
    private SharedPreferences sharedpreferences;
    private SharedPreferences sharedpreferences2;
    // TODO: 24-Jun-19 url
//    private String Img_URL = "http://192.168.137.1/food/assets/images/users/";
    private String Img_URL = Constants.Img_URL_Users;
    private String userimg;
    ImageLoader imageLoader;

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("resume", "resume called");
    }

    @Override
    public void onInt(Bundle savedInstanceState) {

        sharedpreferences2 = this.getSharedPreferences("sidebar", Context.MODE_PRIVATE);
        String name = sharedpreferences2.getString("name", null);
        String email = sharedpreferences2.getString("email", null);
        String userimg = sharedpreferences2.getString("userimg", null);
        imageLoader = ImageLoader.getInstance();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(DashboardActivity.this));

//        if (name != null && email != null && userimg != null) {
//            this.userName.setText(name);
//            this.userEmail.setText(email);
//            setSideImage(userimg);
//        } else {
//            getUserData();
//            getUserImg();
//            }

        getUserData();
        getUserImg();

//        }

        //this.userPhoto.setImageResource(R.drawable.prof);

        this.userBackground.setImageResource(R.drawable.ic_user_background_second);
        firebaseAuth = FirebaseAuth.getInstance();

        sharedpreferences = this.getSharedPreferences("drawer", Context.MODE_PRIVATE);
//        this.getSupportActionBar().setTitle("Dashboard");
        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add(getString(R.string.home), R.drawable.ic_home);
        mHelpLiveo.add(getString(R.string.profile), R.drawable.ic_person);
        //mHelpLiveo.addSubHeader(getString(R.string.categories)); //Item subHeader
        mHelpLiveo.add(getString(R.string.purchases), R.drawable.ic_purchase);
        mHelpLiveo.add(getString(R.string.donate_us), R.drawable.donate_icon);
        mHelpLiveo.addSeparator(); // Item separator
        mHelpLiveo.add(getString(R.string.aboutus));
        mHelpLiveo.add(getString(R.string.privacy_policy));

        //with(this, Navigation.THEME_DARK). add theme dark
        //with(this, Navigation.THEME_LIGHT). add theme light

        with(this) // default theme is dark
                .startingPosition(0) //Starting position in the list
                .addAllHelpItem(mHelpLiveo.getHelp())
                //.footerItem(R.string.logout)
                .footerItem(R.string.logout, R.drawable.ic_logout)

                .setOnClickUser(onClickPhoto)
                .setOnPrepareOptionsMenu(onPrepare)
                .setOnClickFooter(onClickFooter)
                .build();
    }


    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                goToFragment(new HomeFragment());
                break;
            case 1:
                goToFragment(new ProfileFragment());
                break;
            case 2:
                goToFragment(new PurchasesFragment());
                break;
            case 3:
                goToFragment(new DonateFragment());
                break;
            case 5:
                goToFragment(new AboutUsFragment());
                break;
            case 6:
                goToFragment(new PrivacyPolicyFragment());
                break;
            default:
                break;
        }

    }

    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {

        }
    };
    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeDrawer();
        }
    };
    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //closeDrawer();
            firebaseAuth.signOut();
            SharedPreferences preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
            preferences.edit().clear().commit();
            sharedpreferences2 = getSharedPreferences("sidebar", Context.MODE_PRIVATE);
            sharedpreferences2.edit().clear().commit();
            startActivity(new Intent(DashboardActivity.this, SignUpActivity.class));

        }
    };

    private void goToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void getUserData() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData dbUserData = dataSnapshot.getValue(UserData.class);
                if ((dbUserData.getUserName() != null && !dbUserData.getUserName().isEmpty() && !dbUserData.getUserName().equals("null")) && (dbUserData.getUserEmail() != null && !dbUserData.getUserEmail().isEmpty() && !dbUserData.getUserEmail().equals("null")) && (dbUserData.getUserMobile() != null && !dbUserData.getUserMobile().isEmpty() && !dbUserData.getUserMobile().equals("null"))) {
                    name2 = dbUserData.getUserName();
                    email2 = dbUserData.getUserEmail();
                    Log.v("profile2", name2);
                    Log.v("profile2", email2);
                    setSide(dbUserData.getUserName(), dbUserData.getUserEmail());
                    saveToSP(dbUserData.getUserName(), dbUserData.getUserEmail());

                    Log.v("profile", dbUserData.getUserName());
                    Log.v("profile", dbUserData.getUserEmail());
                    Log.v("profile", dbUserData.getUserMobile());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void saveToSP(String name, String email) {
        SharedPreferences.Editor editor = sharedpreferences2.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.commit();


    }

    public void getUserImg() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Call<ProfImgResponse> call = RetrofitClient.getInstance().getApi().getUserImg(userId);
        call.enqueue(new Callback<ProfImgResponse>() {
            @Override
            public void onResponse(Call<ProfImgResponse> call, Response<ProfImgResponse> response) {
                ProfImgResponse profImgResponse = response.body();
                //setImageView(profImgResponse.getProfImgs().get(0).getImg());
                if(profImgResponse.getSuccess()!=0){
                    userimg = Img_URL + profImgResponse.getProfImgs().get(0).getImg();
                    //imageView.setImageURI(Uri.parse(userimg));

                    setSideImage(userimg);
                    SharedPreferences.Editor editor = sharedpreferences2.edit();
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

    private void setSide(String name, String email) {
        this.userName.setText(name);
        this.userEmail.setText(email);
    }

    private void setSideImage(String img) {
        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(img, this.userPhoto);


    }
}
