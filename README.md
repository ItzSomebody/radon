# Radon Java Obfuscator
Lightweight Java obfuscator by ItzSomebody. He tried to include as much attribution as possible, so if he missed you just open a pull-request and he will approve it because unlike some, he actually credits my sources (jk lol but I'll still credit sources).

Licenses get to go with the manifest in src/META-INF (yay!)

Usage: ```java -jar Radon.jar --config exampleconfig.yml```

Alternatively, you can also use ```java -jar Radon.jar --help``` for help.

Example config:
```
Input: ClearChat.jar
Output: ClearChat-OBF.jar
StringEncryption: Light
InvokeDynamic: Normal
FlowObfuscation: Normal
LocalVariableObfuscation: Remove
LineNumberObfuscation: Remove
SourceNameObfuscation: Obfuscate
HideCode: True
Crasher: True
StringPool: True
NumberObfuscation: True
TrashClasses: 50
Renamer: True
WatermarkType: ConstantPool
WatermarkMessage: ItzSomebody
WatermarkKey: PASSWORD
ExpiryTime: 1/18/2018
ExpiryMessage: "YOUR SOFTWARE TRIAL HAS ENDED!!! YOU MUST NOW PAY $100000000 FOR THE FULL VERSION LULZ"
Libraries:
    - "C:/Program Files/Java/jdk1.8.0_131/jre/lib/rt.jar"
Exempt:
    - "me/itzsomebody/clearchat/ClearChat"
    - "me/itzsomebody/clearchat/config/Config"
```

Valid config options you can use:

| Option | Expected Value(s) | Desc |
| --- | --- | --- |
| Input | String | Input file to obfuscate |
| Output | String | Output file to dump result of obfuscation |
| Libraries | String List | Libraries used to compile the input |
| Exempts | String List | Exempted classes, methods, or fields from obfuscation |
| StringEncryption | String (Light/Normal/Heavy) | Type of string encryption to apply |
| FlowObfuscation | String (Light/Normal) | Type of flow obfuscation to apply |
| InvokeDynamic | String (Light/Normal/Heavy) | Type of invokedynamic obfuscation to apply |
| LocalVariableObfuscation | String (Obfuscate/Remove) | Type of local variable obfuscation to apply |
| Crasher | Boolean | Determines if the decompiler crasher should be applied |
| HideCode | Boolean | Determines if synthetic modifiers should be applied |
| StringPool | Boolean | Determines if strings should be pooled |
| LineNumberObfuscation | String (Obfuscate/Remove) | Type of line number obfuscation to apply |
| NumberObfuscation | Boolean | Determines if integers should be split into simple math expressions |
| SourceNameObfuscation | String (Obfuscate/Remove) | Type of source name obfuscation to apply |
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


## Credits

* [OW2 ASM](http://asm.ow2.org) - ObjectWeb ASM.
* [SnakeYaml](http://www.snakeyaml.org) - SnameYAML.
* [LordPancake](https://www.youtube.com/user/LordPankake) - Maker of SkidSuite
* [samczsun](https://github.com/samczsun) - Author of Java-Deobfuscator and the 20 (or so) ways to crash the various decompilers which is no longer open-source.
* [VincBreaker](https://github.com/Vinc0682) - Author of Smoke obfuscator which I stole some stuff from xD.
* [Coolman](https://github.com/c001man) - I stole his Zip-writing algorithm xD.
* [WindowBuilder by Eclipse](https://www.eclipse.org/windowbuilder/) - Used to make GUI (yes I know it's Java Swing, I didn't feel like remaking it in JavaFX)
* [Licel](https://licelus.com) - Makers of Stringer Java Obfuscator.
* [Allatori Dev Team](http://www.allatori.com) - Makers of Allatori Java Obfuscator.
* [Caleb Whiting](https://github.com/CalebWhiting/java-asm-obfuscator) - Author of Java ASM Obfuscator.

## License

GNU General Public License v3.0