package es.iessaladillo.pedrojoya.pr089.base;

import android.view.View;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// V is ViewModel type, M is Model type.
@SuppressWarnings("unused")
public abstract class BaseListAdapter<M, V extends BaseViewHolder> extends RecyclerView
        .Adapter<V> {

    private List<M> data;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    private View emptyView;
    private final RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkEmptyViewVisibility();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkEmptyViewVisibility();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkEmptyViewVisibility();
        }
    };

    protected BaseListAdapter(@NonNull List<M> data) {
        this.data = data;
    }

    public void submitList(List<M> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void setEmptyView(@NonNull View emptyView) {
        if (this.emptyView != null) {
            unregisterAdapterDataObserver(adapterDataObserver);
        }
        this.emptyView = emptyView;
        registerAdapterDataObserver(adapterDataObserver);
        checkEmptyViewVisibility();
    }

    private void checkEmptyViewVisibility() {
        if (emptyView != null) {
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public final int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public M getItem(int position) {
        return data.get(position);
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(M item) {
        data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    public void insertItem(M item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void swapItems(int from, int to) {
        Collections.swap(data, from, to);
        notifyItemMoved(from, to);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

}