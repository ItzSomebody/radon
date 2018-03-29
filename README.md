# Radon Java Obfuscator ![Build Status](https://travis-ci.org/ItzSomebody/Radon.svg?branch=master)
Yet another Java obfuscator. If I forgot to credit you, just make a pull-request or open an issue.

Licenses get to go with the manifest in src/META-INF (yay!)

Usage: ```java -jar Radon.jar --config exampleconfig.yml```

Alternatively, you can also use ```java -jar Radon.jar --help``` for help.

Example config:
```yaml
Input: "C:/Users/Buddy/Desktop/RadonOBF/Counter.jar"
Output: "C:/Users/Buddy/Desktop/Counter-OBF.jar"
StringEncryption: Normal
InvokeDynamic: Heavy
FlowObfuscation: Normal
LocalVariableObfuscation: Remove
LineNumberObfuscation: Remove
SourceNameObfuscation: Remove
SourceDebugObfuscation: Remove
HideCode: True
Crasher: True
Shuffler: True
InnerClassRemover: True
StringPool: True
NumberObfuscation: True
TrashClasses: 50
Renamer: True
WatermarkType: ConstantPool
WatermarkMessage: ItzSomebody
WatermarkKey: PASSWORD
ExpiryTime: 5/25/2018
ExpiryMessage: "YOUR SOFTWARE TRIAL HAS ENDED!!! YOU MUST NOW PAY $100000000 FOR THE FULL VERSION LULZ"
Libraries:
    - "C:/Program Files/Java/jre1.8.0_xxx/lib/rt.jar"
Exempts:
    - "Class: org/objectweb/asm/*"
    - "StringPool: me/itzsomebody/counter/Counter$1"
    - "InvokeDynamic: me/itzsomebody/counter/Counter"
    - "Renamer: me/itzsomebody/counter/Counter$1"
    - "Renamer: me/itzsomebody/counter/Counter.process(*"
```

Valid config options you can use:

| Option | Expected Value(s) | Desc |
| --- | --- | --- |
| Input | String | Input file to obfuscate |
| Output | String | Output file to dump result of obfuscation |
| Libraries | String List | Libraries used to compile the input |
| Exempts | String List | Exempted classes, methods, or fields from obfuscation |
| StringEncryption | String (SuperLight/Light/Normal/Heavy) | Type of string encryption to apply |
| FlowObfuscation | String (Light/Normal) | Type of flow obfuscation to apply |
| InvokeDynamic | String (Light/Normal/Heavy) | Type of invokedynamic obfuscation to apply |
| LocalVariableObfuscation | String (Obfuscate/Remove) | Type of local variable obfuscation to apply |
| Crasher | Boolean | Determines if the decompiler crasher should be applied |
| HideCode | Boolean | Determines if synthetic modifiers should be applied |
| StringPool | Boolean | Determines if strings should be pooled |
| LineNumberObfuscation | String (Obfuscate/Remove) | Type of line number obfuscation to apply |
| NumberObfuscation | Boolean | Determines if integers should be split into simple math expressions |
| SourceNameObfuscation | String (Obfuscate/Remove) | Type of source name obfuscation to apply |
| SourceDebugObfuscation | String (Obfuscate/Remove) | Type of source debug obfuscation to apply |
| TrashClasses | Integer | Number of trash classes to generate |
| WatermarkMessage | String | Message to watermark into the output |
| WatermarkType | String (ConstantPool/Signature) | Type of watermark to apply |
| WatermarkKey | String | Key used to encrypt watermarks |
| SpigotPlugin | String | Determines if input should be treated as a spigot/bungee plugin |
| Renamer | Boolean | Determines if obfuscator should rename classes and methods |
| Shuffler | Boolean | Determines if obfuscator should re-arrange class members |
| InnerClassRemover | Boolean | Determines if obfuscator should remove inner-class information |
| ExpiryTime | String | Message to insert for expiry obfuscation (useful for trialware) |
| ExpiryMessage | String | Message to show when set your trialware goes past expiration date (rip) |

## Exempting

The character '\*' can be used as a wildcard for "anything which starts with before this". So "me/itzsomebody/\*" will match "me/itzsomebody/ExampleClass1", "me/itzsomebody/example/ExampleClass2". This also works on method matching, "me/itzsomebody/example/Example.exampleMethod(*" will match any method which has the name "exampleMethod" in "me/itzsomebody/example/Example".

Valid exempt types you can use:

| Option | Expected Value(s) | Desc |
| --- | --- | --- |
| Class | Fully qualified name of internal class name (i.e. me/itzsomebody/counter/Counter) | Exempts entire classes from obfuscation. |
| Method | Fully qualified name of internal owner name + '.' + internal method name + internal method descriptor (i.e. me/itzsomebody/counter/Counter.exampleMethod()V) | Exempts entire method from obfuscation. |
| Field | Fully qualified name of internal owner name + '.' + internal field name | Exempts field from obfuscation. |
| StringEncryption | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method from string encryption. |
| InvokeDynamic | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method from invokedynamic obfuscation. |
| Flow | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method from flow control obfuscation. |
| LocalVars | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method from local variable obfuscation. |
| SourceName | Fully qualified internal name (i.e. me/itzsomebody/counter/Counter) | Exempts class source from name obfuscation. |
| SourceDebug | Fully qualified internal name (i.e. me/itzsomebody/counter/Counter) | Exempts class source from debug obfuscation. |
| LineNumbers | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method from line number obfuscation. |
| StringPool | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method from string pooling. |
| Crasher | Fully qualified internal name (i.e. me/itzsomebody/counter/Counter) | Exempts class from being added an invalid class signature. |
| HideCode | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter.exampleField or ) | Exempts class, method or field from hide code obfuscation. |
| Numbers | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter) | Exempts class or method number obfuscation. |
| Shuffler | Fully qualified internal name (i.e. me/itzsomebody/counter/Counter) | Exempts class from member shuffling. |
| InnerClasses | Fully qualified internal name (i.e. me/itzsomebody/counter/Counter) | Exempts class from inner-class information removal. |
| Renamer | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter.exampleField or ) | Exempts class, method or field from renaming. |
| Expiry | Fully qualified internal name (member name must be seperated from owner by a '.') (i.e. me/itzsomebody/counter/Counter.exampleMethod()V or me/itzsomebody/counter/Counter.exampleField or ) | Exempts class, method or field from expiration. |

## Obfuscation description
This table describes the current obfuscation settings Radon has.

| Obfuscation Type | Description |
| --- | --- |
| StringEncryption | This takes the strings in your program and encrypts them. Each of the current settings are symmetrical and will NOT prevent reverse-engineers from figuring out what your program does. The super light setting uses an extremely simple xor algorithm and is not secure. The light setting uses a simple algorithm which encrypts the strings for speed. There is minimal flow obfuscation in the light setting so be aware this offers minimal protection. The normal string encryption setting is basically a flow-obfuscated version of the light string encryption, it is intended to make the decryption method more confusing. The heavy method uses AES to encrypt the strings. |
| FlowObfuscation | This attempts to confuse the program flow to make the decompiled code harder to understand. The light setting replaces all gotos in the program with conditionals which always evaluate to true. The light setting usually doesn't affect decompiler results by much unless you have methods with some certain flow control situations. The normal flow obfuscation attempts to find places in the bytecode where the stack is empty. It uses an injected predicate which is used to make false jumps and add some extra throw-nulls in random places. |
| InvokeDynamic | This abuses Java 7's new opcode, the invokedynamic. This replaces certain member access with dynamic invocations. The light invokedynamic transformer is very simple and replaces invokestatic, invokevirtual and invokeinterface with invokedynamics. This is easily reversed by experienced reverse-engineers so don't rely on this. The normal invokedynamic protection is a very slight improvement to the light settings, but still is easy to reverse. The heavy invokedynamic hides invokestatic, invokevirtual, getstatic, putstatic, getfield and putfield opcodes with invokedynamics. |
| LocalVariableObfuscation | This attempts to prevent local variable analysis. The obfuscation setting changes the names of the local variables to hard-to-read UTF-8 characters. The remove setting removes the local variable information completely destroying the ability to recover the local variable names. Removal setting tends to shrink jar size since it removes information in the code. |
| Crasher | This adds an invalid signature to classes. The intention of this transformer is to crash a majority of decompiles which attempt to take class signatures into account of the output. This crashes JD-GUI, Procyon, CFR and Javap. |
| HideCode | This adds certain access flag modifiers to members of your code. Synthetic modifiers are added to classes, methods and fields and bridge modifiers are added to methods which aren't initializer methods (\<init> and \<clinit>). |
| StringPool | This adds a method into the class which takes all the strings in the class and pools them into the added method. |
| LineNumberObfuscation | This attempts to obscure stacktrace output by changing line number debug information. The obfuscation setting sets line numbers to random ones. The removal setting removes them from your code entirely. The removal setting also shrinks the size of the jar. |
| NumberObfuscation | This splits integers into a xor expression making it difficult to determine what number is being used. This is defeated by Krakatau. |
| SourceNameObfuscation | This attempts to obscure stacktrace output by removing sourcefile debug information. The obfuscation setting sets all the source file names to random ones. The removal setting removes them entirely from your code. The removal setting also shrinks the size of the jar. |
| SourceDebugObfuscation | This attempts to obscure source debug information. The obfuscation setting sets all the source debug values to random ones. The removal setting removes them entirely from your code. The removal setting also shrinks the size of the jar. |
| TrashClasses | This generates garbage classes which aren't used to attempt to hide the ones that are actually used. This also prevents JByteEdit usage. |
| Watermark | This marks a message into the classes which is intended for customer-identification. The constant pool option marks the message into the constant pool of each class. The signature option marks the messages into the class signature. |
| Renamer | This renames classes, methods and fields from their original names to random UTF-8 strings. |
| Shuffler | This simply changes the order of class members (methods and fields). |
| InnerClassRemover | This removes innerclass information. |
| ExpirationObfuscation | This adds a block of expiration code into each class initializer to prevent usage of jar after the expiration date has passed. |

## Credits

* [OW2 ASM](http://asm.ow2.org) - ObjectWeb ASM.
* [SnakeYaml](http://www.snakeyaml.org) - SnameYAML.
* [VincBreaker](https://github.com/Vinc0682) - Author of Smoke obfuscator which I took some ideas from. (i.e. Renaming classes as spaces and splitting numbers into bitwise xor operations)
* [WindowBuilder by Eclipse](https://www.eclipse.org/windowbuilder/) - Used to make GUI (yes I know it's Java Swing, I didn't feel like remaking it in JavaFX)
* [Licel](https://licelus.com) - Makers of IndyProtect.
* [Allatori Dev Team](http://www.allatori.com) - Makers of Allatori Java Obfuscator.
* [Artel](https://gitlab.com/artel) - Beta tester.

## License

GNU General Public License v3.0