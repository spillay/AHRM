package com.dsleng.fix;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.UnsafeArrayData;
import org.apache.spark.sql.catalyst.util.ArrayData;
import org.apache.spark.unsafe.types.UTF8String;

/* 005 */ // codegenStageId=1
/* 006 */ final class GeneratedIteratorForCodegenStage1 extends org.apache.spark.sql.execution.BufferedRowIterator {
/* 007 */   private Object[] references;
/* 008 */   private scala.collection.Iterator[] inputs;
/* 009 */   private org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter[] scan_mutableStateArray_1 = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter[2];
/* 010 */   private scala.collection.Iterator[] scan_mutableStateArray_0 = new scala.collection.Iterator[1];
/* 011 */   private org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter[] scan_mutableStateArray_2 = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter[8];
/* 012 */
/* 013 */   public GeneratedIteratorForCodegenStage1(Object[] references) {
/* 014 */     this.references = references;
/* 015 */   }
/* 016 */
/* 017 */   public void init(int index, scala.collection.Iterator[] inputs) {
/* 018 */     partitionIndex = index;
/* 019 */     this.inputs = inputs;
/* 020 */     wholestagecodegen_init_0_0();
/* 021 */     wholestagecodegen_init_0_1();
/* 022 */
/* 023 */   }
/* 024 */
/* 025 */   private void wholestagecodegen_init_0_1() {
/* 026 */     scan_mutableStateArray_2[6] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[1], 8);
/* 027 */     scan_mutableStateArray_2[7] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[1], 8);
/* 028 */
/* 029 */   }
/* 030 */
/* 031 */   protected void processNext() throws java.io.IOException {
/* 032 */     while (scan_mutableStateArray_0[0].hasNext()) {
/* 033 */       InternalRow scan_row_0 = (InternalRow) scan_mutableStateArray_0[0].next();
/* 034 */       ((org.apache.spark.sql.execution.metric.SQLMetric) references[0] /* numOutputRows */).add(1);
/* 035 */       boolean scan_isNull_1 = scan_row_0.isNullAt(1);
/* 036 */       ArrayData scan_value_1 = scan_isNull_1 ?
/* 037 */       null : (scan_row_0.getArray(1));
/* 038 */
/* 039 */       boolean scan_isNull_0 = scan_row_0.isNullAt(0);
/* 040 */       UTF8String scan_value_0 = scan_isNull_0 ?
/* 041 */       null : (scan_row_0.getUTF8String(0));
/* 042 */       boolean scan_isNull_2 = scan_row_0.isNullAt(2);
/* 043 */       int scan_value_2 = scan_isNull_2 ?
/* 044 */       -1 : (scan_row_0.getInt(2));
/* 045 */       boolean scan_isNull_3 = scan_row_0.isNullAt(3);
/* 046 */       ArrayData scan_value_3 = scan_isNull_3 ?
/* 047 */       null : (scan_row_0.getArray(3));
/* 048 */       boolean scan_isNull_4 = scan_row_0.isNullAt(4);
/* 049 */       ArrayData scan_value_4 = scan_isNull_4 ?
/* 050 */       null : (scan_row_0.getArray(4));
/* 051 */       boolean scan_isNull_5 = scan_row_0.isNullAt(5);
/* 052 */       int scan_value_5 = scan_isNull_5 ?
/* 053 */       -1 : (scan_row_0.getInt(5));
/* 054 */       boolean scan_isNull_6 = scan_row_0.isNullAt(6);
/* 055 */       ArrayData scan_value_6 = scan_isNull_6 ?
/* 056 */       null : (scan_row_0.getArray(6));
/* 057 */       boolean project_isNull_7 = true;
/* 058 */       UTF8String project_value_7 = null;
/* 059 */
/* 060 */       if (!scan_isNull_1) {
/* 061 */         boolean project_isNull_10 = true;
/* 062 */         UTF8String project_value_10 = null;
/* 063 */
/* 064 */         if (!scan_isNull_1) {
/* 065 */           project_isNull_10 = false; // resultCode could change nullability.
/* 066 */
/* 067 */           // start 4 ====================================================
/* 068 */           for (int project_i_0 = 0; project_i_0 < scan_value_1.numElements(); project_i_0 ++) {
/* 069 */             // start 1
/* 070 */             if (scan_value_1.isNullAt(project_i_0)) {
/* 071 */               project_isNull_10 = true;
/* 072 */             } else {
/* 073 */               if (scan_value_1.getUTF8String(project_i_0).compareTo(((UTF8String) references[2] /* literal */))==0){
/* 074 */                 project_isNull_10 = false;
/* 075 */
/* 076 */                 if (((UTF8String) references[3] /* literal */).equals(UTF8String.fromString(""))){
/* 077 */                   project_value_10 = ((UTF8String) references[2] /* literal */);
/* 078 */                 } else {
/* 079 */                   project_value_10 = UTF8String.concat(((UTF8String) references[3] /* literal */),UTF8String.fromString(","),((UTF8String) references[2] /* literal */));
/* 080 */                 }
/* 081 */
/* 082 */                 break;
/* 083 */               } else {
/* 084 */                 project_isNull_10 = false;
/* 085 */
/* 086 */                 if (((UTF8String) references[3] /* literal */).equals(UTF8String.fromString(""))){
/* 087 */                   project_value_10 = scan_value_1.getUTF8String(project_i_0);
/* 088 */                 } else {
/* 089 */                   project_value_10 = UTF8String.concat(((UTF8String) references[3] /* literal */),UTF8String.fromString(","),scan_value_1.getUTF8String(project_i_0));
/* 090 */                 }
/* 091 */
/* 092 */                 break;
/* 093 */               }
/* 094 */
/* 095 */             }
/* 096 */
/* 097 */           }
/* 098 */
/* 099 */         }
/* 100 */         if (!project_isNull_10) {
/* 101 */           project_isNull_7 = false; // resultCode could change nullability.
/* 102 */
/* 103 */           // start 4 ====================================================
/* 104 */           for (int project_i_1 = 0; project_i_1 < scan_value_1.numElements(); project_i_1 ++) {
/* 105 */             // start 1
/* 106 */             if (scan_value_1.isNullAt(project_i_1)) {
/* 107 */               project_isNull_7 = true;
/* 108 */             } else {
/* 109 */               if (scan_value_1.getUTF8String(project_i_1).compareTo(((UTF8String) references[1] /* literal */))==0){
/* 110 */                 project_isNull_7 = false;
/* 111 */
/* 112 */                 if (project_value_10.equals(UTF8String.fromString(""))){
/* 113 */                   project_value_7 = ((UTF8String) references[1] /* literal */);
/* 114 */                 } else {
/* 115 */                   project_value_7 = UTF8String.concat(project_value_10,UTF8String.fromString(","),((UTF8String) references[1] /* literal */));
/* 116 */                 }
/* 117 */
/* 118 */                 break;
/* 119 */               } else {
/* 120 */                 project_isNull_7 = false;
/* 121 */
/* 122 */                 if (project_value_10.equals(UTF8String.fromString(""))){
/* 123 */                   project_value_7 = scan_value_1.getUTF8String(project_i_1);
/* 124 */                 } else {
/* 125 */                   project_value_7 = UTF8String.concat(project_value_10,UTF8String.fromString(","),scan_value_1.getUTF8String(project_i_1));
/* 126 */                 }
/* 127 */
/* 128 */                 break;
/* 129 */               }
/* 130 */
/* 131 */             }
/* 132 */
/* 133 */           }
/* 134 */
/* 135 */         }
/* 136 */
/* 137 */       }
/* 138 */       scan_mutableStateArray_1[1].reset();
/* 139 */
/* 140 */       scan_mutableStateArray_1[1].zeroOutNullBytes();
/* 141 */
/* 142 */       if (scan_isNull_0) {
/* 143 */         scan_mutableStateArray_1[1].setNullAt(0);
/* 144 */       } else {
/* 145 */         scan_mutableStateArray_1[1].write(0, scan_value_0);
/* 146 */       }
/* 147 */
/* 148 */       if (scan_isNull_1) {
/* 149 */         scan_mutableStateArray_1[1].setNullAt(1);
/* 150 */       } else {
/* 151 */         // Remember the current cursor so that we can calculate how many bytes are
/* 152 */         // written later.
/* 153 */         final int project_previousCursor_0 = scan_mutableStateArray_1[1].cursor();
/* 154 */
/* 155 */         final ArrayData project_tmpInput_0 = scan_value_1;
/* 156 */         if (project_tmpInput_0 instanceof UnsafeArrayData) {
/* 157 */           scan_mutableStateArray_1[1].write((UnsafeArrayData) project_tmpInput_0);
/* 158 */         } else {
/* 159 */           final int project_numElements_0 = project_tmpInput_0.numElements();
/* 160 */           scan_mutableStateArray_2[4].initialize(project_numElements_0);
/* 161 */
/* 162 */           for (int project_index_0 = 0; project_index_0 < project_numElements_0; project_index_0++) {
/* 163 */             if (project_tmpInput_0.isNullAt(project_index_0)) {
/* 164 */               scan_mutableStateArray_2[4].setNull8Bytes(project_index_0);
/* 165 */             } else {
/* 166 */               scan_mutableStateArray_2[4].write(project_index_0, project_tmpInput_0.getUTF8String(project_index_0));
/* 167 */             }
/* 168 */
/* 169 */           }
/* 170 */         }
/* 171 */
/* 172 */         scan_mutableStateArray_1[1].setOffsetAndSizeFromPreviousCursor(1, project_previousCursor_0);
/* 173 */       }
/* 174 */
/* 175 */       if (scan_isNull_2) {
/* 176 */         scan_mutableStateArray_1[1].setNullAt(2);
/* 177 */       } else {
/* 178 */         scan_mutableStateArray_1[1].write(2, scan_value_2);
/* 179 */       }
/* 180 */
/* 181 */       if (scan_isNull_3) {
/* 182 */         scan_mutableStateArray_1[1].setNullAt(3);
/* 183 */       } else {
/* 184 */         // Remember the current cursor so that we can calculate how many bytes are
/* 185 */         // written later.
/* 186 */         final int project_previousCursor_1 = scan_mutableStateArray_1[1].cursor();
/* 187 */
/* 188 */         final ArrayData project_tmpInput_1 = scan_value_3;
/* 189 */         if (project_tmpInput_1 instanceof UnsafeArrayData) {
/* 190 */           scan_mutableStateArray_1[1].write((UnsafeArrayData) project_tmpInput_1);
/* 191 */         } else {
/* 192 */           final int project_numElements_1 = project_tmpInput_1.numElements();
/* 193 */           scan_mutableStateArray_2[5].initialize(project_numElements_1);
/* 194 */
/* 195 */           for (int project_index_1 = 0; project_index_1 < project_numElements_1; project_index_1++) {
/* 196 */             if (project_tmpInput_1.isNullAt(project_index_1)) {
/* 197 */               scan_mutableStateArray_2[5].setNull8Bytes(project_index_1);
/* 198 */             } else {
/* 199 */               scan_mutableStateArray_2[5].write(project_index_1, project_tmpInput_1.getUTF8String(project_index_1));
/* 200 */             }
/* 201 */
/* 202 */           }
/* 203 */         }
/* 204 */
/* 205 */         scan_mutableStateArray_1[1].setOffsetAndSizeFromPreviousCursor(3, project_previousCursor_1);
/* 206 */       }
/* 207 */
/* 208 */       if (scan_isNull_4) {
/* 209 */         scan_mutableStateArray_1[1].setNullAt(4);
/* 210 */       } else {
/* 211 */         // Remember the current cursor so that we can calculate how many bytes are
/* 212 */         // written later.
/* 213 */         final int project_previousCursor_2 = scan_mutableStateArray_1[1].cursor();
/* 214 */
/* 215 */         final ArrayData project_tmpInput_2 = scan_value_4;
/* 216 */         if (project_tmpInput_2 instanceof UnsafeArrayData) {
/* 217 */           scan_mutableStateArray_1[1].write((UnsafeArrayData) project_tmpInput_2);
/* 218 */         } else {
/* 219 */           final int project_numElements_2 = project_tmpInput_2.numElements();
/* 220 */           scan_mutableStateArray_2[6].initialize(project_numElements_2);
/* 221 */
/* 222 */           for (int project_index_2 = 0; project_index_2 < project_numElements_2; project_index_2++) {
/* 223 */             if (project_tmpInput_2.isNullAt(project_index_2)) {
/* 224 */               scan_mutableStateArray_2[6].setNull8Bytes(project_index_2);
/* 225 */             } else {
/* 226 */               scan_mutableStateArray_2[6].write(project_index_2, project_tmpInput_2.getUTF8String(project_index_2));
/* 227 */             }
/* 228 */
/* 229 */           }
/* 230 */         }
/* 231 */
/* 232 */         scan_mutableStateArray_1[1].setOffsetAndSizeFromPreviousCursor(4, project_previousCursor_2);
/* 233 */       }
/* 234 */
/* 235 */       if (scan_isNull_5) {
/* 236 */         scan_mutableStateArray_1[1].setNullAt(5);
/* 237 */       } else {
/* 238 */         scan_mutableStateArray_1[1].write(5, scan_value_5);
/* 239 */       }
/* 240 */
/* 241 */       if (scan_isNull_6) {
/* 242 */         scan_mutableStateArray_1[1].setNullAt(6);
/* 243 */       } else {
/* 244 */         // Remember the current cursor so that we can calculate how many bytes are
/* 245 */         // written later.
/* 246 */         final int project_previousCursor_3 = scan_mutableStateArray_1[1].cursor();
/* 247 */
/* 248 */         final ArrayData project_tmpInput_3 = scan_value_6;
/* 249 */         if (project_tmpInput_3 instanceof UnsafeArrayData) {
/* 250 */           scan_mutableStateArray_1[1].write((UnsafeArrayData) project_tmpInput_3);
/* 251 */         } else {
/* 252 */           final int project_numElements_3 = project_tmpInput_3.numElements();
/* 253 */           scan_mutableStateArray_2[7].initialize(project_numElements_3);
/* 254 */
/* 255 */           for (int project_index_3 = 0; project_index_3 < project_numElements_3; project_index_3++) {
/* 256 */             if (project_tmpInput_3.isNullAt(project_index_3)) {
/* 257 */               scan_mutableStateArray_2[7].setNull8Bytes(project_index_3);
/* 258 */             } else {
/* 259 */               scan_mutableStateArray_2[7].write(project_index_3, project_tmpInput_3.getUTF8String(project_index_3));
/* 260 */             }
/* 261 */
/* 262 */           }
/* 263 */         }
/* 264 */
/* 265 */         scan_mutableStateArray_1[1].setOffsetAndSizeFromPreviousCursor(6, project_previousCursor_3);
/* 266 */       }
/* 267 */
/* 268 */       if (project_isNull_7) {
/* 269 */         scan_mutableStateArray_1[1].setNullAt(7);
/* 270 */       } else {
/* 271 */         scan_mutableStateArray_1[1].write(7, project_value_7);
/* 272 */       }
/* 273 */       append((scan_mutableStateArray_1[1].getRow()));
/* 274 */       if (shouldStop()) return;
/* 275 */     }
/* 276 */   }
/* 277 */
/* 278 */   private void wholestagecodegen_init_0_0() {
/* 279 */     scan_mutableStateArray_0[0] = inputs[0];
/* 280 */     scan_mutableStateArray_1[0] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter(7, 160);
/* 281 */     scan_mutableStateArray_2[0] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[0], 8);
/* 282 */     scan_mutableStateArray_2[1] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[0], 8);
/* 283 */     scan_mutableStateArray_2[2] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[0], 8);
/* 284 */     scan_mutableStateArray_2[3] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[0], 8);
/* 285 */     scan_mutableStateArray_1[1] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter(8, 192);
/* 286 */     scan_mutableStateArray_2[4] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[1], 8);
/* 287 */     scan_mutableStateArray_2[5] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeArrayWriter(scan_mutableStateArray_1[1], 8);
/* 288 */
/* 289 */   }
/* 290 */
/* 291 */ }