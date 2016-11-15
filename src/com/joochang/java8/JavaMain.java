package com.joochang.java8;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.joochang.java8.MyFunction;

public class JavaMain {

	
	public void test() {
		

		MyFunction f1 = (a, b) ->  a > b ? a : b ;
		int val1 = f1.max(1, 2);
		System.out.println("-- val1 : " + val1);
		
		MyFunction f2 = (int c, int d) ->  {
			return c > d ? c : d;
		};
		int val2 = f1.max(3, 4);
		System.out.println("-- val2 : " + val2);
	}
	
	public void test1() {
		List<String> list = Arrays.asList("aaa", "ccc", "bbb");
		Collections.sort(list, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		for(String str : list) {
			System.out.println("-- str1 : " + str);
		}
		
		Collections.sort(list, (o1, o2) -> o2.compareTo(o1));
		for(String str : list) {
			System.out.println("-- str2 : " + str);
		}
		
	}
	
	public void test3() {
		Function<Integer, Integer> f = (Integer x) -> x + 1;
		int val = f.apply(1);
		System.out.println("-- Function val : " + val);
	}
	
	public static void main(String[] args) {
	
		JavaMain javaMain = new JavaMain();
		javaMain.test();
		
		javaMain.test1();
		
		
		javaMain.test3();
		// javaMain.test(()->{}, 0);
		
		
	    System.out.println("-- random : " + (int) (Math.random() * 10));
	}
}
