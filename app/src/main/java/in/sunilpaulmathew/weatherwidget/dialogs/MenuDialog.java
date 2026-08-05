package in.sunilpaulmathew.weatherwidget.dialogs;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.MenuAdapter;
import in.sunilpaulmathew.weatherwidget.serializable.MenuItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 05, 2026
 */
public abstract class MenuDialog extends BottomSheetDialog {

    public MenuDialog(List<MenuItems> menuItemList, Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_menuitems, null);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(new MenuAdapter(menuItemList, position -> {
            onCommandSelected(position);
            dismiss();
        }));

        setContentView(root);

        show();
    }

    public abstract void onCommandSelected(int position);

}