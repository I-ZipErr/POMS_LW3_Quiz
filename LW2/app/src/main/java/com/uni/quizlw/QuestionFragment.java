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


public class QuestionFragment extends Fragment {

    private OnClickListenerPlay onClickListenerPlay = new OnClickListenerPlay();
    //private int
    private TextView quizTextCounter, questionText;
    private Button topRightButton, topLeftButton, bottomRightButton, bottomLeftButton;
    private FloatingActionButton exitButton;

    private FirebaseFirestore db;
    private static String QUESTIONS_TABLE = "questions";
    private static String SCOREBOARD_TABLE = "scoreboard";


    private int currentQuestionIndex = -1;
    private QuestionFragmentArgs questionFragmentArgs;
    private int questionNumber;

    private ArrayList<Question> questionArrayList = new ArrayList<>();


    public QuestionFragment() {
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
        if (getArguments() != null) {
            questionFragmentArgs = QuestionFragmentArgs.fromBundle(getArguments());
            questionNumber = questionFragmentArgs.getNavQuestionNumber();
        }
        else
            questionNumber = 2;

        db = FirebaseFirestore.getInstance();
        //addQuestions();
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
        return inflater.inflate(R.layout.fragment_question, container, false);
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
                    else button.setBackgroundColor(getResources().getColor(R.color.button_incorrect_answer_color, getContext().getTheme()));
                }
            }, 620);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!button.getText().toString().equals(questionArrayList.get(currentQuestionIndex).getCorrectAnswer())){
                        NavDirections action = QuestionFragmentDirections.actionQuestionFragmentToDialogFragment()
                                .setScore(currentQuestionIndex)
                                .setMaxScore(questionNumber);
                        Navigation.findNavController(v).navigate(action);

                        return;
                    }
                    button.setBackgroundColor(getResources().getColor(R.color.button_common_color, getContext().getTheme()));
                    if(currentQuestionIndex+1 == questionNumber)
                    {
                        NavDirections action = QuestionFragmentDirections.actionQuestionFragmentToDialogFragment()
                                .setScore(questionNumber)
                                .setMaxScore(questionNumber);
                        Navigation.findNavController(v).navigate(action);
                        return;
                    }
                    loadNewQuestion();

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
    }


    private void addQuestions(){
        Map<String, Object> question_buff = new HashMap<>();
        question_buff.put("question","Кто написал Горе от ума");
        question_buff.put("1","Пушкин");
        question_buff.put("2","Грибоедов");
        question_buff.put("3","Лермонтов");
        question_buff.put("4","Достоевский");
        question_buff.put("correct_answer","Грибоедов");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();

        question_buff.put("question","Как называется самая высокая гора в мире?");
        question_buff.put("1","Эверест");
        question_buff.put("2","Эльбрус");
        question_buff.put("3","Монблан");
        question_buff.put("4","Алтай");
        question_buff.put("correct_answer","Эверест");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();

        question_buff.put("question","Как называется самый большой океан на Земле?");
        question_buff.put("1","Тихий");
        question_buff.put("2","Атлантический");
        question_buff.put("3","Индийский");
        question_buff.put("4","Северный Ледовитый");
        question_buff.put("correct_answer","Тихий");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется столица Франции?");
        question_buff.put("1","Лондон");
        question_buff.put("2","Париж");
        question_buff.put("3","Рим");
        question_buff.put("4","Мадрид");
        question_buff.put("correct_answer","Париж");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая маленькая птица в мире?");
        question_buff.put("1","Колибри");
        question_buff.put("2","Воробей");
        question_buff.put("3","Синица");
        question_buff.put("4","Голубь");
        question_buff.put("correct_answer","Колибри");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Какой город является столицей России?");
        question_buff.put("1","Москва");
        question_buff.put("2","Санкт-Петербург");
        question_buff.put("3","Казань");
        question_buff.put("4","Екатеринбург");
        question_buff.put("correct_answer","Москва");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самое глубокое озеро в мире?");
        question_buff.put("1","Байкал");
        question_buff.put("2","Ладожское");
        question_buff.put("3","Онежское");
        question_buff.put("4","Каспийское");
        question_buff.put("correct_answer","Байкал");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая длинная река в мире?");
        question_buff.put("1","Нил");
        question_buff.put("2","Амазонка");
        question_buff.put("3","Миссисипи");
        question_buff.put("4","Янцзы");
        question_buff.put("correct_answer","Нил");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая большая пустыня в мире?");
        question_buff.put("1","Тар");
        question_buff.put("2","Тартар");
        question_buff.put("3","Тарзан");
        question_buff.put("4","Тартария");
        question_buff.put("correct_answer","Тар");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самый высокий водопад в мире?");
        question_buff.put("1","Ниагарский");
        question_buff.put("2","Анхель");
        question_buff.put("3","Виктория");
        question_buff.put("4","Кивач");
        question_buff.put("correct_answer","Анхель");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Какой язык программирования назван в честь дочери создателя?");
        question_buff.put("1","Python");
        question_buff.put("2","Ruby");
        question_buff.put("3","Java");
        question_buff.put("4","C++");
        question_buff.put("correct_answer","Python");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется программа, которая преобразует написанный код в машинный?");
        question_buff.put("1","Компилятор");
        question_buff.put("2","Интерпретатор");
        question_buff.put("3","Транслятор");
        question_buff.put("4","Кодер");
        question_buff.put("correct_answer","Компилятор");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется процесс создания программы?");
        question_buff.put("1","Кодирование");
        question_buff.put("2","Программирование");
        question_buff.put("3","Декомпиляция");
        question_buff.put("4","Дешифрация");
        question_buff.put("correct_answer","Программирование");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется ошибка в программе?");
        question_buff.put("1","Баг");
        question_buff.put("2","Вирус");
        question_buff.put("3","Сбой");
        question_buff.put("4","Брак");
        question_buff.put("correct_answer","Баг");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется набор инструкций, которые выполняются последовательно?");
        question_buff.put("1","Алгоритм");
        question_buff.put("2","Цикл");
        question_buff.put("3","Условие");
        question_buff.put("4","Конвейер");
        question_buff.put("correct_answer","Алгоритм");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется переменная, которая не изменяется в процессе выполнения программы?");
        question_buff.put("1","Константа");
        question_buff.put("2","Аргумент");
        question_buff.put("3","Параметр");
        question_buff.put("4","Статуя");
        question_buff.put("correct_answer","Константа");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется структура данных, которая состоит из элементов, расположенных в определённом порядке?");
        question_buff.put("1","Список");
        question_buff.put("2","Массив");
        question_buff.put("3","Множество");
        question_buff.put("4","Куча");
        question_buff.put("correct_answer","Массив");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется функция, которая возвращает значение?");
        question_buff.put("1","Рекурсивная");
        question_buff.put("2","Возвращающая");
        question_buff.put("3","Вызывающая");
        question_buff.put("4","Развращающая");
        question_buff.put("correct_answer","Возвращающая");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется метод, который позволяет определить, является ли строка числом?");
        question_buff.put("1","isdigit()");
        question_buff.put("2","isalpha()");
        question_buff.put("3","isnumeric()");
        question_buff.put("4","isnull()");
        question_buff.put("correct_answer","isnumeric()");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется оператор, который используется для сравнения двух значений?");
        question_buff.put("1","=");
        question_buff.put("2","==");
        question_buff.put("3","!=");
        question_buff.put("4","^=");
        question_buff.put("correct_answer","==");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая высокая гора в мире?");
        question_buff.put("1","Эверест");
        question_buff.put("2","Эльбрус");
        question_buff.put("3","Монблан");
        question_buff.put("4","Вектор");
        question_buff.put("correct_answer","Эверест");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самый большой остров на Земле?");
        question_buff.put("1","Гренландия");
        question_buff.put("2","Мадагаскар");
        question_buff.put("3","Новая Гвинея");
        question_buff.put("4","Австралия");
        question_buff.put("correct_answer","Гренландия");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется столица Италии?");
        question_buff.put("1","Рим");
        question_buff.put("2","Венеция");
        question_buff.put("3","Флоренция");
        question_buff.put("4","Деменция");
        question_buff.put("correct_answer","Рим");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая маленькая птица в России?");
        question_buff.put("1","Воробей");
        question_buff.put("2","Синица");
        question_buff.put("3","Зарянка");
        question_buff.put("4","Колибри");
        question_buff.put("correct_answer","Зарянка");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Какой город является столицей Японии?");
        question_buff.put("1","Токио");
        question_buff.put("2","Киото");
        question_buff.put("3","Осака");
        question_buff.put("4","Сакура");
        question_buff.put("correct_answer","Токио");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самое глубокое озеро в России?");
        question_buff.put("1","Ладожское");
        question_buff.put("2","Онежское");
        question_buff.put("3","Байкал");
        question_buff.put("4","Заренцо");
        question_buff.put("correct_answer","Байкал");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая длинная река в России?");
        question_buff.put("1","Волга");
        question_buff.put("2","Обь");
        question_buff.put("3","Енисей");
        question_buff.put("4","Дунай");
        question_buff.put("correct_answer","Волга");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самая большая пустыня в России?");
        question_buff.put("1","Тар");
        question_buff.put("2","Тартар");
        question_buff.put("3","Тарзан");
        question_buff.put("4","Таран");
        question_buff.put("correct_answer","Тар");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самый высокий водопад в России?");
        question_buff.put("1","Ниагарский");
        question_buff.put("2","Анхель");
        question_buff.put("3","Виктория");
        question_buff.put("4","Амозонка");
        question_buff.put("correct_answer","Анхель");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
        question_buff.put("question","Как называется самый известный вулкан в мире?");
        question_buff.put("1","Везувий");
        question_buff.put("2","Фудзияма");
        question_buff.put("3","Этна");
        question_buff.put("4","Дота");
        question_buff.put("correct_answer","Везувий");
        db.collection(QUESTIONS_TABLE).add(question_buff);
        question_buff.clear();
    }
}