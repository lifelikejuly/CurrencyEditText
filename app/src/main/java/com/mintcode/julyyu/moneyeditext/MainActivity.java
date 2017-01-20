package com.mintcode.julyyu.moneyeditext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MoneyEditText moneyEditText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moneyEditText = (MoneyEditText) findViewById(R.id.money_edittext);
        textView = (TextView) findViewById(R.id.tv_value);
    }

    public void getValue(View view){
        textView.setText(moneyEditText.getMoneyValue() + "");
    }
}
