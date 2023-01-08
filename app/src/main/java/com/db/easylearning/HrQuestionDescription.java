package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class HrQuestionDescription extends AppCompatActivity {

    private TextView txtHeadText,txtTrapText,txtBestAnswer;
    private String question,trapText,bestAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_question_description);
        txtHeadText=findViewById(R.id.txtHeadText);
        txtTrapText=findViewById(R.id.txtTrapText);
        txtBestAnswer=findViewById(R.id.txtBestAnswer);
        question=getIntent().getStringExtra("question");
        trapText=getIntent().getStringExtra("trapText");
        bestAnswer=getIntent().getStringExtra("bestAnswer");

        txtHeadText.setText(question);
        txtTrapText.setText(Html.fromHtml(trapText));
        txtBestAnswer.setText(Html.fromHtml(bestAnswer));



    }
}