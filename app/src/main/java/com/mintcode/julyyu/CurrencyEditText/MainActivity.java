package com.mintcode.julyyu.currencyedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.julyyu.currencyedittext.CurrencyEditText;

public class MainActivity extends AppCompatActivity {

    CurrencyEditText currencyEditText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currencyEditText = (CurrencyEditText) findViewById(R.id.money_edittext);
        textView = (TextView) findViewById(R.id.tv_value);
    }

    public void getValue(View view){
        textView.setText(currencyEditText.getMoneyValue() + "");
    }
}
