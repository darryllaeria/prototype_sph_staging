package com.hbb20;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeDialog {
    static void openCountryCodeDialog(CountryCodePicker codePicker) {
        Context context = codePicker.getContext();
        final Dialog dialog = new Dialog(context, R.style.SelectCountryDialogStyle);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        List<Country> masterCountries = Country.getCustomMasterCountryList(codePicker);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog_new);
        RecyclerView recyclerView_countryDialog = dialog.findViewById(R.id.recycler_countryDialog);
        final EditText editText_search = dialog.findViewById(R.id.editText_search);
        TextView textView_noResult = dialog.findViewById(R.id.textView_noresult);
        textView_noResult.setText(codePicker.getNoResultFoundText());
        TextView imageView = dialog.findViewById(R.id.tvClose);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final CountryCodeAdapter cca = new CountryCodeAdapter(context, masterCountries, codePicker, editText_search, textView_noResult, dialog);
        if (!codePicker.isSelectionDialogShowSearch()) {
            Toast.makeText(context, "Found not to show search", Toast.LENGTH_SHORT).show();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView_countryDialog.getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerView_countryDialog.setLayoutParams(params);
        }
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_countryDialog.setAdapter(cca);
        recyclerView_countryDialog.requestFocus();
        dialog.show();
    }
}
