package com.dsleng.fix;

import org.apache.spark.unsafe.types.UTF8String;
import org.apache.spark.sql.catalyst.util.GenericArrayData;
import org.apache.spark.sql.catalyst.util.ArrayData;


public class Check {
	public void s1() {
		//String[] myStringArray = new String[3];
		UTF8String[] myStringArray = {UTF8String.fromString("hello*"),UTF8String.fromString("bye")};//{"a", "b", "c"};
		GenericArrayData gd = new GenericArrayData(myStringArray);
		ArrayData ad = (ArrayData)gd;
		for(int i=0; i < ad.numElements();i++) {
			if (ad.getUTF8String(i).contains(UTF8String.fromString("*"))) {
				System.out.print("problem");
			}
			UTF8String v = ad.getUTF8String(i);
			System.out.print(v);
		}
		for(int i=0; i < gd.numElements();i++) {
			UTF8String v = gd.getUTF8String(i);
			System.out.print(v);
		}
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

