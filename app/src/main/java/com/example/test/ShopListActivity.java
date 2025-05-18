package com.example.test;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.SimpleTimeZone;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG=ShopListActivity.class.getName();
    private FirebaseUser user;
    //private FirebaseAuth mAuth;
    private RecyclerView mRecycleView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private int gridNumber=1;
    private boolean viewRow = true;
    private int cartItems=0;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private int querryLimit=20;
    private NotificationHandler mNotiHandler;
    private AlarmManager mAlarmManager;
    private JobScheduler mJobScheduler;
    private Button addProductButton;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = findViewById(R.id.shop_toolbar);
        setSupportActionBar(toolbar);

        mRecycleView=findViewById(R.id.recycleView);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList=new ArrayList<>();

        mAdapter=new ShoppingItemAdapter(this, mItemList);
        mRecycleView.setAdapter(mAdapter);

        mFirestore=FirebaseFirestore.getInstance();
        mItems=mFirestore.collection("Items");

        user= FirebaseAuth.getInstance().getCurrentUser();
        addProductButton = findViewById(R.id.add_product_button);
        if(user != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (user == null || user.isAnonymous()) {
                // Vendégként használja az appot
                Log.i(LOG_TAG, "Vendégként használja az alkalmazást.");
                mAdapter.setIsAdmin(false);
                mAdapter.notifyDataSetChanged();
                addProductButton.setVisibility(View.GONE);
                return;
            }

            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            boolean isAdmin = "admin".equals(role);

                            if (isAdmin) {
                                addProductButton.setVisibility(View.VISIBLE);
                                addProductButton.setOnClickListener(v -> {
                                    Intent intent = new Intent(this, AddItemActivity.class);
                                    startActivity(intent);
                                });
                            } else {
                                addProductButton.setVisibility(View.GONE);
                            }

                            mAdapter.setIsAdmin(isAdmin);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            // Nincs dokumentum, akkor kezeljük user-ként
                            mAdapter.setIsAdmin(false);
                            mAdapter.notifyDataSetChanged();
                            addProductButton.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Nem sikerült lekérdezni a felhasználói szerepet", e);
                        Toast.makeText(this, "Nem sikerült lekérdezni a felhasználói szerepet", Toast.LENGTH_SHORT).show();
                        mAdapter.setIsAdmin(false);
                        mAdapter.notifyDataSetChanged();
                        addProductButton.setVisibility(View.GONE);
                    });
            Log.i(LOG_TAG, "Authenticated User!");
        }else{
            Log.i(LOG_TAG, "Unauthenticated User!");
            finish();
        }
        querryData();
        //initializeData();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);
        registerReceiver(powerReceiver, filter);
        isReceiverRegistered = true;

        mNotiHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        setAlarmManager();
        setJobSceduler();

    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action==null){
                return;
            }
            switch (action){
                case Intent.ACTION_POWER_CONNECTED:
                    querryLimit=20;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    querryLimit=15;
                    break;
            }

            querryData();
        }
    };

    private void querryData() {
        mItemList.clear();

        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(querryLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }

            if(mItemList.size()==0){
                initializeData();
                querryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteItem(ShoppingItem item){
        DocumentReference ref = mItems.document(item._getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Item sikeresen törölve: "+ item._getId());
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Item " + item._getId() + "nem lehet törölni.", Toast.LENGTH_LONG).show();
        });

        querryData();
        mNotiHandler.cancel();
    }


    private void initializeData(){
        String[] itemList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemInfo = getResources().getStringArray(R.array.shopping_item_desc);;
        String[] itemPrice= getResources().getStringArray(R.array.shopping_item_price);;
        TypedArray itemImage = getResources().obtainTypedArray(R.array.shopping_item_images);

        for (int i = 0; i < itemList.length; i++) {
            mItems.add(new ShoppingItem(
                    itemList[i],
                    itemInfo[i],
                    itemPrice[i],
                    itemImage.getResourceId(i,0), 0));
        }

        itemImage.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem=menu.findItem(R.id.search_bar);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG,s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id=item.getItemId();

        if(id==R.id.log_out_button){
            Log.d(LOG_TAG, "Log out clicked!");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }else if(id == R.id.profile_button){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }else if(id==R.id.cart){
            Log.d(LOG_TAG, "Cart clicked!");
            return true;
        }else if(id==R.id.view_selector){
            Log.d(LOG_TAG, "View Selector clicked!");
            if (viewRow){
                changeSpanCount(item, R.drawable.ic_view_grid, 1);
            }else{
                changeSpanCount(item, R.drawable.ic_view_row, 2);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCont){
        viewRow= !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecycleView.getLayoutManager();
        layoutManager.setSpanCount(spanCont);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem=menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle=(FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView=(TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShoppingItem item){
        cartItems=(cartItems+1);
        if(0<cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        }else{
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount()+1).addOnFailureListener(failure -> {
            Toast.makeText(this, "Item " + item._getId() + "nem lehet megváltoztatni.", Toast.LENGTH_LONG).show();
        });

        querryData();

        mNotiHandler.send(item.getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(powerReceiver);
            isReceiverRegistered = false;
        }
    }

    private void setAlarmManager(){
        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime= SystemClock.elapsedRealtime()+ repeatInterval;

        Intent intent=new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime,repeatInterval, pendingIntent);

        //mAlarmManager.cancel(pendingIntent);
    }

    private void setJobSceduler(){
        int networkType= JobInfo.NETWORK_TYPE_UNMETERED;
        int hardDeadline=5000;

        ComponentName name=new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, name)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(true)
                .setOverrideDeadline(hardDeadline);

        mJobScheduler.schedule(builder.build());
        //mJobScheduler.cancel(0);
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }*/
}