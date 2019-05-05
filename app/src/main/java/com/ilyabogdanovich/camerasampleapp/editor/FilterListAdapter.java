package com.ilyabogdanovich.camerasampleapp.editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilyabogdanovich.camerasampleapp.R;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(items[position].bitmap);
        holder.imageView.clearColorFilter();
        holder.imageView.setColorFilter(items[position].colorFilter);
        holder.selectionFrame.setBackgroundColor(position == selectedItem? 0xffff0000 : 0xffffffff);
        holder.nameText.setText(items[position].name);
        holder.itemView.setOnClickListener(view -> itemClickSubject.onNext(position));
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final FrameLayout selectionFrame;
        private final TextView nameText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.filter_list_item_image);
            selectionFrame = itemView.findViewById(R.id.filter_list_item_selection_frame);
            nameText = itemView.findViewById(R.id.filter_list_item_name);
        }
    }

    private int selectedItem = 0;
    private FilterListItem[] items = new FilterListItem[0];
    private final Subject<Integer> itemClickSubject = PublishSubject.create();

    void setItems(FilterListItem[] items) {
        this.items = items;
    }

    void setSelectedItem(int selectedItem) {
        int prevSelectedItem = this.selectedItem;
        this.selectedItem = selectedItem;
        notifyItemChanged(prevSelectedItem);
        notifyItemChanged(selectedItem);
    }

    int getSelectedItem() {
        return selectedItem;
    }

    Observable<Integer> observeItemClick() {
        return itemClickSubject;
    }
}
