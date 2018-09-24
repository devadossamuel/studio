package es.iessaladillo.pedrojoya.pr249.ui.list;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import es.iessaladillo.pedrojoya.pr249.base.BaseListAdapter;
import es.iessaladillo.pedrojoya.pr249.base.BaseViewHolder;

public class ListFragmentAdapter extends BaseListAdapter<String, ListFragmentAdapter.ViewHolder, Long> {

    private static final DiffUtil.ItemCallback<String> diffUtilItemCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return TextUtils.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return TextUtils.equals(oldItem, newItem);
        }
    };
    @LayoutRes
    private final int layoutResId;

    ListFragmentAdapter(@LayoutRes int layoutResId) {
        super(diffUtilItemCallback);
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public ListFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListFragmentAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bind(getItem(position));
    }

    class ViewHolder extends BaseViewHolder {

        private final TextView text1;

        ViewHolder(@NonNull View itemView) {
            super(itemView, onItemClickListener);
            text1 = ViewCompat.requireViewById(itemView, android.R.id.text1);
        }

        void bind(String item) {
            text1.setText(item);
        }
    }

}
