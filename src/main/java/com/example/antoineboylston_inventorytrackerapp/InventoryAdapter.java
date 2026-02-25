package com.example.antoineboylston_inventorytrackerapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * InventoryAdapter
 *
 * RecyclerView adapter responsible for:
 * - Binding inventory database rows (Cursor) to the UI (inventory_row.xml)
 * - Handling per-item actions: increment, decrement, delete
 * - Triggering an SMS alert (via InventoryActivity) when quantity reaches 0
 *
 * Note: This adapter uses a Cursor-backed approach for simplicity and direct DB binding.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final Context context;
    private Cursor cursor;
    private final DatabaseHelper dbHelper;

    public InventoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * ViewHolder caches view references for performance.
     * Each ViewHolder represents one inventory row on screen.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView quantity;
        ImageButton buttonPlus;
        ImageButton buttonMinus;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.textItemName);
            quantity = itemView.findViewById(R.id.textItemQty);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    /**
     * Inflates the row layout and creates a ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.inventory_row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data from the cursor row into the ViewHolder UI.
     * Also assigns click handlers for each row action.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return; // Defensive: should not happen, but avoids crashes
        }

        // Extract row data from DB cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
        int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

        // Bind text fields
        holder.itemName.setText(name);
        holder.quantity.setText(String.valueOf(qty));

        /*
         * Visual feedback for stock levels:
         * - <= 5 : danger (low stock)
         * - <= 10: warning
         * - else : normal
         */
        if (qty <= 5) {
            holder.quantity.setTextColor(
                    context.getResources().getColor(R.color.brand_danger));
        } else if (qty <= 10) {
            holder.quantity.setTextColor(
                    context.getResources().getColor(R.color.brand_warning));
        } else {
            holder.quantity.setTextColor(
                    context.getResources().getColor(R.color.text_primary));
        }

        // + Button: increment quantity by 1
        holder.buttonPlus.setOnClickListener(v -> {
            dbHelper.incrementQuantity(name, 1);
            swapCursor(dbHelper.getAllItems());
        });

        // - Button: decrement quantity by 1 and optionally trigger SMS if it reaches 0
        holder.buttonMinus.setOnClickListener(v -> {
            dbHelper.decreaseQuantity(id);

            // Re-query this item to determine updated quantity
            Cursor updatedCursor = dbHelper.getItemById(id);
            if (updatedCursor != null && updatedCursor.moveToFirst()) {

                int newQty = updatedCursor.getInt(
                        updatedCursor.getColumnIndexOrThrow("quantity"));

                // Only trigger alert when the item becomes out-of-stock
                if (newQty == 0) {
                    SharedPreferences prefs =
                            PreferenceManager.getDefaultSharedPreferences(context);

                    boolean smsEnabled = prefs.getBoolean("sms_enabled", false);

                    // Delegate SMS sending to the activity so permission logic stays centralized
                    if (smsEnabled && context instanceof InventoryActivity) {
                        ((InventoryActivity) context).checkSmsPermissionAndSend(
                                "Inventory Alert: " + name + " is out of stock!"
                        );
                    }
                }
            }

            if (updatedCursor != null) {
                updatedCursor.close();
            }

            swapCursor(dbHelper.getAllItems());
        });

        // Delete button: remove item from inventory
        holder.buttonDelete.setOnClickListener(v -> {
            dbHelper.deleteItem(id);
            swapCursor(dbHelper.getAllItems());
        });
    }

    /**
     * Returns the number of rows in the cursor (inventory item count).
     */
    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    /**
     * Replaces the adapter cursor with updated data and refreshes the list.
     * This is used after insert/update/delete operations.
     */
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}