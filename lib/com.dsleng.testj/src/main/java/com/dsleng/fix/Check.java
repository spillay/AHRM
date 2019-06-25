package com.dsleng.fix;

import org.apache.spark.unsafe.types.CalendarInterval;
import org.apache.spark.unsafe.types.UTF8String;
import org.apache.spark.sql.catalyst.util.GenericArrayData;
import org.apache.spark.sql.catalyst.util.MapData;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Decimal;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.util.ArrayData;
import org.apache.spark.sql.types.StringType;
import scala.collection.*;


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
	public void s2() {
		Object references[] = new Object[2];
		GeneratedIteratorForCodegenStage1 o = new GeneratedIteratorForCodegenStage1(references);
		//Seq inputs[] = new Seq[2];
		//o.init(0, inputs);
	}
	public void s3() {
		UTF8String[] myStringArray = {UTF8String.fromString("hello*"),UTF8String.fromString("bye")};//{"a", "b", "c"};
		GenericArrayData add = new GenericArrayData(myStringArray);
		//gd.update(3,UTF8String.fromString("he"));
		System.out.println(add);
		UTF8String[] dd = {UTF8String.fromString("hello*")};
		//UTF8String[] vs = (UTF8String[])add.toSeq(DataTypes.StringType);
		UTF8String[] res = new UTF8String[]{UTF8String.fromString("hello*")};
		
		 int i = add.numElements(); 
		 System.out.println(i);
		 int n = ++i;  
		 System.out.println(n);
		 UTF8String[] newArray = new UTF8String[n];  
		 for(int cnt=0;cnt<i-1;cnt++)
		 {  
			 System.out.println(cnt);
		    newArray[cnt] = (UTF8String) add.get(cnt, DataTypes.StringType); 
		 }  
		 newArray[i-1] = UTF8String.fromString("last");
		 System.out.println(newArray.toString());
		 ArrayData.toArrayData(newArray);
		   
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Check o = new Check();
		o.s3();
		//o.s1();
		//Object[] references = {};
//		System.out.println(UTF8String.concat(UTF8String.fromString("one"),
//		UTF8String.fromString(","),
//		UTF8String.fromString("three")));
//		String s = "hello";
//		String[] r = s.split(",");
//		System.out.print(r.length);
		

}
}

