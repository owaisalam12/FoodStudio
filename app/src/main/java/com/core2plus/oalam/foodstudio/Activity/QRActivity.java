package com.core2plus.oalam.foodstudio.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.InsertResponse;
import com.core2plus.oalam.foodstudio.Entity.History;
import com.core2plus.oalam.foodstudio.Entity.PurchaseData;
import com.core2plus.oalam.foodstudio.R;
import com.core2plus.oalam.foodstudio.SQLite.ORM.HistoryORM;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.text.DateFormat;

import butterknife.BindView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    // Init ui elements
    @BindView(R.id.lightButton)
    ImageView flashImageView;
    //Variables
    Intent i;
    HistoryORM h = new HistoryORM();
    private ZXingScannerView mScannerView;
    private boolean flashState = false;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String purchaseItemG, purchaseTimeG, blockTimeG;
    private static boolean qrScanned = false;

    public boolean getQrScanned() {
        return qrScanned;
    }

    public boolean setQrScanned(Boolean qr) {
        return qrScanned = qr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        setQrScanned(false);
        ActivityCompat.requestPermissions(QRActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

    }

    @Override
    public void handleResult(final Result rawResult) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences prefs = getSharedPreferences("time", Context.MODE_PRIVATE);
        long blocktime = prefs.getLong("time", 0);

        // adding result to history
        String mydate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        }
        purchaseItemG = rawResult.getText();
        purchaseTimeG = mydate;
        blockTimeG = String.valueOf(blocktime);

        //send purchase to firebase
        sendPurchaseToFirebase();
        // TODO: 24-Apr-19 send data to mysql
        Call<InsertResponse> call = RetrofitClient.getInstance().getApi().insertpurchase(userId, rawResult.getText(), getText(rawResult.getText()), getImgURl(rawResult.getText()), getSuggestedPrice(rawResult.getText()), mydate, String.valueOf(blocktime));
        call.enqueue(new Callback<InsertResponse>() {
            @Override
            public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                InsertResponse insertResponse = response.body();
                if (insertResponse.getSuccess() != 0) {
                    qrScanned = true;
                    Toast.makeText(QRActivity.this, insertResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    qrScanned = false;
                    Toast.makeText(QRActivity.this, insertResponse.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<InsertResponse> call, Throwable t) {

            }
        });


        History history = new History();
        history.setContext(rawResult.getText());
        history.setDate(mydate);
        h.add(getApplicationContext(), history);
        // show custom alert dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        View v = dialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        TextView text = dialog.findViewById(R.id.someText);
        text.setText(rawResult.getText());
        ImageView img = dialog.findViewById(R.id.imgOfDialog);
        img.setImageResource(R.drawable.ic_done_gr);

        Button webSearch = dialog.findViewById(R.id.searchButton);
        Button copy = dialog.findViewById(R.id.copyButton);
        Button share = dialog.findViewById(R.id.shareButton);
        webSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                if (Patterns.WEB_URL.matcher(rawResult.getText()).matches()) {
                    url = rawResult.getText();
                } else {
                    url = "http://www.google.com/#q=" + rawResult.getText();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                dialog.dismiss();
                mScannerView.resumeCameraPreview(QRActivity.this);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", rawResult.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                mScannerView.resumeCameraPreview(QRActivity.this);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = rawResult.getText();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share "));

                dialog.dismiss();
                mScannerView.resumeCameraPreview(QRActivity.this);
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mScannerView.resumeCameraPreview(QRActivity.this);
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(QRActivity.this, "Permission denied to camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void mainActivityOnClickEvents(View v) {

        switch (v.getId()) {
            case R.id.historyButton:
                i = new Intent(this, HistoryActivity.class);
                startActivity(i);
                break;
            case R.id.lightButton:
                if (flashState == false) {
                    v.setBackgroundResource(R.drawable.ic_flash_off);
                    Toast.makeText(getApplicationContext(), "Flashlight turned on", Toast.LENGTH_SHORT).show();
                    mScannerView.setFlash(true);
                    flashState = true;
                } else if (flashState) {
                    v.setBackgroundResource(R.drawable.ic_flash_on);
                    Toast.makeText(getApplicationContext(), "Flashlight turned off", Toast.LENGTH_SHORT).show();
                    mScannerView.setFlash(false);
                    flashState = false;
                }
                break;
        }

    }

    private void sendPurchaseToFirebase() {

        //uploadImage();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Purchases/");
        String purchaseItem = purchaseItemG;
        String purchaseTime = purchaseTimeG;
        String blockTime = blockTimeG;

        //String imageUrl=imagePath;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if ((purchaseItem != null && !purchaseItem.isEmpty() && !purchaseItem.equals("null")) && (purchaseTime != null && !purchaseTime.isEmpty() && !purchaseTime.equals("null")) && (blockTime != null && !blockTime.isEmpty() && !blockTime.equals("null"))) {
                Log.d("token", "GetTokenResult result 1 = " + userId);
                PurchaseData purchaseData = new PurchaseData(userId, purchaseItem, purchaseTime, blockTime);
                myRef.child(userId).setValue(purchaseData);
            }

        } else {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public String getImgURl(String rawText) {

        String text = rawText;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[0] != null) {
            //System.out.println(arr[1]);
            return arr[0];
        } else {
            return "Error occured!";
        }
    }

    public String getText(String rawText) {

        String text = rawText;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[1] != null) {
            //System.out.println(arr[1]);
            return arr[1];
        } else {
            return "Error occured!";
        }

    }

    public String getSuggestedPrice(String rawText) {

        String text = rawText;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[2] != null) {
            //System.out.println(arr[1]);
            return arr[2];
        } else {
            return "0";
        }

    }
}
