package pe.perujug.jep;

public class Jep507Demo {
    static void main() {
        Object a = 42;       // Integer
        Object b = 42L;      // Long
        Object c = 3.14d;    // Double
        Object d = "hi";     // String
        Object e = null;

        System.out.println("=== instanceof: Before vs Java 25 ===");
        beforeJava25Instanceof(a); beforeJava25Instanceof(b); beforeJava25Instanceof(c); beforeJava25Instanceof(d); beforeJava25Instanceof(e);
        java25Instanceof(a);  java25Instanceof(b);  java25Instanceof(c);  java25Instanceof(d);  java25Instanceof(e);

        System.out.println("\n=== switch(Object): Before vs Java 25 ===");
        System.out.println("beforeSwitch(a): " + beforeJava25Switch(a));
        System.out.println("beforeSwitch(b): " + beforeJava25Switch(b));
        System.out.println("beforeSwitch(c): " + beforeJava25Switch(c));
        System.out.println("beforeSwitch(d): " + beforeJava25Switch(d));
        System.out.println("beforeSwitch(e): " + beforeJava25Switch(e));
        System.out.println("afterSwitch(a):  " + java25Switch(a));
        System.out.println("afterSwitch(b):  " + java25Switch(b));
        System.out.println("afterSwitch(c):  " + java25Switch(c));
        System.out.println("afterSwitch(d):  " + java25Switch(d));
        System.out.println("afterSwitch(e):  " + java25Switch(e));

        System.out.println("\n=== switch(int) with guards (patterns) vs pre-J25 ===");
        System.out.println("primitiveSwitchGuards(10): " + primitiveSwitchGuards(10));
        System.out.println("primitiveSwitchGuards(0):  " + primitiveSwitchGuards(0));
        System.out.println("primitiveSwitchGuards(-5): " + primitiveSwitchGuards(-5));
        System.out.println("beforePrimitiveSwitch(10): " + beforePrimitiveSwitch(10));
        System.out.println("beforePrimitiveSwitch(0):  " + beforePrimitiveSwitch(0));
        System.out.println("beforePrimitiveSwitch(-5): " + beforePrimitiveSwitch(-5));
    }

    // ------------------------------------------------------------
    // 1) instanceof
    // ------------------------------------------------------------
    static void beforeJava25Instanceof(Object o) {
        if (o instanceof Integer) {
            int i = (Integer) o; // manual unboxing
            System.out.println("[before] int=" + i);
        } else if (o instanceof Long) {
            long l = (Long) o;   // manual unboxing
            System.out.println("[before] long=" + l);
        } else if (o instanceof Double) {
            double d = (Double) o;
            System.out.println("[before] double=" + d);
        } else if (o == null) {
            System.out.println("[before] null");
        } else {
            System.out.println("[before] other=" + o);
        }
    }

    static void java25Instanceof(Object o) {
        if (o instanceof int i && i > 10) {
            System.out.println("[J25] large int=" + i);
        } else if (o instanceof int i) {
            System.out.println("[J25] int=" + i);
        } else if (o instanceof long l) {
            System.out.println("[J25] long=" + l);
        } else if (o instanceof double d) {
            System.out.println("[J25] double=" + d);
        } else if (o == null) {
            System.out.println("[J25] null");
        } else {
            System.out.println("[J25] other=" + o);
        }
    }

    // ------------------------------------------------------------
    // 2) switch over Object
    // ------------------------------------------------------------
    static String beforeJava25Switch(Object o) {
        if (o == null) return "null";
        if (o instanceof Integer i) {
            return (i % 2 == 0) ? "even int" : "odd int";
        } else if (o instanceof Long l) {
            return "a long: " + l;
        } else if (o instanceof Double d) {
            return "a double: " + d;
        } else {
            return "other";
        }
    }

    static String java25Switch(Object o) {
        return switch (o) {
            case null               -> "null";
            case int i when i % 2 == 0 -> "even int";
            case int i              -> "odd int";
            case long l             -> "a long: " + l;
            case double d           -> "a double: " + d;
            default                 -> "other";
        };
    }

    // ------------------------------------------------------------
    // 3) switch over primitive with pattern guards
    // ------------------------------------------------------------
    static String beforePrimitiveSwitch(int v) {
        if (v > 0) return "positive";
        if (v == 0) return "zero";
        return "negative";
    }
    static String primitiveSwitchGuards(int v) {
        return switch (v) {
            case int i when i > 0 -> "positive";
            case int i when i == 0 -> "zero";
            case int i            -> "negative";
        };
    }
}
