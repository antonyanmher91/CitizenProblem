package com.example.problem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.problem.R;
import com.example.problem.model.CommentsModel;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecviewAdapter extends RecyclerView.Adapter<CommentsRecviewAdapter.ViewHolder> {
    private List<CommentsModel> list;

    public CommentsRecviewAdapter(List<CommentsModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsRecviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_recview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecviewAdapter.ViewHolder holder, int position) {
        if (!list.get(position).getUserImg().equals("")) {
            Picasso.get().load(list.get(position).getUserImg()).into(holder.img);
        }
        if (list.get(position).getUserName()!=null){
            holder.name.setText(list.get(position).getUserName());

        }else {
            holder.name.setText(R.string.user);
        }
        holder.comments.setText(list.get(position).getComments());
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView name;
        TextView comments;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_user);
            name = itemView.findViewById(R.id.name_surname_user);
            comments = itemView.findViewById(R.id.description_comments);
        }
    }
}
