// ServerAIDL.aidl
package com.example.ipcserver;

// Declare any non-default types here with import statements

interface ServerAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

            int getPid();
            int getConnectionCount();
            void setDisplayedValue(String packageName, int pid, String data);
}