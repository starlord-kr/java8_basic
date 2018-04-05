package com.joochang.stream;

import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Java8 in Action
 * 7. 병렬 데이터 처리와 성능
 *
 * for문과 Stream.iterate에서 parallel 사용 시 주의점
 *
 */
public class ParallelStream {

    public static long iterativeSum(long n) {
        long result = 0L;
        for(int i = 1; i <= n; i++) {
            result += i;
        }
        return result;
    }

    public static long sequentialSum(long n) {
        // Stream : iterator는 long 이 Long 으로 오토박싱, 반대로 언박싱되는 비용 발생.
        // iterator는 병렬로 실행될 수 있도록 독립적인 청크로 분할하기가 어렵다.
        return Stream.iterate(1L, i -> i+1)
                .limit(n)
                .reduce(0L, Long::sum);
    }

    public static long parallelSum(long n) {
        // Stream : iterator는 long 이 Long 으로 오토박싱, 반대로 언박싱되는 비용 발생.
        // iterator는 병렬로 실행될 수 있도록 독립적인 청크로 분할하기가 어렵다.
        return Stream.iterate(1L, i -> i+1)
                .limit(n)
                .parallel() // 스트림을 병렬 스트림으로 변환
                .reduce(0L, Long::sum);
    }

    public static long rangeSum(long n) {
        // LongStream : 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라진다.
        return LongStream.rangeClosed(1, n)
                .reduce(0L, Long::sum);
    }

    public static long paraelleRangeSum(long n) {
        // LongStream : 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라진다.
        return LongStream.rangeClosed(1, n)
                .parallel()
                .reduce(0L, Long::sum);
    }

    public static long measureSumPerf(Function<Long, Long> adder, long n) {
        long fastest = Long.MAX_VALUE;
        for(int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            long sum = adder.apply(n);
            long duration = (System.nanoTime() - start) / 1_000_000;
            // System.out.println("Result: " + sum);
            if(duration < fastest) {
                fastest = duration;
            }
        }
        return fastest;
    }

    public static void main(String[] args) {

        long iterativeSum = ParallelStream.iterativeSum(10_000_000);
        System.out.println("ParallelStream.iterativeSum=" + iterativeSum);
        System.out.println("iterativeSum sum don in: " +
                measureSumPerf(ParallelStream::iterativeSum, 10_000_000) + " msecs\n");

        long sequentialSum = ParallelStream.sequentialSum(10_000_000);
        System.out.println("ParallelStream.sequentialSum=" + sequentialSum);
        System.out.println("sequentialSum sum don in: " +
                measureSumPerf(ParallelStream::sequentialSum, 10_000_000) + " msecs\n");

        long parallelSum = ParallelStream.parallelSum(10_000_000);
        System.out.println("ParallelStream.parallelSum=" + parallelSum);
        System.out.println("parallelSum sum don in: " +
                measureSumPerf(ParallelStream::parallelSum, 10_000_000) + " msecs\n");

        long rangeSum = ParallelStream.rangeSum(10_000_000);
        System.out.println("ParallelStream.rangeSum=" + rangeSum);
        System.out.println("rangeSum sum don in: " +
                measureSumPerf(ParallelStream::rangeSum, 10_000_000) + " msecs\n");

        long paraelleRangeSum = ParallelStream.paraelleRangeSum(10_000_000);
        System.out.println("ParallelStream.paraelleRangeSum=" + paraelleRangeSum);
        System.out.println("rangeSum sum don in: " +
                measureSumPerf(ParallelStream::paraelleRangeSum, 10_000_000) + " msecs\n");
    }

}
