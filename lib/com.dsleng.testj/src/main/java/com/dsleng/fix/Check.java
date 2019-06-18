package com.dsleng.fix;

import org.apache.spark.unsafe.types.UTF8String;
import org.apache.spark.sql.catalyst.util.GenericArrayData;


public class Check {
	public void s1() {
		//GenericArrayData gd = new GenericArrayData();
		if (UTF8String.fromString("heelo*").contains(UTF8String.fromString("*"))) {
			System.out.print("found");
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Check o = new Check();
		o.s1();
	}

}

