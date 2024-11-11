package com.example.nekochancoffee.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.CategoryActivity;
import com.example.nekochancoffee.Activities.PaymentActivity;
import com.example.nekochancoffee.Activities.TableDetail;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private final Context context;
    private final List<Table> tableList;
    private final OnTableActionListener listener;

    public TableAdapter(Context context, List<Table> tableList, OnTableActionListener listener) {
        this.context = context;
        this.tableList = tableList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_item, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);

        // Set table attributes
        holder.txtTableName.setText(table.getTable_name());
        // Handle image placeholder for table (if applicable)
        if("yes".equals(table.getTable_status())){
            holder.imgTableImage.setImageResource(R.drawable.table_seat);
        }else holder.imgTableImage.setImageResource(R.drawable.table_coffee);
//        holder.imgTableImage.setImageResource(R.drawable.table_coffee);

        holder.imgOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryActivity.class);
            intent.putExtra("tableId", table.getTable_id());
            intent.putExtra("tableStatus","yes");
            context.startActivity(intent);
        });
        holder.imgPayment.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra("tableId", table.getTable_id());
            intent.putExtra("tableStatus","no");
            context.startActivity(intent);
        });
        holder.imgDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, TableDetail.class);
            intent.putExtra("tableId", table.getTable_id());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
            popupMenu.getMenuInflater().inflate(R.menu.menu_cat_action_delete_edit, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    if (item.getItemId() == R.id.action_edit) {
                        listener.onEditTable(table);
                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        listener.onDeleteTable(table);
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView txtTableName;
        ImageView imgTableImage ,imgOrder,imgPayment,imgDetail;
        FloatingActionButton btnAddTable;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTableName = itemView.findViewById(R.id.txtTableName);
            imgOrder = itemView.findViewById(R.id.imgOrder);
            imgPayment = itemView.findViewById(R.id.imgPayment);
            imgDetail = itemView.findViewById(R.id.imgDetail);
            imgTableImage = itemView.findViewById(R.id.imgTable); // status
            btnAddTable = itemView.findViewById(R.id.btnAddTable);
        }
    }

    // Interface for table actions
    public interface OnTableActionListener {
        void onDeleteTable(Table table);
        void onEditTable(Table table);
    }
}
