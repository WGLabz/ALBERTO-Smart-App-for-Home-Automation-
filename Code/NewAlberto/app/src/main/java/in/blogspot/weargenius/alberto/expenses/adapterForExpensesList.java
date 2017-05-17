package in.blogspot.weargenius.alberto.expenses;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.MyExpensesDB;

public class adapterForExpensesList extends RecyclerView.Adapter<adapterForExpensesList.ViewHolder> {
    private final TypedValue mTypedValue = new TypedValue();
    private ArrayList<String> itemName;
    private ArrayList<String> itemCOst;
    private ArrayList<String> expenseDate;
    private ArrayList<Integer> expenseId;
    private int mBackground;
    private Context expenseContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public adapterForExpensesList(Context con, ArrayList<Integer> expenseIdData, ArrayList<String> itemNameData, ArrayList<String> itemCOstData, ArrayList<String> expenseDateData) {
        itemName = itemNameData;
        itemCOst = itemCOstData;
        expenseDate = expenseDateData;
        expenseId = expenseIdData;
        expenseContext = con;
        con.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;

    }

    public void remove(String item) {
        int position = itemName.indexOf(item);
        itemName.remove(position);
        itemCOst.remove(position);
        expenseDate.remove(position);
        expenseId.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public adapterForExpensesList.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_expenses_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.itemName.setText(itemName.get(position));
        holder.itemCost.setText(itemCOst.get(position));
        holder.expenseDate.setText(expenseDate.get(position));
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyExpensesDB notesDB = new MyExpensesDB(expenseContext);
                        notesDB.open();
                        notesDB.deleteExpense(expenseId.get(position));
                        notesDB.close();
                        remove(itemName.get(position));
                        Toast.makeText(expenseContext, "Expense Removed", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                return true;
            }

        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                // expenseContext
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemName.size();
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        private TextView itemName;
        private TextView itemCost;
        private TextView expenseDate;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            itemName = (TextView) v.findViewById(R.id.item_name);
            itemCost = (TextView) v.findViewById(R.id.item_cost);
            expenseDate = (TextView) v.findViewById(R.id.expense_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemName.getText();
        }
    }

}