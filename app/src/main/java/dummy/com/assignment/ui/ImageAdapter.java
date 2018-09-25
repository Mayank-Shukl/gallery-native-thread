package dummy.com.assignment.ui;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dummy.com.assignment.R;
import dummy.com.assignment.interactionHandlers.ImageClickHandler;
import dummy.com.assignment.model.ItemData;

public class ImageAdapter extends BaseBindingRecyclerAdapter<ItemData, ImageClickHandler> {

    private List<ItemData> itemDataList;

    public ImageAdapter(ImageClickHandler handler, Context context, List<ItemData> itemDataList) {
        super(handler, context);
        this.itemDataList = new ArrayList<>();
        if (itemDataList != null) {
            this.itemDataList.addAll(itemDataList);
        }
    }

    public void updateData(List<ItemData> itemData) {
        this.itemDataList = new ArrayList<>();
        this.itemDataList.addAll(itemData);
        notifyDataSetChanged();
    }

    public void clearData(){
        this.itemDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutIdForViewType(int viewType) {
        return R.layout.galleryitem;
    }

    @Override
    public ItemData getObjectForPosition(int position) {
        return itemDataList != null && itemDataList.size() > 0 ? itemDataList.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return itemDataList != null ? itemDataList.size() : 0;
    }
}
