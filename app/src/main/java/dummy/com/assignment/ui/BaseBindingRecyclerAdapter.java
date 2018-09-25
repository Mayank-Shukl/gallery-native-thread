package dummy.com.assignment.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import dummy.com.assignment.BR;
import dummy.com.assignment.model.BaseBindingDataItem;

/**
 * generic base Recycler adapter which can be used  as base for a recycler adapter which
 * 1. has a single list for items
 * 2. should use ItemData as its variable name for databinding
 * 3. handler as the name for the click and other interaction events
 *
 * @param <T> - type of data whose list would be returned
 * @param <H> - handler class which would handle clicks and interactions
 */
public abstract class BaseBindingRecyclerAdapter<T extends BaseBindingDataItem, H> extends RecyclerView.Adapter<BaseBindingRecyclerAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private H handler;

    public BaseBindingRecyclerAdapter(H handler, Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding itemBinding =
                DataBindingUtil.inflate(layoutInflater, getLayoutIdForViewType(viewType), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(BaseBindingRecyclerAdapter.ViewHolder holder, int position) {
        BaseBindingDataItem object = getObjectForPosition(position);
        holder.bind(object, position);
    }


    public abstract int getLayoutIdForViewType(int viewType);

    public abstract T getObjectForPosition(int position);


    class ViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BaseBindingDataItem object, final int position) {
            object.setPosition(position);
            this.binding.setVariable(BR.itemData, object);
            this.binding.setVariable(BR.handler, handler);
            this.binding.executePendingBindings();
        }
    }

}
