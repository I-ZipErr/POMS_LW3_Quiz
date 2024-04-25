package com.uni.quizlw;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavHost;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class DialogFragment extends Fragment {

    private Button finishButton;
    private TextInputEditText nameEditText;
    private TextView scoreTextView;

    private DialogFragmentArgs dialogFragmentArgs;
    private int score, maxScore;

    private FirebaseFirestore db;
    private static String SCOREBOARD_TABLE = "scoreboard";

    public DialogFragment() {
        // Required empty public constructor
    }

    public static DialogFragment newInstance() {
        DialogFragment fragment = new DialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            dialogFragmentArgs = DialogFragmentArgs.fromBundle(getArguments());
            score = dialogFragmentArgs.getScore();
            maxScore = dialogFragmentArgs.getMaxScore();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment fragment = this;
        finishButton = view.findViewById(R.id.dialog_fragment_button);
        nameEditText = view.findViewById(R.id.dialog_fragment_text_input);
        scoreTextView = view.findViewById(R.id.score_text_view);

        scoreTextView.setText(score + " из " + maxScore);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = DialogFragmentDirections.actionDialogFragmentToHomeFragment();
//                    nameEditText.getText().toString()
//                            .setScore(score)
//                            .setMaxScore(maxScore);
                NavHostFragment.findNavController(fragment).navigate(action);
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        Map<String, Object> score_line = new HashMap<>();
        score_line.put("name", nameEditText.getText().toString());
        score_line.put("score", score);
        score_line.put("max_score", maxScore);
        db.collection(SCOREBOARD_TABLE).add(score_line);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}