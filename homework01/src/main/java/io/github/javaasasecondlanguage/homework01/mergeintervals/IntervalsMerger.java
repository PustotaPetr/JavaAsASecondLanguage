package io.github.javaasasecondlanguage.homework01.mergeintervals;

import java.util.Arrays;

public class IntervalsMerger {
    /**
     * Given array of intervals, merge overlap intervals and sort them by start in ascending order
     * Interval is defined as [start, end] where start < end
     * <p>
     * Examples:
     * [[1,3][2,4][5,6]] -> [[1,4][5,6]]
     * [[1,2][2,3]] -> [[1,3]]
     * [[1,4][2,3]] -> [[1,4]]
     * [[5,6][1,2]] -> [[1,2][5,6]]
     *
     * @param diap is a nullable array of pairs [start, end]
     * @return merged intervals
     * @throws IllegalArgumentException if intervals is null
     */
    public int[][] merge(int[][] diap) {
        if (diap == null) {
            throw new IllegalArgumentException("intervals is null");
        }
        if (diap.length == 0) {
            return diap;
        }


        simpleSort(diap);

        int[][] newIntervals = new int[diap.length][2];

        int baseInd = 0;
        int newBaseIndex = 0;
        int compareInd = 1;

        while (true) {
            if (compareInd >= diap.length) {
                newIntervals[newBaseIndex] = diap[baseInd];
                newBaseIndex++;
                break;
            }

            if (diap[baseInd][1] >= diap[compareInd][1]) {
                //Интервал целиком входит в предыдущий
                compareInd++;
                continue;
            }

            if (diap[baseInd][1] >= diap[compareInd][0] && diap[baseInd][1] < diap[compareInd][1]) {
                //пересечение интервалов. подправим верхнюю границу базового интервала.
                diap[baseInd][1] = diap[compareInd][1];
                compareInd++;
                continue;
            }

            if (diap[baseInd][1] < diap[compareInd][0]) {
                //нет пересечения. формируем элемент выходного массива.
                newIntervals[newBaseIndex] = diap[baseInd];
                newBaseIndex++;
                baseInd = compareInd;
                compareInd++;
            }

        }
        return Arrays.copyOfRange(newIntervals, 0, newBaseIndex);

    }

    private static void simpleSort(int[][] sortArray) {
        int[] temp;
        for (int i = 1; i < sortArray.length; i++) {
            int j = i;
            while (j > 0 && sortArray[j - 1][0] > sortArray[j][0]) {
                temp = sortArray[j - 1];
                sortArray[j - 1] = sortArray[j];
                sortArray[j] = temp;
                j--;
            }
        }
    }
}
