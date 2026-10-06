package com.christopher.docreader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentDocumentAdapter
        extends RecyclerView.Adapter<RecentDocumentAdapter.ViewHolder> {

    public interface OnDocumentClickListener {
        void onDocumentClick(RecentDocument document);
    }

    private final List<RecentDocument> documents;
    private final OnDocumentClickListener listener;

    public RecentDocumentAdapter(
            List<RecentDocument> documents,
            OnDocumentClickListener listener
    ) {
        this.documents = documents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_recent_document,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        RecentDocument document =
                documents.get(position);

        holder.tvName.setText(
                document.getName()
        );

        String extension =
                document.getExtension();

        if (
                extension == null || extension.isEmpty()) {

            holder.tvType.setText(
                    "Formato desconhecido"
            );

        } else {
            String formattedExtension =
                    extension.toUpperCase();

            holder.tvType.setText(
                    formattedExtension
            );

            holder.tvIcon.setText(
                    formattedExtension
            );
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onDocumentClick(document);
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvType;
        TextView tvIcon;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvRecentName
            );

            tvType = itemView.findViewById(
                    R.id.tvRecentType
            );

            tvIcon = itemView.findViewById(
                    R.id.tvRecentIcon
            );
        }
    }
}