package com.fahim.autoexam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahim.autoexam.db.ReportEntity;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<ReportEntity> reports = new ArrayList<>();
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(ReportEntity report);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ReportEntity report);
    }

    public ReportAdapter(OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    public void setReports(List<ReportEntity> reports) {
        this.reports = reports;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportEntity report = reports.get(position);
        holder.title.setText(report.paperName);
        holder.description.setText(report.subjectName);
        holder.date.setText(report.date);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(report);
            }
        });
        holder.more.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Delete")) {
                    if (deleteListener != null) {
                        deleteListener.onDeleteClick(report);
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;
        ImageView more;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            more = itemView.findViewById(R.id.more);
        }
    }
}
