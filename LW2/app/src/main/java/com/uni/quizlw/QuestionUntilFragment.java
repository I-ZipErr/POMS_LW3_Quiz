package com.uni.quizlw;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class QuestionUntilFragment extends Fragment {

    private OnClickListenerPlay onClickListenerPlay = new OnClickListenerPlay();
    //private int
    private TextView quizTextCounter, questionText, healthTextView;
    private Button topRightButton, topLeftButton, bottomRightButton, bottomLeftButton;
    private FloatingActionButton exitButton;

    private FirebaseFirestore db;
    private static String QUESTIONS_TABLE = "questions";

    private int health = 3;


    private int currentQuestionIndex = -1;
    private QuestionFragmentArgs questionFragmentArgs;
    private int questionNumber;

    private ArrayList<Question> questionArrayList = new ArrayList<>();


    public QuestionUntilFragment() {
        // Required empty public constructor
    }


    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        db.collection(QUESTIONS_TABLE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Question buffer;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                buffer = new Question(document.getString("question"),
                                        new ArrayList<String>(){
                                            {
                                                add(document.getString("1"));
                                                add(document.getString("2"));
                                                add(document.getString("3"));
                                                add(document.getString("4"));
                                            }},
                                        document.getString("correct_answer"));
                                buffer.shuffleAnswers();
                                questionArrayList.add(buffer);
                            }
                            Collections.shuffle(questionArrayList);
                        }
                    }
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question_until, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        quizTextCounter = view.findViewById(R.id.quiz_text_counter);
        questionText = view.findViewById(R.id.question_text);
        topLeftButton = view.findViewById(R.id.quiz_top_left_button);
        topRightButton = view.findViewById(R.id.quiz_top_right_button);
        bottomLeftButton = view.findViewById(R.id.quiz_bottom_left_button);
        bottomRightButton = view.findViewById(R.id.quiz_bottom_right_button);
        exitButton = view.findViewById(R.id.exit_button);
        healthTextView = view.findViewById(R.id.health);

        healthTextView.setText(Integer.toString(health));

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = QuestionFragmentDirections.actionQuestionFragmentToDialogFragment()
                        .setScore(currentQuestionIndex)
                        .setMaxScore(questionNumber);
                Navigation.findNavController(v).navigate(action);
            }
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                topRightButton.setOnClickListener(new OnClickListenerPlay());
                topLeftButton.setOnClickListener(new OnClickListenerPlay());
                bottomRightButton.setOnClickListener(new OnClickListenerPlay());
                bottomLeftButton.setOnClickListener(new OnClickListenerPlay());
                questionNumber = questionArrayList.size();
                loadNewQuestion();
            }
        },5000);


//        requireActivity().getOnBackPressedDispatcher().addCallback(this.getViewLifecycleOwner(), new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                NavDirections action = QuestionFragmentDirections.actionQuestionFragmentToDialogFragment()
//                        .setScore(currentQuestionIndex)
//                        .setMaxScore(questionNumber);
//                Navigation.findNavController(view).navigate(action);
//            }
//        });



        super.onViewCreated(view, savedInstanceState);


    }

    private class OnClickListenerPlay implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Animation anim = new AlphaAnimation(1.0f, 0.1f);
                    anim.setDuration(100);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(4);
                    v.startAnimation(anim);
                }
            });
            Button button = (Button) v;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(button.getText().toString().equals(questionArrayList.get(currentQuestionIndex).getCorrectAnswer())){
                        button.setBackgroundColor(getResources().getColor(R.color.button_correct_answer_color, getContext().getTheme()));
                    }
                    else {
                        button.setBackgroundColor(getResources().getColor(R.color.button_incorrect_answer_color, getContext().getTheme()));
                        health--;
                        healthTextView.setText(Integer.toString(health));
                    }
                }
            }, 620);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!button.getText().toString().equals(questionArrayList.get(currentQuestionIndex).getCorrectAnswer())){
                        if(health == 0) {
                            NavDirections action = QuestionFragmentDirections.actionQuestionFragmentToDialogFragment()
                                    .setScore(currentQuestionIndex)
                                    .setMaxScore(questionNumber);
                            Navigation.findNavController(v).navigate(action);
                            return;
                        }

                    }
                    else {
                        button.setBackgroundColor(getResources().getColor(R.color.button_common_color, getContext().getTheme()));
                        if (currentQuestionIndex + 1 == questionNumber) {
                            NavDirections action = QuestionFragmentDirections.actionQuestionFragmentToDialogFragment()
                                    .setScore(questionNumber)
                                    .setMaxScore(questionNumber);
                            Navigation.findNavController(v).navigate(action);
                            return;
                        }
                        loadNewQuestion();
                    }

                }

            }, 1650);


        }
    }

    private void loadNewQuestion(){
        currentQuestionIndex++;
        quizTextCounter.setText((currentQuestionIndex + 1) + " / " + questionNumber);
        questionText.setText(questionArrayList.get(currentQuestionIndex).getQuestion_text());
        topRightButton.setText(questionArrayList.get(currentQuestionIndex).getAnswers().get(0));
        topLeftButton.setText(questionArrayList.get(currentQuestionIndex).getAnswers().get(1));
        bottomLeftButton.setText(questionArrayList.get(currentQuestionIndex).getAnswers().get(2));
        bottomRightButton.setText(questionArrayList.get(currentQuestionIndex).getAnswers().get(3));


        topRightButton.setBackgroundColor(getResources().getColor(R.color.button_common_color, getContext().getTheme()));
        topLeftButton.setBackgroundColor(getResources().getColor(R.color.button_common_color, getContext().getTheme()));
        bottomLeftButton.setBackgroundColor(getResources().getColor(R.color.button_common_color, getContext().getTheme()));
        bottomRightButton.setBackgroundColor(getResources().getColor(R.color.button_common_color, getContext().getTheme()));
    }


}