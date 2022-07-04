package com.example.zuut.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.example.zuut.R;
import com.example.zuut.model.Company;

public class AboutListAdapter extends RecyclerView.Adapter<AboutListAdapter.ViewHolder> {

    private final List<Company> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.companyName);
            imageView = view.findViewById(R.id.companyLogo);
        }

        public TextView getTextView(){
            return textView;
        }
        public ImageView getImageView(){
            return imageView;
        }
    }

    public AboutListAdapter(List<Company> dataSet) {
        localDataSet = dataSet;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_company, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(localDataSet.get(position).getName());
        viewHolder.getImageView().setImageResource(localDataSet.get(position).getLogo());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
