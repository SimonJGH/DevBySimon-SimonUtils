package com.simon.utils.utils;

import java.util.List;

/**
 * 集合工具
 * Created by Administrator on 2017/12/17.
 */

public class ListUtils {
    /**
     * 获取ArrayList中的最大值
     *
     * @param list
     * @return
     */
    public static double maxValue(List list) {
        try {
            double maxDevation = 0.0;
            int totalCount = list.size();
            if (totalCount >= 1) {
                double max = Double.parseDouble(list.get(0).toString());
                for (int i = 0; i < totalCount; i++) {
                    double temp = Double.parseDouble(list.get(i).toString());
                    if (temp > max) {
                        max = temp;
                    }
                }
                maxDevation = max;
            }
            return maxDevation;
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * 获取ArrayList中的最小值
     *
     * @param list
     * @return
     */
    public static double minValue(List list) {
        try {
            double mixDevation = 0.0;
            int totalCount = list.size();
            if (totalCount >= 1) {
                double min = Double.parseDouble(list.get(0).toString());
                for (int i = 0; i < totalCount; i++) {
                    double temp = Double.parseDouble(list.get(i).toString());
                    if (min > temp) {
                        min = temp;
                    }
                }
                mixDevation = min;
            }
            return mixDevation;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
