package com.sldroids.scheduleintent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class OrderActivity extends AppCompatActivity {

    private int number, status;
    private double qty, rate, srcLat, srcLon, desLat, desLon;
    private String cname, contact, item, descript;
    public static final int IMAGE_CAPTURED_BEFORE = 100;
    public static final int IMAGE_CAPTURED_AFTER = 200;
    public static final int CUS_SIGN = 150;
    public static final int SAL_SIGN = 250;
    public static final String SIGN = "sign";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        number = getIntent().getIntExtra(MainActivity.ORDER_NO, 0);
        cname = getIntent().getStringExtra(MainActivity.CUSTOMER);
        item = getIntent().getStringExtra(MainActivity.FUEL_TYPE);
        qty = getIntent().getDoubleExtra(MainActivity.QUANTITY, 0);

        ((TextView)findViewById(R.id.txtOrderNo)).setText("Order No\t\t: #" +  String.valueOf(number));
        ((TextView)findViewById(R.id.txtCustomer)).setText("Customer\t: " + cname);
        ((TextView)findViewById(R.id.txtFuelType)).setText("Fuel Type\t: " + item);
        ((TextView)findViewById(R.id.txtQty)).setText("Quantity\t\t: " + String.valueOf(qty) + " L");

        ((ImageView)findViewById(R.id.imgBeforePNG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri exampleUri  = Uri.parse("file:///sdcard/" + String.valueOf(number) + "_before.jpg");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, exampleUri);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURED_BEFORE);
            }
        });

        ((ImageView)findViewById(R.id.imgAfterPNG)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri exampleUri  = Uri.parse("file:///sdcard/" + String.valueOf(number) + "_after.jpg");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, exampleUri);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURED_AFTER);
            }
        });

        ((ImageView)findViewById(R.id.imgCusSign)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderActivity.this.startActivityForResult(new Intent(OrderActivity.this, SignActivity.class), CUS_SIGN);
            }
        });

        ((ImageView)findViewById(R.id.imgSalerSign)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderActivity.this.startActivityForResult(new Intent(OrderActivity.this, SignActivity.class), SAL_SIGN);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURED_BEFORE){

            if (resultCode == RESULT_OK){

                File file = new File(Environment.getExternalStorageDirectory(), String.valueOf(number) + "_before.jpg");

                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(String.valueOf(file)), 100, 100);
                ((ImageView)findViewById(R.id.imgBeforePNG)).setImageBitmap(thumbImage);
            }else if (requestCode == RESULT_CANCELED){

            }
        } else if (requestCode == IMAGE_CAPTURED_AFTER){

            if (resultCode == RESULT_OK){

                File file = new File(Environment.getExternalStorageDirectory(), String.valueOf(number) + "_after.jpg");

                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(String.valueOf(file)), 100, 100);
                ((ImageView)findViewById(R.id.imgAfterPNG)).setImageBitmap(thumbImage);
            }else if (requestCode == RESULT_CANCELED){

            }
        }else if (requestCode == CUS_SIGN){

            if (resultCode == RESULT_OK){
                byte[] byteArray = data.getExtras().getByteArray(OrderActivity.SIGN);
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ((ImageView)findViewById(R.id.imgCusSign)).setImageBitmap(bmp);
            }else if (resultCode == RESULT_CANCELED){

            }
        }else if (requestCode == SAL_SIGN){

            if (resultCode == RESULT_OK){
                byte[] byteArray = data.getExtras().getByteArray(OrderActivity.SIGN);
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ((ImageView)findViewById(R.id.imgSalerSign)).setImageBitmap(bmp);
            }else if (resultCode == RESULT_CANCELED){

            }
        }
    }
}
