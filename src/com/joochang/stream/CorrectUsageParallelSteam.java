package com.joochang.stream;

import java.util.stream.LongStream;

/**
 * Java8 in Action
 * 7. 병렬 데이터 처리와 성능
 *
 * 병렬 스트림과 병렬 계산에서는 공유된 가변 상태를 피해야 한다.
 * 
 */
public class CorrectUsageParallelSteam {

    /**
     * @param n
     * @return
     */
    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).forEach(accumulator::add);
        return accumulator.total;
    }

    /**
     * 잘못된 결과를 도출한다.
     */
    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }

    public static void main(String[] args) {

        long sideEffectSum = CorrectUsageParallelSteam.sideEffectSum(10000);
        System.out.println("CorrectUsageParallelSteam.sideEffectSum = " + sideEffectSum);

        System.out.println();

        long sideEffectParallelSum = CorrectUsageParallelSteam.sideEffectParallelSum(10000);
        System.out.println("CorrectUsageParallelSteam.sideEffectParallelSum = " + sideEffectParallelSum);
    }
}
