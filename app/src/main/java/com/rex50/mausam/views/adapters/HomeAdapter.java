package com.rex50.mausam.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rex50.mausam.R;
import com.rex50.mausam.interfaces.OnGroupItemClickListener;
import com.rex50.mausam.model_classes.utils.AllContentModel;
import com.rex50.mausam.model_classes.utils.GenericModelFactory;

import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.END_IMAGE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.ITEM_CATEGORY_TYPE;

public class HomeAdapter extends RecyclerView.Adapter{

    private Context context;
    private AllContentModel allContentModel;
    private OnGroupItemClickListener groupItemClickListener;

    private HomeAdapter(){}

    public HomeAdapter(Context context, AllContentModel allContentModel){
        this.context = context;
        this.allContentModel = allContentModel;
    }

    public void setGroupItemClickListener(OnGroupItemClickListener groupItemClickListener){
        this.groupItemClickListener = groupItemClickListener;
    }

    public static class EndImageHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public EndImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.wallpaper_img);
        }
    }

    public static class ItemCategoryHolder extends RecyclerView.ViewHolder{

        TextView txtTitle, txtDesc;
        Button btnMore;
        RecyclerView contentRecyclerView;

        public ItemCategoryHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_category_title);
            txtDesc = itemView.findViewById(R.id.txt_category_desc);
            btnMore = itemView.findViewById(R.id.btn_category_more);
            contentRecyclerView = itemView.findViewById(R.id.recycler_category_items);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(viewType, parent, false);
        switch (viewType){
            case FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE:
            case ITEM_CATEGORY_TYPE :
                return new ItemCategoryHolder(v);
            case END_IMAGE :
                return new EndImageHolder(v);
            default: throw new IllegalArgumentException("Please add case for viewType:\"" + viewType +"\"");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = allContentModel.getModel(position).getViewType();
        if(viewType == ITEM_CATEGORY_TYPE){
            ItemCategoryHolder itemHolder = ((ItemCategoryHolder) holder);
            GenericModelFactory model = allContentModel.getModel(position);
            itemHolder.txtTitle.setText(model.getTitle());
            itemHolder.txtDesc.setText(model.getDesc());
            itemHolder.btnMore.setVisibility(model.isHasMore() ? View.VISIBLE : View.GONE);
            //TODO : try not to create adapter here
            ContentAdapter adapter = new ContentAdapter(context, model);
            itemHolder.contentRecyclerView.setLayoutManager(
                    new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false));
            if(groupItemClickListener != null) {
                adapter.setChildClickListener((o, childView, childPos) -> {
                    groupItemClickListener.onItemClick(o, childView,  position, childPos);
                });
            }
            itemHolder.contentRecyclerView.setAdapter(adapter);
        } else if(viewType == FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE){
            ItemCategoryHolder itemHolder = ((ItemCategoryHolder) holder);
            GenericModelFactory model = allContentModel.getModel(position);
            itemHolder.txtTitle.setText(model.getTitle());
            itemHolder.txtDesc.setText(model.getDesc());
            itemHolder.btnMore.setVisibility(model.isHasMore() ? View.VISIBLE : View.GONE);
            //TODO : try not to create adapter here
            ContentAdapter adapter = new ContentAdapter(context, model);
            itemHolder.contentRecyclerView.setLayoutManager(
                    new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false));
            if(groupItemClickListener != null) {
                adapter.setChildClickListener((o, childView, childPos) -> {
                    groupItemClickListener.onItemClick(o, childView,  position, childPos);
                });
            }
            itemHolder.contentRecyclerView.setAdapter(adapter);
        } else if(viewType == END_IMAGE){
            EndImageHolder itemHolder = ((EndImageHolder) holder);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return allContentModel.getModel(position).getViewLayout();
    }

    @Override
    public int getItemCount() {
        return allContentModel.size();
    }
}
