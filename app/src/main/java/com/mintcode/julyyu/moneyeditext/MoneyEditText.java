package com.mintcode.julyyu.moneyeditext;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.BadParcelableException;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.IllegalFormatCodePointException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/21.
 */
public class MoneyEditText extends EditText implements TextWatcher{

    Context context;
    DecimalFormatSymbols decimalFormatSymbols;
    DecimalFormat decimalFormat;

    String text;
    String moneySymbol;

    public MoneyEditText(Context context) {
        super(context);
        initView(context);
    }

    public MoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    void initView(Context context){
        this.context = context;
        int inputType = InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
        this.setInputType(inputType);
        this.addTextChangedListener(this);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        Configuration configuration = getResources().getConfiguration();
        decimalFormatSymbols = new DecimalFormatSymbols(configuration.locale);
        decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingSize(3);
        moneySymbol = decimalFormatSymbols.getCurrencySymbol();
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String content = s.toString();
        if(TextUtils.isEmpty(content) || TextUtils.equals(text,s)){
            return;
        }
        if(content.contains(moneySymbol+".") || TextUtils.equals(s,".")){
            this.setText("");
            return;
        }
        int indexOf = content.indexOf(".");
        String decimals = "";
        if(indexOf > 0){
            decimals = content.substring(indexOf);
            content = content.substring(0,indexOf);
            Log.i("money decimals",decimals);
        }
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(content);
        StringBuilder builder = new StringBuilder();
        while(m.find()){
            builder.append(m.group());
            Log.i("money find",builder.toString());
        }
        if(builder != null){
            content = builder.toString();
            Log.i("money content",content);
        }
        try {
            Integer num = Integer.valueOf(content);
            Log.i("money num",num + "");
            content = moneySymbol + decimalFormat.format(num) + decimals;
            Log.i("money", content);
            text = content;
            this.setText(text);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        text = content;
        this.setSelection(this.getText().length());
    }
}
