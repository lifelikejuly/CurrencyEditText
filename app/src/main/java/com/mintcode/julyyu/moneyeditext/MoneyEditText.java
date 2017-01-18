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
 * Created by JulyYu on 2016/7/21.
 */
public class MoneyEditText extends EditText implements TextWatcher{

    /**
     * 货币符号
     */
    DecimalFormatSymbols decimalFormatSymbols;
    /**
     * 数字格式化
     */
    DecimalFormat decimalFormat;
    /**
     * 符号字符
     */
    String moneySymbol;
    /**
     * 数字文本
     */
    String text;
    /**
     * 最大数字长度
     */
    int numLength = 15;

    public MoneyEditText(Context context) {
        super(context);
        initView();
    }

    public MoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView(){
        int inputType = InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
        this.setInputType(inputType);
        this.addTextChangedListener(this);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(numLength)});
        Configuration configuration = getResources().getConfiguration();
        decimalFormatSymbols = new DecimalFormatSymbols(configuration.locale);
        moneySymbol = decimalFormatSymbols.getCurrencySymbol();
        decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingSize(3);
        decimalFormat.setMaximumFractionDigits(3);
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
