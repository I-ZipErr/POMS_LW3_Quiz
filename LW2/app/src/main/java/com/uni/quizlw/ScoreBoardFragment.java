package com.uni.quizlw;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uni.quizlw.placeholder.PlaceholderContent;

import java.util.ArrayList;

public class ScoreBoardFragment extends Fragment {


    private FirebaseFirestore db;
    private static String SCOREBOARD_TABLE = "scoreboard";

    private RecyclerView recyclerView;
    private ArrayList<ScoreLine> arrayList = new ArrayList<>();
    private ScoreboardRecyclerAdapter adapter;
    private FloatingActionButton exitButton;

    public ScoreBoardFragment() {
    }

    public static ScoreBoardFragment newInstance(int columnCount) {
        ScoreBoardFragment fragment = new ScoreBoardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        db = FirebaseFirestore.getInstance();

        db.collection(SCOREBOARD_TABLE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ScoreLine buffer;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                buffer = new ScoreLine(document.getString("name"),
                                        (int)(float)document.getLong("score"),
                                        (int)(float)document.getLong("max_score"));
                                arrayList.add(buffer);
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_score_board_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.list);
        exitButton = view.findViewById(R.id.exit_button);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter = new ScoreboardRecyclerAdapter(arrayList);
                recyclerView.setAdapter(adapter);
            }
        }, 3000);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}