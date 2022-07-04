package com.example.zuut.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.example.zuut.R;
import com.example.zuut.model.Company;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> {
    private List<Company> localDataSet;

    private final CompanyToggleCallback callback;
    public CompanyListAdapter(List<Company> dataSet, CompanyToggleCallback callback) {
        localDataSet = dataSet;
        this.callback = callback;
    }

    public void setLocalDataSet(List<Company> localDataSet) {
        this.localDataSet = localDataSet;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public CompanyListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_company_switch, viewGroup, false);
        return new ViewHolder(view, callback);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getImageView().setImageResource(localDataSet.get(position).getLogo());
        viewHolder.getTextView().setText(localDataSet.get(position).getName());
        viewHolder.getBtnSwitch().setChecked(localDataSet.get(position).isActivated());
        viewHolder.activateCallbackListener();
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public Company getCompanyAtPos(int position){
        return localDataSet.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        private final TextView textView;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private final Switch btnSwitch;
        private final CompanyToggleCallback callback;

        public ViewHolder(View view, CompanyToggleCallback callback) {
            super(view);
            this.callback = callback;
            imageView = view.findViewById(R.id.companyLogo);
            textView = view.findViewById(R.id.companyName);
            btnSwitch = view.findViewById(R.id.companySwitch);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public Switch getBtnSwitch() {
            return btnSwitch;
        }

        public void activateCallbackListener(){
            btnSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    callback.handle(getAbsoluteAdapterPosition(), isChecked));
        }
    }

    public interface CompanyToggleCallback{
        void handle(int position, boolean isChecked);
    }
}