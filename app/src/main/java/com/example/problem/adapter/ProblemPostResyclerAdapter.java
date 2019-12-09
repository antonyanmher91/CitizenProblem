package com.example.problem.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.problem.CommentActivity;
import com.example.problem.R;
import com.example.problem.model.Problem_Model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProblemPostResyclerAdapter extends RecyclerView.Adapter<ProblemPostResyclerAdapter.ViewHolder> {
    private List<Problem_Model> list;


    public ProblemPostResyclerAdapter(List<Problem_Model> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.user_name_surname.setText(list.get(position).getName());
        holder.title_post.setText(list.get(position).getAddress());
        holder.post_description.setText(list.get(position).getProblemDescription());
        if (!list.get(position).getUserimg().equals("")) {
            Picasso.get().load(list.get(position).getUserimg()).into(holder.user_img);
        }
        if (!list.get(position).getProblemimg().equals(""))
            Picasso.get().load(list.get(position).getProblemimg()).into(holder.post_img);
        holder.countLike.setText(String.valueOf(list.get(position).getLike()));
        holder.like.setOnClickListener(view -> {
            int likecount = list.get(position).getLike();
            list.get(position).setLike(++likecount);
            notifyDataSetChanged();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Integer> hashmap = new HashMap<>();
            hashmap.put("like", likecount);
            db.collection("problems").document(list.get(position).getId())
                    .set(hashmap, SetOptions.merge());
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_img;
        TextView user_name_surname;
        TextView title_post;
        ImageView post_img;
        TextView post_description;
        ImageView comment;
        ImageView like;
        TextView countLike;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_img = itemView.findViewById(R.id.img_user);
            countLike = itemView.findViewById(R.id.like);
            user_name_surname = itemView.findViewById(R.id.name_surname_user);
            title_post = itemView.findViewById(R.id.title_post);
            post_img = itemView.findViewById(R.id.img_problem);
            post_description = itemView.findViewById(R.id.description_comment);
            comment = itemView.findViewById(R.id.post_coment);
            like = itemView.findViewById(R.id.like_post);
            comment.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), CommentActivity.class);
                intent.putExtra("id", list.get(getAdapterPosition()).getId());
                intent.putExtra("username", list.get(getAdapterPosition()).getName());
                intent.putExtra("imgurl", list.get(getAdapterPosition()).getUserimg());
                itemView.getContext().startActivity(intent);
            });

        }
    }
}
