package com.example.hyojung.quest.JSON;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONArrayParser extends ArrayList<LinkedHashMap<String, Object>> {
    String input = "";
    public JSONArrayParser(String input) {
        input = input.substring(1, input.length() - 1);
        ArrayList<String> tempList = new ArrayList<String>();

        Pattern pattern = Pattern.compile("\\{.+?\\}");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            tempList.add(input.substring(matcher.start(), matcher.end()));
        }

        for (int i = 0; i < tempList.size(); i++) {
            LinkedHashMap<String, Object> tempHashMap = new LinkedHashMap<String, Object>();
            int pointer = 0;
            Pattern pattern2 = Pattern.compile("\\\".+?(\\\":)((\\\".+?\\\")|(-?[0-9]+)|null)");
            Matcher matcher2 = pattern2.matcher(tempList.get(i));
            while(matcher2.find()) {
                String tempEntry = tempList.get(i).substring(matcher2.start(), matcher2.end());
                tempHashMap.put(tempEntry.substring(0,  tempEntry.indexOf(':'))
                        , tempEntry.substring(tempEntry.indexOf(':')+1));
            }
            super.add(tempHashMap);
        }

        for (int i = 0; i < super.size(); i++) {
            LinkedHashMap<String, Object> tempHashMap = new LinkedHashMap<String, Object>();
            for (String key : super.get(i).keySet()) {
                Object value = null;
                String valueString = (String)super.get(i).get(key);
                if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
                    value = valueString.substring(1, valueString.length()-1);
                }
                else if (valueString.equals("null")){
                    value = null;
                }
                else if (valueString.matches("-?[1-9]+[0-9]*|0")) {
                    value = Long.valueOf(valueString);
                }
                tempHashMap.put(key.substring(1,  key.length() - 1), value);
            }
            super.set(i, tempHashMap);
        }
    }
}