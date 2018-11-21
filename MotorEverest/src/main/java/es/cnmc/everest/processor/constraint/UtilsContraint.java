package es.cnmc.everest.processor.constraint;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dromera on 27/04/2016.
 */
public class UtilsContraint {

    private static String SEPARATOR_SET_VALUES = ",";
    private static String SEPARATOR_LIST = ";";


    public static String[] getSetValuesAtPosition(String values, String defaultValue, int position) {
        String[] tokenValues = StringUtils.split(values, SEPARATOR_LIST);
        String[] correctValues;
        if (position == -1) {
            correctValues = new String[]{defaultValue};
        } else {
            correctValues = StringUtils.split(StringUtils.removePattern(tokenValues[position], "[\\{\\}]"), SEPARATOR_SET_VALUES);
        }
        return correctValues;
    }

    public static List<RangeItem> splitToRangeItem(String[] rowRange) {
        List<RangeItem> rangeItemList = new ArrayList<RangeItem>();
        RangeItem rangeItem;
        for (String rangeStr : rowRange) {
            rangeItem = new RangeItem();
            rangeItem.setValue(getDoubleInStr(rangeStr));
            rangeItem.setOperator(getOperatorInStr(rangeStr));
            rangeItemList.add(rangeItem);
        }
        return rangeItemList;
    }

    private static RangeItem.Operator getOperatorInStr(String str) {
        // importante el orden de los indexOf
        if (str.contains("<=")) {
            return RangeItem.Operator.MINUS_EQ;
        } else if (str.contains("<")) {
            return RangeItem.Operator.MINUS;
        } else if (str.contains(">=")) {
            return RangeItem.Operator.GREATER_EQ;
        } else if (str.contains(">")) {
            return RangeItem.Operator.GREATER;
        } else { // =
            return RangeItem.Operator.EQ;
        }
    }

    private static Double getDoubleInStr(String str) {
        return Double.parseDouble(str.replaceAll("[\b<>=]",""));
    }
}


