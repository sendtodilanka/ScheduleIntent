package com.sldroids.scheduleintent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sldroids.scheduleintent.sqlite.DataBaseAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String FIRST_TIME = "first_time";
    public static final String ORDER_NO = "order_no";
    public static final String CUSTOMER = "customer";
    public static final String FUEL_TYPE = "fuel_type";
    public static final String QUANTITY = "quantity";
    private DataBaseAdapter dbAdapter;

    private RecyclerView recyclerview;
    private FastScroller fastScroller;
    private ArrayList<Order> orders;
    private RecyclerViewAdapter recyclerAdapter;

    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "++ onCreate ++");

        dbAdapter = new DataBaseAdapter(this);
        dbAdapter.createDatabase();
        dbAdapter.open();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(FIRST_TIME, false)) {

            // <---- run your one time code here
            Log.d(TAG, "++ Runt at first time only ++");
            // Set Alarm at first time
            new AlarmReceiver().setAlarm(this);

            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_TIME, true);
            editor.commit();
        }

        updateCardView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            dbAdapter.delLastRow();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateCardView(){
        recyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        fastScroller = (FastScroller) findViewById(R.id.fastScroller);

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        orders = dbAdapter.getActiveOrders();
        recyclerAdapter = new RecyclerViewAdapter(orders);
        recyclerview.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        //has to be called AFTER RecyclerView.setAdapter()
        fastScroller.setRecyclerView(recyclerview);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
            implements SectionTitleProvider {

        private final ArrayList<Order> order;

        RecyclerViewAdapter(ArrayList<Order> orders){
            this.order = orders;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
            holder.mapView.onCreate(null);
            holder.mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    builder = new LatLngBounds.Builder();

                    // Add a marker in Sydney and move the camera
                    LatLng start = new LatLng(order.get(position).getSrcLat(), order.get(position).getSrcLon());
                    LatLng stop = new LatLng(order.get(position).getDesLat(), order.get(position).getDesLon());

                    MarkerOptions markerOptions1 = new MarkerOptions();
                    markerOptions1.position(start).title("Start");
                    googleMap.addMarker(markerOptions1);
                    builder.include(markerOptions1.getPosition());

                    markerOptions1.position(stop).title("Stop");
                    googleMap.addMarker(markerOptions1);
                    builder.include(markerOptions1.getPosition());

                    bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    googleMap.animateCamera(cu);

                    PolylineOptions line= new PolylineOptions().add(start, stop).width(5).color(Color.RED);
                    googleMap.addPolyline(line);

                    DrawArrowHead(googleMap, start, stop);
                }
            });
            holder.txtCustomer.setText("Customer: " + order.get(position).getCname());
            holder.txtOrderNo.setText("Order No: #" + String.valueOf(order.get(position).getNumber()));
            holder.txtFuelType.setText("Fuel Type: " + order.get(position).getItem());
            holder.txtQty.setText(String.valueOf("Qty: " + order.get(position).getQty()) + " Litter");
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
                    orderIntent.putExtra(ORDER_NO, order.get(position).getNumber());
                    orderIntent.putExtra(CUSTOMER, order.get(position).getCname());
                    orderIntent.putExtra(FUEL_TYPE, order.get(position).getItem());
                    orderIntent.putExtra(QUANTITY, order.get(position).getQty());
                    MainActivity.this.startActivity(orderIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return order.size();
        }

        @Override
        public String getSectionTitle(int position) {
            return order.get(position).getCname().substring(0,1).toUpperCase(Locale.ENGLISH);
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final MapView mapView;
        private final CardView cardView;
        private final TextView txtCustomer, txtOrderNo, txtFuelType, txtQty;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            txtCustomer = (TextView) itemView.findViewById(R.id.txtCustomer);
            txtOrderNo = (TextView) itemView.findViewById(R.id.txtOrderNo);
            txtFuelType = (TextView) itemView.findViewById(R.id.txtFuelType);
            txtQty = (TextView) itemView.findViewById(R.id.txtQty);
            mapView = (MapView) itemView.findViewById(R.id.map_view);
        }
    }

    private final double degreesPerRadian = 180.0 / Math.PI;

    private void DrawArrowHead(GoogleMap mMap, LatLng from, LatLng to){
        // obtain the bearing between the last two points
        double bearing = GetBearing(from, to);

        // round it to a multiple of 3 and cast out 120s
        double adjBearing = Math.round(bearing / 3) * 3;
        while (adjBearing >= 120) {
            adjBearing -= 120;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the corresponding triangle marker from Google
        URL url;
        Bitmap image = null;

        try {
            url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (image != null){

            // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
            float anchorX = 0.5f;
            float anchorY = 0.5f;

            int offsetX = 0;
            int offsetY = 0;

            // images are 24px x 24px
            // so transformed image will be 48px x 48px

            //315 range -- 22.5 either side of 315
            if (bearing >= 292.5 && bearing < 335.5){
                offsetX = 24;
                offsetY = 24;
            }
            //270 range
            else if (bearing >= 247.5 && bearing < 292.5){
                offsetX = 24;
                offsetY = 12;
            }
            //225 range
            else if (bearing >= 202.5 && bearing < 247.5){
                offsetX = 24;
                offsetY = 0;
            }
            //180 range
            else if (bearing >= 157.5 && bearing < 202.5){
                offsetX = 12;
                offsetY = 0;
            }
            //135 range
            else if (bearing >= 112.5 && bearing < 157.5){
                offsetX = 0;
                offsetY = 0;
            }
            //90 range
            else if (bearing >= 67.5 && bearing < 112.5){
                offsetX = 0;
                offsetY = 12;
            }
            //45 range
            else if (bearing >= 22.5 && bearing < 67.5){
                offsetX = 0;
                offsetY = 24;
            }
            //0 range - 335.5 - 22.5
            else {
                offsetX = 12;
                offsetY = 24;
            }

            Bitmap wideBmp;
            Canvas wideBmpCanvas;
            Rect src, dest;

            // Create larger bitmap 4 times the size of arrow head image
            wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());

            wideBmpCanvas = new Canvas(wideBmp);

            src = new Rect(0, 0, image.getWidth(), image.getHeight());
            dest = new Rect(src);
            dest.offset(offsetX, offsetY);

            wideBmpCanvas.drawBitmap(image, src, dest, null);

            mMap.addMarker(new MarkerOptions()
                    .position(to)
                    .icon(BitmapDescriptorFactory.fromBitmap(wideBmp))
                    .anchor(anchorX, anchorY));
        }
    }

    private double GetBearing(LatLng from, LatLng to){
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ), Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;

        return angle;
    }
}
