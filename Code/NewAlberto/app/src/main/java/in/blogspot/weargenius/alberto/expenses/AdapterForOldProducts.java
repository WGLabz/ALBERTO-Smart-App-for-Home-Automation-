package in.blogspot.weargenius.alberto.expenses;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.ProductsDB;

public class AdapterForOldProducts extends RecyclerView.Adapter<AdapterForOldProducts.ViewHolder> {
    private final TypedValue mTypedValue = new TypedValue();
    private ArrayList<String> productName;
    private ArrayList<String> productCost;
    private ArrayList<String> productWarranty;
    private ArrayList<String> productFrom;
    private ArrayList<Integer> productId;
    private ArrayList<String> productDate;
    private ArrayList<String> productImage;
    private int mBackground;
    private Context productContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterForOldProducts(Context con, ArrayList<Integer> productId, ArrayList<String> productName, ArrayList<String> purchaseDate, ArrayList<String> productCost, ArrayList<String> prodcutFrom, ArrayList<String> productWarranty, ArrayList<String> productImage) {
        this.productName = productName;
        this.productCost = productCost;
        this.productWarranty = productWarranty;
        this.productFrom = prodcutFrom;
        this.productId = productId;
        this.productDate = purchaseDate;
        this.productImage = productImage;
        this.productContext = con;

        con.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);

        mBackground = mTypedValue.resourceId;

    }

    public void remove(String item) {
        int position = productName.indexOf(item);
        productName.remove(position);
        productCost.remove(position);
        productWarranty.remove(position);
        productFrom.remove(position);
        productId.remove(position);
        productDate.remove(position);
        productImage.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterForOldProducts.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.old_products_list_item, parent, false);
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
        holder.itemName.setText(productName.get(position));
        holder.itemFrom.setText(productFrom.get(position));
        holder.boughtDate.setText(productDate.get(position));
        holder.itemWarranty.setText(productWarranty.get(position));
        holder.itemCost.setText(productCost.get(position));

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        ProductsDB productsDB = new ProductsDB(productContext);
                        productsDB.open();
                        productsDB.deleteProduct(productId.get(position));
                        productsDB.close();
                        remove(productName.get(position));
                        Toast.makeText(productContext, "Product Removed", Toast.LENGTH_SHORT).show();
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
        Glide.with(holder.itemImage.getContext())
                .load(productImage.get(position))
                .fitCenter()
                .into(holder.itemImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return productName.size();
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        private TextView itemName;
        private TextView itemFrom;
        private TextView boughtDate;
        private TextView itemWarranty;
        private TextView itemCost;
        private ImageView itemImage;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            itemName = (TextView) v.findViewById(R.id.item_name);
            itemCost = (TextView) v.findViewById(R.id.price);
            itemFrom = (TextView) v.findViewById(R.id.item_from);
            boughtDate = (TextView) v.findViewById(R.id.current_date);
            itemWarranty = (TextView) v.findViewById(R.id.warranty);
            itemImage = (ImageView) v.findViewById(R.id.product_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemName.getText();
        }
    }

}