package in.sunilpaulmathew.weatherwidget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.serializable.LocationItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private final List<LocationItems> mData;
    private final OnItemClickListener mClickListener;

    public LocationsAdapter(List<LocationItems> data, OnItemClickListener clickListener) {
        this.mData = data;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_locations, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsAdapter.ViewHolder holder, int position) {
        holder.mLocationItems.setText(this.mData.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialTextView mLocationItems;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mLocationItems = view.findViewById(R.id.locations);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(mData.get(getBindingAdapterPosition()));
        }

    }

    public interface OnItemClickListener {
        void onItemClick(LocationItems locationItem);
    }

}