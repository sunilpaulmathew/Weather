package in.sunilpaulmathew.weatherwidget.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.SettingsItems;
import in.sunilpaulmathew.weatherwidget.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final ArrayList<SettingsItems> data;

    private static ClickListener mClickListener;

    public SettingsAdapter(ArrayList<SettingsItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_settings, parent, false);
        return new SettingsAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ViewHolder holder, int position) {
        holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
        holder.mTitle.setText(this.data.get(position).getTitle());
        holder.mDescription.setText(this.data.get(position).getDescription());
        if (position == 1 || position == 2 || position == 5) {
            holder.mChecked.setVisibility(View.VISIBLE);
            if (position == 5) {
                holder.mChecked.setChecked(Utils.getBoolean("transparentBackground", false, holder.mChecked.getContext()));
            } else if (position == 2) {
                holder.mChecked.setChecked(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? !Utils.isNotificationAccessDenied(
                        holder.mChecked.getContext()) && Utils.getBoolean("weatherAlerts", false, holder.mChecked.getContext())
                        : Utils.getBoolean("weatherAlerts", false, holder.mChecked.getContext()));
            } else {
                holder.mChecked.setChecked(!Utils.isLocationAccessDenied(holder.mChecked.getContext()) && Utils.getBoolean("useGPS",
                        true, holder.mChecked.getContext()));
            }
        } else {
            holder.mChecked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mIcon;
        private final SwitchMaterial mChecked;
        private final MaterialTextView mTitle, mDescription;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mChecked = view.findViewById(R.id.checked);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SettingsAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}