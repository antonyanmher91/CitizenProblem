package com.example.problem;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.problem.adapter.CommentsRecviewAdapter;
import com.example.problem.model.CommentsModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class CommentActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String imgurl;
    private FirebaseFirestore db;
    private CommentsRecviewAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton button;
    private EditText editText;
    private List<CommentsModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        recyclerView = findViewById(R.id.recycler_view);
        button = findViewById(R.id.btn_send);
        editText = findViewById(R.id.text_send);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("username");
        imgurl = getIntent().getStringExtra("imgurl");
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        readList();
        button.setOnClickListener(view -> {
            CommentsModel commentsModel = new CommentsModel(editText.getText().toString(), name, imgurl);
            db.collection(id)
                    .add(commentsModel);
            list.add(commentsModel);
            adapter.notifyDataSetChanged();
            editText.setText("");
        });
    }

    private void readList() {
        db.collection(id)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommentsModel model = document.toObject(CommentsModel.class);
                            list.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

        adapter = new CommentsRecviewAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommentActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


    }


}
