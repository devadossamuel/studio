package es.iessaladillo.pedrojoya.pr174.ui.main;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import es.iessaladillo.pedrojoya.pr174.R;

public class MainFragmentAdapter extends ListAdapter<String, MainFragmentAdapter.ViewHolder> {

    MainFragmentAdapter() {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return TextUtils.equals(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return true;
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .fragment_main_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView lblName;

        ViewHolder(View itemView) {
            super(itemView);
            lblName = ViewCompat.requireViewById(itemView, R.id.lblName);
        }

        void bind(String student) {
            lblName.setText(student);
        }

    }

}
