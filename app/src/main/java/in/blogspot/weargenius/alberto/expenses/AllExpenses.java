package in.blogspot.weargenius.alberto.expenses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.MyExpensesDB;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;

public class AllExpenses extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText itemCostView;
    EditText itemNameView;
    adapterForExpensesList expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //
        refreshContents();
        itemCostView = (EditText) findViewById(R.id.item_cost);
        itemNameView = (EditText) findViewById(R.id.item_name);
    }

    private void refreshContents() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.expenses_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        MyExpensesDB expenseDB = new MyExpensesDB(getApplicationContext());
        expenseDB.open();
        Cursor allExpenses = expenseDB.getPreviousExpenses();
        int i = 0;
        ArrayList<String> itemName = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> itemCost = new ArrayList<>(allExpenses.getCount());
        ArrayList<String> expenseDate = new ArrayList<>(allExpenses.getCount());
        ArrayList<Integer> expenseId = new ArrayList<>(allExpenses.getCount());
        if (allExpenses.getCount() > 0)
            for (allExpenses.moveToFirst(); i < allExpenses.getCount(); allExpenses.moveToNext()) {

                itemName.add(i, allExpenses.getString(allExpenses.getColumnIndex("item")));
                itemCost.add(i, allExpenses.getString(allExpenses.getColumnIndex("cost")));
                expenseDate.add(i, allExpenses.getString(allExpenses.getColumnIndex("currentDate")));
                expenseId.add(i, allExpenses.getInt(allExpenses.getColumnIndex("Sl_No")));
                Log.d("Expenses_all", allExpenses.getString(allExpenses.getColumnIndex("item")));
                i++;
            }
        // specify an adapter (see also next example)
        expenseAdapter = new adapterForExpensesList(this, expenseId, itemName, itemCost, expenseDate);
        mRecyclerView.setAdapter(expenseAdapter);
        expenseDB.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_expense) {
            MyExpensesDB notesDB = new MyExpensesDB(getApplicationContext());
            notesDB.open();
            notesDB.addExpense(itemNameView.getText().toString(), itemCostView.getText().toString());
            notesDB.close();
            Toast.makeText(AllExpenses.this, "Expense Added", Toast.LENGTH_SHORT).show();
            expenseAdapter.notifyDataSetChanged();
            refreshContents();
            itemNameView.setText("");
            itemCostView.setText("");
            itemNameView.requestFocus();
            return true;
        }
        if (id == R.id.add_product) {
            startActivity(new Intent(getApplicationContext(), new_product.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
