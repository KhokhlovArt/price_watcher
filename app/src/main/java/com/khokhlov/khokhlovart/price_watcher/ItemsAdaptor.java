package com.khokhlov.khokhlovart.price_watcher;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khokhlov.khokhlovart.price_watcher.Models.ItemListModels;
import com.khokhlov.khokhlovart.price_watcher.Results.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ItemsAdaptor extends RecyclerView.Adapter<ItemsAdaptor.ItemViewHolder> {
    private Activity baseActivity;
    private ItemListModels mdl;
    private ItemsAdapterListener clickListener = null;
    public final int HEAD_HOLDER_TYPE = 0;
    public final int BODY_HOLDER_TYPE = 1;


    public ItemsAdaptor(Activity a, ItemListModels mdl)
    {
        baseActivity = a;
        this.mdl = mdl;
    }

    public void addItem(Item i){
        mdl.fullItemList.add(i);
        mdl.itemList.add(i);
    }



    public void filterItems(String query) {
        if ((query != null) && (!query.equals("")))
        {
            mdl.itemList = new ArrayList<>();
            for (Item item : mdl.fullItemList) {
                if (item.description.toLowerCase().contains(query.toLowerCase())){
                    mdl.itemList.add(item);
                    item.isFinde = true;
                }
                else
                {
                    item.isFinde = false;
                }
            }
        }
        else
        {
            mdl.itemList = mdl.fullItemList;
            for (Item item : mdl.itemList) {item.isFinde = false;}
        }
        notifyDataSetChanged();
    }

    public void setItems(List<Item> listItms){

        mdl.itemList.clear();
        mdl.fullItemList.clear();

        mdl.itemList = new ArrayList<>();
        mdl.fullItemList = new ArrayList<>();
        for (Item item : listItms) {
            mdl.itemList.add(item);
            mdl.fullItemList.add(item);
        }

        //this.itemList.add(0, new Item("-", "-", "-", false, 0)); // Костыль что бы вставить шапку таблицы
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        //return (position == 0 ? HEAD_HOLDER_TYPE : BODY_HOLDER_TYPE);
        return BODY_HOLDER_TYPE;
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
                Item item = mdl.itemList.get(position);
                boolean isNeedLight = isNeedLight(item.id);
                holder.bind(item, position, mdl.selectedItems.get(position, false), clickListener);


                holder.shop.setText(        mdl.itemList.get(position).shop.domain);
                holder.description.setText( (mdl.itemList.get(position).shop.parserState) ? mdl.itemList.get(position).description : MainActivity.getRes().getString(R.string.shop_is_added));
                holder.date.setText(        (mdl.itemList.get(position).createDate == null) ? "" :  new SimpleDateFormat("dd-MM-yyyy HH:mm").format(mdl.itemList.get(position).createDate));
                holder.isHave.setText(      mdl.itemList.get(position).inStock ? MainActivity.getRes().getString(R.string.in_stock) :  MainActivity.getRes().getString(R.string.not_in_stock) );
                holder.cost.setText(        PWService.formatPrice(mdl.itemList.get(position).price));
                if (holder.itemView.isActivated()) {
                    holder.itemCard.setBackgroundColor(ContextCompat.getColor(baseActivity.getBaseContext(), R.color.rowToDelete));
                    holder.btn_del.setVisibility(View.VISIBLE);
                } else {
                    
                    //holder.itemCard.setBackground(ResourcesCompat.getDrawable(MainActivity.getRes(), R.drawable.back_repeat, null));
                    holder.itemCard.setBackgroundColor(MainActivity.getRes().getColor(item.isFinde ? R.color.findeColor : R.color.mainColor_1));
                    holder.btn_del.setVisibility(View.GONE);
                }

                boolean itemInStock = mdl.itemList.get(position).inStock;
                holder.shop.setTextColor(ContextCompat.getColor(baseActivity.getBaseContext(),        itemInStock ? R.color.colorPrimary : R.color.itemGone));
                holder.description.setTextColor(ContextCompat.getColor(baseActivity.getBaseContext(), itemInStock ? R.color.black        : R.color.itemGone));
                holder.date.setTextColor(ContextCompat.getColor(baseActivity.getBaseContext(),        itemInStock ? R.color.colorPrimary : R.color.itemGone));
                holder.isHave.setTextColor(ContextCompat.getColor(baseActivity.getBaseContext(),      itemInStock ? R.color.colorPrimary : R.color.itemGone));
                holder.cost.setTextColor(ContextCompat.getColor(baseActivity.getBaseContext(),        itemInStock ? R.color.colorAccent  : R.color.itemGone));

                holder.img_light.setVisibility(isNeedLight ? View.VISIBLE : View.GONE );
                break;
            default:
                break;
        }

//holder.itemView.startAnimation(holder.anim);
//Animation anim = null;
//anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.alpha);
    }


    @Override
    public int getItemCount() {
        return mdl.itemList.size();
    }

    public void toggleSelection(int pos) {
        if (mdl.selectedItems.get(pos, false)) {
            mdl.selectedItems.delete(pos);
        } else {
            mdl.selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    void clearSelections() {
        mdl.selectedItems.clear();
        notifyDataSetChanged();
    }

    public Item getItemByPosition(int pos) {
        return mdl.itemList.get(pos);
    }

    int getSelectedItemCount() {
        return mdl.selectedItems.size();
    }

    List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mdl.selectedItems.size());
        for (int i = 0; i < mdl.selectedItems.size(); i++) {
            items.add(mdl.selectedItems.keyAt(i));
        }
        return items;
    }

    Item remove(int pos) {
        final Item item = mdl.itemList.remove(pos);
        mdl.fullItemList.remove(pos);
        notifyItemRemoved(pos);
        return item;
    }

    private boolean isNeedLight(int itemId)
    {
        boolean isNeedLight = false;
        App apl = (App) baseActivity.getApplication();
        String lightItemsInStr = apl.getPreferences(App.IS_CHANGE_ITEM);
        if  (lightItemsInStr != null) {
            String[] lightId = lightItemsInStr.split(",");

            for (int i = 0; i < lightId.length; i++) {
                if (lightId[i].equals(Integer.toString(itemId))) {
                    isNeedLight = true;
                    apl.setPreferences(App.IS_CHANGE_ITEM, lightItemsInStr.replace("," + Integer.toString(itemId), ""));
                    break;
                }
            }
        }
        return isNeedLight;
    }
    /****************************************************************************************************************
     ******************************** ItemViewHolder *****************************************************************
     ****************************************************************************************************************/
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout itemCard;
        public TextView shop;
        public TextView description;
        public TextView date;
        public TextView isHave;
        public TextView cost;
        public ImageButton img_light;
        public ImageButton btn_del;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemCard    = (RelativeLayout) itemView.findViewById(R.id.item_card);
            shop        = (TextView) itemView.findViewById(R.id.shop);
            description = (TextView) itemView.findViewById(R.id.description);
            date        = (TextView) itemView.findViewById(R.id.date);
            isHave      = (TextView) itemView.findViewById(R.id.isHave);
            cost        = (TextView) itemView.findViewById(R.id.cost);
            img_light   = (ImageButton) itemView.findViewById(R.id.light_img);
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
            Resources r = MainActivity.getRes();
            setHeadStyle(shop,        r.getString(R.string.head_titel_shop));
            setHeadStyle(description, r.getString(R.string.head_titel_description));
            setHeadStyle(date,        r.getString(R.string.head_titel_date));
            setHeadStyle(isHave,      r.getString(R.string.head_titel_isHave));
            setHeadStyle(cost,        r.getString(R.string.head_titel_cost));
        }

        private void setHeadStyle(TextView v, String text)
        {
            v.setText(text);
            v.setTypeface(null, Typeface.BOLD);
            v.setTextColor(ContextCompat.getColor(baseActivity.getBaseContext(), R.color.black));
        }

    }
}
