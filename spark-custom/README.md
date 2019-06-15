This project is meant to download a Spark source and update it with native functions for the emotion detection algorithm:

The following steps are executed:

1. wget https://github.com/apache/spark/archive/v2.4.3.tar.gz
2. spark-2.4.3/sql/core/src/main/scala/org/apache/spark/sql/SPFunctions.scala
3. spark-2.4.3/sql/catalyst/src/main/scala/org/apache/spark/sql/catalyst/expressions/SPEmo.scala
4. Compile and Publish
