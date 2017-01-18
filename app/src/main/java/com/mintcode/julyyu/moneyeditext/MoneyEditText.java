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
import java.util.IllegalFormatException;
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
//        decimalFormat.setMaximumFractionDigits(3);
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
        //清空特殊的显示内容
        if(content.contains(moneySymbol+".") || TextUtils.equals(s,".")){
            this.setText("");
            return;
        }
        //去货币符号
        content = content.replace(moneySymbol,"");
        //整数小数分离
        String decimals = "";
        String integer = "";
        String[] segments = content.split("\\.");
        switch (segments.length){
            case 2:
                integer = segments[0];
                decimals = segments[1];
                break;
            case 1:
                integer = segments[0];
                if(content.indexOf(".") > 0){
                    decimals = "0";
                }else{
                    decimals = "";
                }
                break;
            case 0:
                integer = content;
                break;
        }
        //截取数字字符
        integer = filterStringNum(integer);
        decimals = filterStringNum(decimals);
        //重新组装货币显示字符串
        Integer integerNum = str2Int(integer);
        Integer decimalsNum = str2Int(decimals);
        decimals = formatToCurrency(decimalsNum);
        decimals = TextUtils.isEmpty(decimals) ? "" : "." + decimals;
        content = moneySymbol + formatToCurrency(integerNum) + decimals;
        text = content;
        this.setText(text);
        this.setSelection(this.getText().length());
    }

    /**
     * 正则筛选数字
     * @param str
     * @return
     */
    private String filterStringNum(String str){
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(str);
        StringBuilder builder = new StringBuilder();
        while(m.find()){
            builder.append(m.group());
        }
        return builder == null ? "" : builder.toString();
    }
    private Integer str2Int(String str){
        Integer num = null;
        try {
            num = Integer.valueOf(str);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return num;
    }
    private String formatToCurrency(Integer integer){
        if (integer == null) return "";
        String str = "";
        try {
            str = decimalFormat.format(integer);
        }catch (IllegalFormatException e){
            e.printStackTrace();
        }
        return str;
    }
}
