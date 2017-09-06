package io.totokaka.strikk.processor;

public class StrikkPackage {

    private static String packageName = null;

    public static String getPackageName() {
        return packageName;
    }

    public static void setPackageName(String packageName) {
        StrikkPackage.packageName = packageName;
    }
}
