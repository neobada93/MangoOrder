package com.todo.order.util;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Etc {

    public Etc() {

    }

    public String dateFormat(String oriDate, String fsOriginal, String fsTarget ) {
        SimpleDateFormat sdfOri = new SimpleDateFormat(fsOriginal);
        SimpleDateFormat sdfTar = new SimpleDateFormat(fsTarget);
        String out = null;
        try {
            Date date = sdfOri.parse( oriDate);
            out = sdfTar.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return out;
    }

    public String comma(String amt) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        if (TextUtils.isEmpty(amt)) {
            amt = "0";
        }
        long longAmt = Long.parseLong(amt);
        return decimalFormat.format(longAmt);
    }

    public String commaDouble(String amt) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        if (TextUtils.isEmpty(amt)) {
            amt = "0";
        }
        double longAmt = Double.parseDouble(amt);
        return decimalFormat.format(longAmt);
    }
}
