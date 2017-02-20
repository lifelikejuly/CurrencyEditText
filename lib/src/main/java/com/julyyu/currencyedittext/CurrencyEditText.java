package com.julyyu.currencyedittext;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JulyYu on 2016/7/21.
 */
public class CurrencyEditText extends EditText implements TextWatcher{

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
    /**
     *
     */
    int numdecimal = 4;
    /**
     * 是否有小数点
     */
    boolean hasDecimalPoint = false;
    /**
     * 是否显示货币符号
     */
    boolean hasSymbol = true;
    /**
     * 是否显示小数部分
     */
    boolean hasDecimal = true;
    /**
     * 小数第一位是否为0
     */
    boolean hasDecimalZero = false;

    public CurrencyEditText(Context context) {
        super(context);
        initView();
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParameters(context,attrs);
        initView();
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParameters(context,attrs);
        initView();
    }

    private void initParameters(Context context, AttributeSet attrs) {
       TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.MoneyEditTextAttrs);
        numLength = typedArray.getInteger(R.styleable.MoneyEditTextAttrs_numlength,15);
        numdecimal = typedArray.getInteger(R.styleable.MoneyEditTextAttrs_numdecimal,4);
        hasSymbol = typedArray.getBoolean(R.styleable.MoneyEditTextAttrs_symbolshow,true);
        numdecimal = numdecimal < 0 ? 0 : numdecimal;
        hasDecimal = numdecimal > 0 ? true : false;
    }

    void initView(){
        int inputType;
        if(hasDecimal){
            inputType = InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }else{
            inputType = InputType.TYPE_CLASS_NUMBER;
        }
        this.setInputType(inputType);
        this.addTextChangedListener(this);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(numLength)});
        Configuration configuration = getResources().getConfiguration();
        decimalFormatSymbols = new DecimalFormatSymbols(configuration.locale);
        moneySymbol = decimalFormatSymbols.getCurrencySymbol();
        moneySymbol = hasSymbol ? moneySymbol : "";
        decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingSize(3);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        Log.i("ontext","onTextChanged \n" + " CharSequence : " + text + " lengthBefore:" + lengthBefore + " lengthAfter:" + lengthAfter);

//        int end = this.getSelectionEnd();
//        int ssss = this.getSelectionStart();
//        Log.i("ontext","Changed" + " start" + ssss + "----end " + end + "");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.i("ontext","beforeTextChanged \n" + " CharSequence : " + s + " start" + start + " count:" + count + " after:" + after);
//        int end = this.getSelectionEnd();
//        int ssss = this.getSelectionStart();
//        Log.i("ontext","before" + " start" + ssss + "----end " + end + "");
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
                    hasDecimalPoint = true;
                }else{
                    hasDecimalPoint = false;
                }
                break;
            case 0:
                integer = content;
                break;
        }
        //截取数字字符
        integer = filterStringNum(integer);
        decimals = filterStringNum(decimals);
        if(hasDecimal && decimals.length() >= numdecimal){
            decimals = decimals.substring(0,numdecimal);
        }
        if(decimals != null && decimals.length() > 1){
            String zero = decimals.substring(0,1);
            hasDecimalZero = TextUtils.equals(zero,"0") ? true : false;
        }else if(decimals.length() <= 1){
            hasDecimalZero = false;
        }
        //重新组装货币显示字符串
        Long integerNum = str2Long(integer);
        Long decimalsNum = str2Long(decimals);
        if(hasDecimalPoint && hasDecimal){
            content = moneySymbol
                    + formatToCurrency(integerNum)
                    + "."
                    + decimalFormatToCurrency(decimalsNum);
        }else{
            content = moneySymbol
                    + formatToCurrency(integerNum);
        }
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
    private String filterSringFloat(String str){
        Pattern p = Pattern.compile("\\-*\\d+(\\.\\d+)?");
        Matcher m = p.matcher(str);
        StringBuilder builder = new StringBuilder();
        while(m.find()){
            builder.append(m.group());
        }
        return builder == null ? "" : builder.toString();
    }
    private Long str2Long(String str){
        Long num = null;
        try {
            num = Long.valueOf(str);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 货币格式化
     * @param lng
     * @return
     */
    private String formatToCurrency(Long lng){
        if (lng == null) return "";
        String str = "";
        try {
            str = decimalFormat.format(lng);
        }catch (IllegalFormatException e){
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 小数货币格式化
     * @param lng
     * @return
     */
    private String decimalFormatToCurrency(Long lng){
        if (lng == null) return "";
        String str = "";
        if(hasDecimalZero){
            int bit = lng.toString().length() + 1;
            StringBuilder builder = new StringBuilder();
            while (bit > 0){
                builder.append("0");
                bit --;
            }
            DecimalFormat df = new DecimalFormat(builder.toString());
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            try {
                str = df.format(lng);
            }catch (IllegalFormatException e){
                e.printStackTrace();
            }
        }else{
            try {
                str = decimalFormat.format(lng);
            }catch (IllegalFormatException e){
                e.printStackTrace();
            }
        }
        return str;
    }

    /**
     *
     * @return
     */
    public float getMoneyValue(){
        String content = this.getText().toString();
        if(TextUtils.isEmpty(content)){
            return 0;
        }
        content = filterSringFloat(content);
        return Float.valueOf(content);
    }
}
