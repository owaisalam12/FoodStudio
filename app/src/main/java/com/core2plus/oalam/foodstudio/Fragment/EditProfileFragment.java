package com.core2plus.oalam.foodstudio.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private final int PICK_IMAGE_REQUEST = 71;
    private CircleImageView circleImageView;
    private Toolbar toolbar;
    private Bitmap bitmap;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        setHasOptionsMenu(true);
        // circleImageView = view.findViewById(R.id.editProfileImgage);
        //toolbar=view.findViewById(R.id.my_toolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Edit Profile");
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();

            }
        });
        return view;
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                circleImageView.setImageBitmap(bitmap);
                //Sbitmap = bitmap;
                //startActivity(new Intent(this, SendPrescriptionActivity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }

//        }else if(requestCode == REQUEST_CAPTURE_IMAGE){
//            //don't compare the data to null, it will always come as  null because we are providing a file URI, so load with the imageFilePath we obtained before opening the cameraIntent
//            if (resultCode == RESULT_OK) {
//                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//                Sbitmap=imageBitmap;
//                Uri uri =Uri.fromFile(new File(imageFilePath));
//
//
//                //Toast.makeText(this, "path: "+uri, Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this,SendPrescriptionActivity.class));
//            }
//            else if(resultCode == Activity.RESULT_CANCELED) {
//                // User Cancelled the action
//
//            }
//
//
//        }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mymenu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    Bundle bun = new Bundle();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveAction) {
            if (bitmap != null) {
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                bun.putByteArray("image", byteArray);
                Toast.makeText(getActivity(), "Saved: " + bitmap, Toast.LENGTH_SHORT).show();
                goToFragment(new ProfileFragment(), bun);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }
}

