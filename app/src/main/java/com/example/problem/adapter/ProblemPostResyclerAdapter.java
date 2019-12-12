package com.example.problem.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.problem.CommentActivity;
import com.example.problem.PostActivity;
import com.example.problem.R;
import com.example.problem.model.Constants;
import com.example.problem.model.Problem_Model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProblemPostResyclerAdapter extends RecyclerView.Adapter<ProblemPostResyclerAdapter.ViewHolder> implements Filterable {
    private List<Problem_Model> list;
    private SharedPreferences sharedPreferences;
    private List<Problem_Model> listfiltr;


    public ProblemPostResyclerAdapter(List<Problem_Model> list) {
        this.list = list;
        this.listfiltr = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.user_name_surname.setText(listfiltr.get(position).getName());
        holder.title_post.setText(listfiltr.get(position).getAddress());
        holder.post_description.setText(listfiltr.get(position).getProblemDescription());
        holder.problem_category.setText(listfiltr.get(position).getProblemsType());
        if (!listfiltr.get(position).getUserimg().equals("")) {
            Picasso.get().load(listfiltr.get(position).getUserimg()).into(holder.user_img);
        }
        if (!listfiltr.get(position).getProblemimg().equals(""))
            Picasso.get().load(listfiltr.get(position).getProblemimg()).into(holder.post_img);
        holder.countLike.setText(String.valueOf(listfiltr.get(position).getLike()));
        if (isLike(holder, position)) {
            holder.like.setClickable(false);
            holder.like.setImageResource(R.drawable.ic_thumb_up_blue_24dp);

        } else {
            holder.like.setOnClickListener(view -> {
                int likecount = listfiltr.get(position).getLike();
                listfiltr.get(position).setLike(++likecount);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> hashmap = new HashMap<>();
                hashmap.put("like", likecount);
                hashmap.put("islike", true);
                db.collection(Constants.problem).document(listfiltr.get(position).getId())
                        .set(hashmap, SetOptions.merge());
                holder.like.setClickable(false);
                holder.like.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                holder.like.setClickable(false);
                like(holder, position);
                notifyDataSetChanged();
            });
        }


    }

    private boolean isLike(ViewHolder holder, int position) {
        PostActivity activity = (PostActivity) holder.itemView.getContext();
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        boolean like = sharedPreferences.getBoolean(listfiltr.get(position).getId(), false);
        return like;


    }

    private void like(@NonNull ViewHolder holder, int position) {
        PostActivity activity = (PostActivity) holder.itemView.getContext();
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(listfiltr.get(position).getId(), true);
        editor.commit();
    }


    @Override
    public int getItemCount() {
        return listfiltr == null ? 0 : listfiltr.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listfiltr = list;
                } else {
                    List<Problem_Model> filtredList = new ArrayList<>();
                    for (Problem_Model s : list) {
                        if (s.getProblemsType().toLowerCase().contains(charString.toLowerCase())) {
                            filtredList.add(s);
                        }
                        if (s.getAddress().toLowerCase().contains(charString.toLowerCase())) {
                            filtredList.add(s);
                        }

                    }

                    listfiltr = filtredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listfiltr;
                filterResults.count = listfiltr.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                listfiltr = (ArrayList<Problem_Model>) results.values;
                notifyDataSetChanged();
            }
        };
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
        TextView problem_category;

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
            problem_category = itemView.findViewById(R.id.problem_category);
            comment.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), CommentActivity.class);
                intent.putExtra("id", listfiltr.get(getAdapterPosition()).getId());
                intent.putExtra("username", listfiltr.get(getAdapterPosition()).getName());
                intent.putExtra("imgurl", listfiltr.get(getAdapterPosition()).getUserimg());
                itemView.getContext().startActivity(intent);
            });


        }
    }

}
