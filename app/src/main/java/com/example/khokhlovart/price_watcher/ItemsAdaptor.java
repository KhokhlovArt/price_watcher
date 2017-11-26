package com.example.khokhlovart.price_watcher;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dom on 25.11.2017.
 */

public class ItemsAdaptor extends RecyclerView.Adapter<ItemsAdaptor.ItemViewHolder> {
    private List<Item> itemList = new ArrayList<>();
    private ItemsAdapterListener clickListener = null;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public final int HEAD_HOLDER_TYPE = 0;
    public final int BODY_HOLDER_TYPE = 1;

    public ItemsAdaptor(){
    }

    public void addItem(Item i){
        itemList.add(i);
    }

    public void setItems(List<Item> listItms){
        this.itemList = listItms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ? HEAD_HOLDER_TYPE : BODY_HOLDER_TYPE);
    }

    public void setListener(ItemsAdapterListener listener) {
        this.clickListener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEAD_HOLDER_TYPE:
                holder.setHeader();
                break;
            case BODY_HOLDER_TYPE:
                Item item = itemList.get(position);
                holder.bind(item, position, selectedItems.get(position, false), clickListener);
                holder.setRow();
                holder.shop.setText(itemList.get(position).shop.domain);
                holder.description.setText(itemList.get(position).description);
                holder.date.setText(itemList.get(position).date);
                holder.isHave.setText(itemList.get(position).inStock ? "+" : "-");
                holder.cost.setText(String.format("%.2f",itemList.get(position).price));
                holder.tab_row.setBackgroundColor(position % 2 == 0 ? MainActivity.getRes().getColor(R.color.mainColor_1) : MainActivity.getRes().getColor(R.color.mainColor_2));
                if (holder.itemView.isActivated()) {
                    holder.tab_row.setBackgroundColor(MainActivity.getRes().getColor(R.color.rowToDelete));
                    holder.btn_del.setVisibility(View.VISIBLE);
                } else {
                    holder.tab_row.setBackgroundColor(position % 2 == 0 ? MainActivity.getRes().getColor(R.color.mainColor_1) : MainActivity.getRes().getColor(R.color.mainColor_2));
                    holder.btn_del.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
//        return itemList.size() + 1;
        return itemList.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    int getSelectedItemCount() {
        return selectedItems.size();
    }

    List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    Item remove(int pos) {
        final Item item = itemList.remove(pos);
        notifyItemRemoved(pos);
        return item;
    }

    /****************************************************************************************************************
     ******************************** ItemViewHolder *****************************************************************
     ****************************************************************************************************************/
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView shop;
        public TextView description;
        public TextView link;
        public TextView date;
        public TextView isHave;
        public TextView cost;
        public TableRow tab_row;
        public ImageButton btn_del;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tab_row     = (TableRow) itemView.findViewById(R.id.tab_row);
            shop        = (TextView) itemView.findViewById(R.id.shop);
            description = (TextView) itemView.findViewById(R.id.description);
            link        = (TextView) itemView.findViewById(R.id.link);
            date        = (TextView) itemView.findViewById(R.id.date);
            isHave      = (TextView) itemView.findViewById(R.id.isHave);
            cost        = (TextView) itemView.findViewById(R.id.cost);
            btn_del     = (ImageButton) itemView.findViewById(R.id.btn_del);
        }

        void bind(final Item item, final int position, boolean selected, final ItemsAdapterListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(item, position);
                    return true;
                }
            });
            itemView.setActivated(selected);
        }

        public void setHeader()
        {
            setHeadStyle(shop,       "Магазин");
            setHeadStyle(description,"Описание");
            setHeadStyle(date,       "Дата");
            setHeadStyle(isHave,     "Наличие");
            setHeadStyle(cost,       "Цена");
        }
        public void setRow()
        {
            setRowStyle(shop);
            setRowStyle(description);
            setRowStyle(date);
            setRowStyle(isHave);
            setRowStyle(cost);
        }

        private void setHeadStyle(TextView v, String text)
        {
            v.setText(text);
            v.setTypeface(null, Typeface.BOLD);
            v.setTextColor(MainActivity.getRes().getColor(R.color.black));
        }
        private void setRowStyle(TextView v)
        {
            v.setTypeface(null, Typeface.NORMAL);
            v.setTextColor(MainActivity.getRes().getColor(R.color.colorPrimary));
        }
    }
}
