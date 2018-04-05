package com.joochang.stream;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * Java8 in Action
 * 7. 병렬 데이터 처리와 성능
 *
 * fork and join test
 *
 * RecursiveTask를 상속받아 포크조인 프레임워크에서 사용할 태스크를 생성한다.
 * RecursiveTask 안에서는 ForkJoinPool의 invoke 메서드를 사용하지 말아야한다.
 */
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

    private final long[] numbers; // 더할 숫자 배열
    private final int start;
    private final int end;
    public static final long THRESHOLD = 10000; // 이 값 이하의 서브태스크는 더 이상 분할할 수 없다.

    private static int COUNT = 1;
    private final String name;


    /**
     * 메인 태스크를 생성할 때 사용할 공개 생성자.
     * @param numbers
     */
    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length, "");
    }

    /**
     * 메인 태스크의 서브태스크를 재귀적으로 만들 때 사용할 비공개 생성자.
     * @param numbers
     * @param start
     * @param end
     */
    private ForkJoinSumCalculator(long[] numbers, int start, int end, String name) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
        this.name = name;

        System.out.println("Name="+name+ ", COUNT=" + COUNT + ", start="+start +", end="+end);
        System.out.println();
        COUNT++;
    }

    /**
     * RecursiveTask의 추상 메서드 오버라이드
     *
     * join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다.
     * 따라서 두 서브태스크가 모두 시작된 다음에 join을 호출해야 한다.
     *
     * 하나는 fork 다른 하나는 compute 를 해야 한 태스크에는 같은 스레드를 재사용할 수 있으므로 풀에서 불필요한 태스크를 할당하는 오버헤드를 피할 수 있다.
     *
     * @return
     */
    @Override
    protected Long compute() {
        int length = end - start; // 이 태스크에서 더할 배열의 길이
        if(length <= THRESHOLD) {
            return computeSequentially(); // 기준값과 같거나 작으면 순차적으로 결과를 계산한다.
        }

        // 배열의 첫 번째 절반을 더하도록 서브태스크를 생성한다.
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length/2, "LEFT");
        leftTask.fork(); // ForkJoinPool의 다른 스레드로 새로 생성한 태스크를 비동기로 실행한다.

        // 배열의 나머지 절반을 더하도록 서브태스크를 생성한다.
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length/2, end, "RIGHT");
        rightTask.compute();

        Long rightResult = rightTask.compute(); // 두 번째 서브태스크를 동기 실행한다. 이 때 추가로 분할이 일어날 수 있다.
        Long leftResult = leftTask.join(); // 첫 번째 서브태스크의 결과를 읽거나 아직 결과가 없으면 기다린다.

        return leftResult + rightResult; // 두 서브 태스크의 결과를 조합한 값이 이 태스크의 결과다.
    }

    /**
     * 더 분할할 수 없을 때 서브태스크의 결과를 계산하는 단순한 알고리즘.
     * @return
     */
    private long computeSequentially() {
        long sum = 0;
        for(int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }

    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);

        // 일반적으로 애플리케이션에서는 둘 이상의 ForkJoinPool을 사용하지 않는다.(싱글턴으로 사용하기를 권장)
        // 인수가 없는 디폴트 생성자를 이용했는데 이는 JVM에서 이용할 수 있는 모든 프로세서가 자유롭게 풀에 접근할 수 있음을 의미한다.
        // 순차코드에서 병렬 계산을 시작할 때만 invoke를 사용한다.
        return new ForkJoinPool().invoke(task);
    }

    /**
     * 병렬 스트림에서 살펴본 것처럼 멀티코어에 포크조인 프레임워크를 사용하는 것이 순차처리보다 무조건 빠를 거라는 생각은 버려야 한다.
     * 병렬 처리로 성능을 개선하려면 태스크를 여러 독립적인 서브태스크로 분할할 수 있어야 한다.
     * 각 서브태스크의 실행시간은 새로운 태스크를 포킹하는 데 드는 시간보다 길어야 한다.
     *
     * @param args
     */
    public static void main(String[] args) {
        long result = ForkJoinSumCalculator.forkJoinSum(100000);
        System.out.println("ForkJoinSumCalculator result : " + result);
    }
}
