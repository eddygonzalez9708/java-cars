package com.lambdaschool.javacars;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarLog implements Serializable {
    private final String text;
    private final String formmattedDate;

    public CarLog (String text) {
        this.text;
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        formmattedDate = dateFormat.format(date);
    }
}