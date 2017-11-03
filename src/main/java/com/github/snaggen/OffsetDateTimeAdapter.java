package com.github.snaggen;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {
    @Override
    public String marshal(final OffsetDateTime d) {
        return d != null ? d.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
    }

    @Override
    public OffsetDateTime unmarshal(final String s) {
    	try {
    		OffsetDateTime d = s != null && !"".equals(s) ? OffsetDateTime.parse(s, DateTimeFormatter.RFC_1123_DATE_TIME) : null;
    		return d;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        
		return null;
    }
}
