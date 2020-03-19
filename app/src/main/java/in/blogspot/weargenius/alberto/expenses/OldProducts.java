package in.blogspot.weargenius.alberto.expenses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.ProductsDB;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;

public class OldProducts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), new_product.class));
                finish();
            }
        });
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.old_products_rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ProductsDB productsDB = new ProductsDB(getApplicationContext());
        productsDB.open();
        Cursor allExpenses = productsDB.getOldProducts();
        int i = 0;
        ArrayList<Integer> productId = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> productName = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> productCost = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> prodcutFrom = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> purchaseDate = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> productWarranty = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> productImage = new ArrayList<>(allExpenses.getCount());
        if (allExpenses.getCount() > 0)
            for (allExpenses.moveToFirst(); i < allExpenses.getCount(); allExpenses.moveToNext()) {
                Log.d("Products", "Name :" + allExpenses.getInt(allExpenses.getColumnIndex("Sl_No")));
                productId.add(i, allExpenses.getInt(allExpenses.getColumnIndex("Sl_No")));
                productName.add(i, allExpenses.getString(allExpenses.getColumnIndex("item")));
                purchaseDate.add(i, allExpenses.getString(allExpenses.getColumnIndex("currentDate")));
                productCost.add(i, allExpenses.getString(allExpenses.getColumnIndex("cost")));
                prodcutFrom.add(i, allExpenses.getString(allExpenses.getColumnIndex("fromVendor")));
                productWarranty.add(i, allExpenses.getString(allExpenses.getColumnIndex("warranty")));
                productImage.add(i, allExpenses.getString(allExpenses.getColumnIndex("image")));
                Log.d("Expenses_all", allExpenses.getString(allExpenses.getColumnIndex("item")));
                i++;
            }
        // specify an adapter (see also next example)
        AdapterForOldProducts productsAdapter = new AdapterForOldProducts(this, productId, productName, purchaseDate, productCost, prodcutFrom, productWarranty, productImage);
        mRecyclerView.setAdapter(productsAdapter);
        productsDB.close();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        new UniversalMenuItemSelected(item, getApplicationContext());
        finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
