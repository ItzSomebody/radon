# Radon Java Obfuscator ![Build Status](https://travis-ci.org/ItzSomebody/Radon.svg?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/07b0849ead3f47f1a6950a0353f43541)](https://www.codacy.com/app/ItzSomebody/Radon?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ItzSomebody/Radon&amp;utm_campaign=Badge_Grade)

Usage: `java -jar Radon-Program.jar ExampleConfig.yml`

Alternatively, you can also use `java -jar Radon-Program.jar --help` for help.

Example configuration:
```yaml
Input: "C:/Users/ItzSomebody/Desktop/Example.jar"
Output: "C:/Users/ItzSomebody/Desktop/Example-OBF.jar"
StringEncryption:
    Enabled: true
    Mode: Heavy
    StringPool: false
    Exclusions:
        - "Example String #1"
        - "Example String #2"
        - "Example String #3"
InvokeDynamic: Heavy
NumberObfuscation: Heavy
FlowObfuscation: Heavy
LocalVariables:
    Enabled: true
    Remove: true
LineNumbers:
    Enabled: true
    Remove: true
SourceName:
    Enabled: true
    Remove: true
SourceDebug:
    Enabled: true
    Remove: true
HideCode: true
Shuffler: true
Crasher: true
Renamer:
    Enabled: true
    Repackage: "NotTheDefaultPackage"
    AdaptResources:
        - "META-INF/MANIFEST.MF"
        - "Example.resource"
Optimizer:
    Enabled: true
    InlineGotoGoto: true
    InlineGotoReturn: true
    RemoveNopInstructions: true
Shrinker:
    Enabled: true
    RemoveAttributes: true
    RemoveDebug: true
    RemoveInvisibleAnnotations: true
    RemoveVisibleAnnotations: true
Watermarker:
    Enabled: true
    Message: "This copy belongs to ItzSomebody"
    Key: "SuperSecureKey"
Expiration:
    Enabled: true
    InjectJOptionPane: true
    Message: "Sorry, but your software trial has passed the expiration date. Please pay the ItzSomebody Corporation 30 billion USD to the paypal address non-existant@troll.email to receive the full version."
    Expires: "12/31/2018"
Dictionary: Spaces
TrashClasses: 30
Libraries:
    - "C:/Program Files/Java/jre1.8.0_xxx/lib/rt.jar"
    - "C:/Program Files/Java/jre1.8.0_xxx/lib/jce.jar"
Exclusions:
    - "Global: me/itzsomebody/example/excludeallthis.*"
    - "InvokeDynamic: me/itzsomebody/example/excludethisaswell.*"
```

# Configuration Format
## Input

The input key only takes one argument, a **string** providing the input file to the obfuscator.

## Output

The output key only takes one argument, a **string** providing the output file to the obfuscator to write the obfuscated result to.

## StringEncryption

The string encryption key takes several arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if the string encryption transformer should be enabled. |
| Mode | String | Determines which type of string encryption to apply. Valid modes are: **Light**, **Normal** and **Heavy**. |
| StringPool | Boolean | Determines if strings should also be pooled on top of the string encryption. |
| Exclusions | List of Strings | Strings to prevent from being encrypted/pooled. Anything strings found in the input which contain these strings will be left untouched in the output. |

## InvokeDynamic

The invokedynamic key only takes one argument: a **string** determining which kind of invokedynamic to apply to the output jar. Valid modes are **Light**, **Normal** and **Heavy**.

## NumberObfuscation

The number obfuscation key only takes one argument: a **string** determining which kind of number obfuscation to apply to the output jar. Valid modes are **Light**, **Normal** and **Heavy**.

## FlowObfuscation

The flow obfuscation key only takes one argument: a **string** determining which kind of flow obfuscation to apply to the output jar. Valid modes are **Light**, **Normal** and **Heavy**.

## LocalVariables

The local variables obfuscation key takes two arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if local variable names should be obfuscated. |
| Remove | Boolean | Determines if local variable names should be removed altogether. |

## LineNumbers

The line number obfuscation key takes two arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if line numbers should be obfuscated. |
| Remove | Boolean | Determines if should be removed altogether. |

## SourceName

The source name obfuscation key takes two arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if source names should be obfuscated. |
| Remove | Boolean | Determines if source names should be removed altogether. |

## SourceDebug

The source debug obfuscation key takes two arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if source debug information should be obfuscated. |
| Remove | Boolean | Determines if source debug information should be removed altogether. |

## HideCode

The hide code key takes only one argument: a **boolean** determining if hide code obfuscation should be applied.

## Shuffler

The shuffler key takes only one argument: a **boolean** determining if member shuffling should be applied.

## Crasher

The hide code key takes only one argument: a **boolean** determining if crashers should be applied.

## Renamer

The renamer key takes several arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if identifier renaming should be enabled. |
| Repackage | String | Moves all classes into this package. |
| AdaptResources | List of Strings | Regular expressions which tell the obfuscator which resources to attempt to adapt the updated class mappings to. |

## Optimizer

The optimizer key takes several arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if optimization should be enabled. |
| InlineGotoGoto | Boolean | Determines if goto-goto sequences should be optimized. |
| InlineGotoReturn | Boolean | Determines if goto-return sequences should be optimized. |
| RemoveNopInstructions | Boolean | Determines if nop instructions should be removed. |

## Shrinker

The shrinker key takes several arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if shrinking should be enabled. |
| RemoveAttributes | Boolean | Determines if miscellaneous class attributes should be removed. |
| RemoveDebug | Boolean | Determines if miscellaneous debugging info (inner classes, outer class, outer method, etc.) should be removed. |
| RemoveInvisibleAnnotations | Boolean | Determines if invisible annotations should be removed. |
| RemoveVisibleAnnotations | Boolean | Determines if visible annotations should be removed. |

## Watermarker

The watermarker key takes several arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if watermarking should be enabled. |
| Message | String | Message to embed. |
| Key | String | Key to encrypt message with. |

## Expiration

The expiration key takes several arguments.

| Key | Expected value(s) | Description |
| --- | --- | --- |
| Enabled | Boolean | Determines if expiration should be enabled. |
| InjectJOptionPane | Boolean | Determines if a JOptionPane should be injected into the code along with the Throwable. |
| Message | String | Message to show when the expiration date passes. |
| Expires | String | MM/dd/yyyy-formatted string of when the expiration date occurs. |

## Dictionary

The dictionary key takes only one argument: a **string** determining which dictionary to use when generating new names for classes and its members. Valid dictionaries are: **Spaces**, **Unrecognized**, **Alphabetical** and **Alphanumeric**.

## TrashClasses

The trash classes key takes only one argument: a **integer** determining how many trash classes should be generated. Any integer less than or equal to zero will disable trash classes.

## Libraries

The libraries key only takes one argument: a **list of strings** containing the paths of libraries the input jar is dependant on.

## Exclusions

The exclusions key takes only one argument: a **list of strings** containing exempts detailing how Radon should treat each class.

# Exclusions

Exclusions takes the following format: `<ExclusionType>: <ExclusionHere>` to dictate how Radon should treat exclusions. Exclusion statements are treated as **regular expressions**.

To exclude methods, you must use the format: `<ExclusionType>: <ContainingClass>\.<MethodName><MethodDescription>`. Fields are excluded in the format `<ExclusionType>: <ContainingClass>\.<FieldName>\.<FieldDescription>`.

All classes, methods, and fields are checked against exclusion __***at their internal bytecode representations***__. This means that

```java
package me.itzsomebody;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test");
    }
}
```

will be seen as `me/itzsomebody/Test.main([Ljava/lang/String;)V` to Radon. Valid exclusion types you can use are:

| Exclusion Type | Description |
| --- | --- |
| Global | Exempts a class/method/field from any kind of tampering (except for watermarking). |
| StringEncryption | Exempts a class/method from having its string literals encrypted. |
| InvokeDynamic | Exempts a class/method from having its method calls hidden with invokedynamic instructions. |
| FlowObfuscation | Exempts a class/method from having its methods flow-obfuscated. |
| LineNumbers | Exempts a class/method from having its line numbers tampered with. |
| LocalVariables | Exempts a class/method from having its local variables tampered with. |
| NumberObfuscation | Exempts a class/method from having its integer and long constants obfuscated. |
| HideCode | Exempts a class/method/field from having access codes added into its flags. |
| Crasher | Exempts a class from having an invalid type signature being added. |
| Expiration | Exempts a class/method from having an expiration code block being inserted. |
| Optimizer | Exempts a class/method from attempting to be optimized. |
| Shrinker | Exempts a class/methods from attempting to strip information/code Radon deems unnecessary. |
| Shuffler | Exempts a class from having its members (methods and fields) order randomized. |
| SourceName | Exempts a class from having its source name debugging information being tampered with. |
| SourceDebug | Exempts a class from having its source debug information being tampered with. |
| StringPool | Exempts a class/method from having its string literals pooled. |
| Renamer | Exempts a class/method/field from being renamed. |

## FAQ
* **Q: Is this uncrackable/undeobfuscatable?**
* *A: No. Nothing is impossible to deobfuscate or reverse-engineer. Furthermore, Radon is far from being hard to deobfuscate. On a scale of 1 to 10 on how hard Radon is to deobfuscate, I'd say 3 at best.*
* **Q: Why is this open-sourced?**
* *A: I made Radon as a way to experiment with obfuscation and to become familar with the JVM bytecode instruction set and as a codebase if anyone wants to mess around.*
* **Q: Doesn't that make it easier to deobfuscate?**
* *A: Probably.*
* **Q: Why are concepts taken directly from other obfuscators/bytecode manipulation tools? (i.e. expiration transformer which is directly based on Allatori's expiration obfuscation)**
* *A: I thought those would be interesting to include in an obfuscation tool. This is also one of the reasons Radon is open-sourced.*
* **Q: My Java 9+ installation doesn't have a rt.jar! What do I do?**
* *A: Try [this](https://github.com/Storyyeller/jrt-extractor).

## Attribution

* [OW2 ASM](http://asm.ow2.org) - Bytecode manipulation framework.
* [SnakeYaml](http://www.snakeyaml.org) - YAML parser.
* [VincBreaker](https://github.com/Vinc0682) - Author of Smoke obfuscator which I took some ideas from. (i.e. Renaming classes as spaces and splitting numbers into bitwise xor operations)
* [WindowBuilder by Eclipse](https://www.eclipse.org/windowbuilder/) - Used to make GUI (yes I know it's Java Swing, I didn't feel like remaking it in JavaFX)
* [Licel](https://licelus.com) - Makers of IndyProtect which I used as a reference for my invokedynamic transformers.
* [Allatori Dev Team](http://www.allatori.com) - Makers of Allatori Java Obfuscator which I took the concept of watermarking and expiration obfuscation from.

## License

GNU General Public License v3.0 (The cancer license)
