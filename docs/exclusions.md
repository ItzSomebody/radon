# Exclusions

**Note: You will need a basic understanding of internal class/method/field names to understand how Radon processes 
exclusions.**

Exclusions in Radon are regex-based and are matched using the code below:

```java
public boolean matches(String other, ExclusionType type) {
    if (type == ExclusionType.GLOBAL || type == exclusionType) {
        return true;
    }

    return invert != matcher.reset(other).matches();
}
```

**The use of `java.util.regex.Matcher#matches()` is intentional and should be considered whilst creating an exclusion.**

* When **classes** are checked for exclusion, the internal name is passed into `other`. e.g.
```java
// Check for java.lang.Object and java.lang.String
Exclusion exclusion = new Exclusion("java/lang/(Object|String)");

System.out.println(exclusion.matches("java/lang/Object")); // true
System.out.println(exclusion.matches("java/lang/String")); // true
System.out.println(exclusion.matches("java/lang/String isEmpty()Z")); // false
System.out.println(exclusion.matches("java/lang/Number")); // false
```
```java
// Check for all classes that start with "java.lang" in their internal name
Exclusion exclusion = new Exclusion("java/lang.*");

System.out.println(exclusion.matches("java/lang/Object")); // true
System.out.println(exclusion.matches("java/lang/String")); // true
System.out.println(exclusion.matches("java/lang/String isEmpty()Z")); // true - be careful when playing with ".*"
System.out.println(exclusion.matches("java/lang/Number")); // true
```

* When **methods** are checked for exclusion, `<internal owner name> <method name><internal method description>` is
passed into `other`. e.g.
```java
// Check for java.lang.String.isEmpty()
Exclusion exclusion = new Exclusion("java/lang/String isEmpty\\(\\)Z");

System.out.println(exclusion.matches("java/lang/String isEmpty()Z")); // true
System.out.println(exclusion.matches("java/lang/String toCharArray()[C")); // false
```
* When **fields** are checked for exclusion, `<internal owner name> <field name> <internal field description>` is
passed into `other`. e.g.
```java
// Check for java.lang.System.out
Exclusion exclusion = new Exclusion("java/lang/System out Ljava/io/PrintStream;");

System.out.println(exclusion.matches("java/lang/System out Ljava/lang/PrintStream;")); // true
System.out.println(exclusion.matches("java/lang/System err Ljava/lang/PrintStream;")); // false
```
