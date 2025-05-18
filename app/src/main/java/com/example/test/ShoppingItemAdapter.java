package com.example.test;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShoppingItem> mShoppingItemData;
    private ArrayList<ShoppingItem> mShoppingItemDataAll;
    private Context nContext;
    private int lastPositon = -1;
    private boolean isAdmin = false;

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData){
        this.mShoppingItemData=itemsData;
        this.mShoppingItemDataAll=itemsData;
        this.nContext=context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(nContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem=mShoppingItemData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition()>lastPositon){
            Animation animation = AnimationUtils.loadAnimation(nContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPositon=holder.getAdapterPosition();
        }
        if (isAdmin) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList=new ArrayList<>();
            FilterResults results=new FilterResults();

            if(charSequence == null || charSequence.length()==0){
                results.count=mShoppingItemDataAll.size();
                results.values = mShoppingItemDataAll;
            }else{
                String filterPattern=charSequence.toString().toLowerCase().trim();
                for (ShoppingItem item: mShoppingItemDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count=filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingItemData=(ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mNameText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;

        public Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mNameText=itemView.findViewById(R.id.itemName);
            mInfoText=itemView.findViewById(R.id.itemInfo);
            mPriceText=itemView.findViewById(R.id.itemPrice);
            mItemImage=itemView.findViewById(R.id.itemImage);
            deleteButton = itemView.findViewById(R.id.delete);

        }

        public void bindTo(ShoppingItem currentItem) {

            mNameText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());

            int imageRes = currentItem.getImageResorce();
            if (imageRes != 0) {
                Glide.with(nContext).load(imageRes).into(mItemImage);
            } else {
                // Alapértelmezett kép, ha hibás vagy hiányzó az imageResource
                Glide.with(nContext).load(R.drawable.placeholder).into(mItemImage);
            }

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((ShopListActivity)nContext).updateAlertIcon(currentItem));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((ShopListActivity)nContext).deleteItem(currentItem));
        }
    }
}


