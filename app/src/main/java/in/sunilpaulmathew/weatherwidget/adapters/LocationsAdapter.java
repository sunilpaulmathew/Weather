package in.sunilpaulmathew.weatherwidget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.LocationItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private static ClickListener mClickListener;
    private final List<LocationItems> mData;

    public LocationsAdapter(List<LocationItems> data) {
        this.mData = data;
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialTextView mLocationItems;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mLocationItems = view.findViewById(R.id.locations);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        LocationsAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }


}