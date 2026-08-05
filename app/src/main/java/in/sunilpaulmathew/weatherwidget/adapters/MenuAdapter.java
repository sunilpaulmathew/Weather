package in.sunilpaulmathew.weatherwidget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.serializable.MenuItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 05, 2026
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final List<MenuItems> data;
    private final OnItemClickListener listener;

    public MenuAdapter(List<MenuItems> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_menuitems, parent, false);
        return new MenuAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        holder.title.setText(this.data.get(position).getTitle());
        holder.icon.setImageResource(this.data.get(position).getDrawable());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatImageButton icon;
        private final MaterialTextView title;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = view.findViewById(R.id.icon);
            this.title = view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}