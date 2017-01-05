// IMyAidlInterface.aidl
package com.umeng.soexample;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

        //向左
       void turnLeft();

       //向右
       void turnRight();

       //无变化
       void noChange();

       //摇一摇
       void shark();
}
