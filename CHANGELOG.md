## 0.8.2

* Added warning about mixing Spigot's anti-piracy and trash classes together.

## 0.8.1

* Changed the CustomClassWriter a bit.
* Heavy Flow transformer now implemented into GUI.
* Changed up the way the heavy flow transformer works due to the old one not working whatsoever.

## 0.8.0

* Changed even more internals.
* Resources can now be processed by obfuscator.

## 0.7.0

* Completely rewrote StackAnalyzer.
* Changed some internals.

## 0.6.1

* Fixed an issue with the invokedynamic transformers which sometimes caused programs to crash with an access exception.

## 0.6.0

* Added GUI console for GUI users.

## 0.5.4

* GUI updates:
    * You can add multiple libraries at a time rather than one.
    * You can remove multiple libraries at a time rather than one.
    * You can remove multiple exempts at a time rather than one.
    * Added GC button which invokes System.gc().

## 0.5.3

* Added the ability to change between Radon's built-in dictionaries.

## 0.5.2

* You can now resize the GUI.
* Expiry exempts are added.
* StackAnalyzer takes try-catch blocks into account now resulting in possibly better obfuscation by normal flow obfuscation transformer.
* Number obfuscation transformer now operates on longs as well as integers.

## 0.5.1

* Fixes as issue with exempting where global-exempts didn't work.

## 0.5.0

* Exempt update. Exempt-per-transformer is now implemented.

## 0.4.4

* Exempting should work with renamer transformer again.

## 0.4.3

* Changed the heavy invokedynamic transformer so it now replaces field calls with primitive types.

## 0.4.2

* Added UTF-8 source-encoding to Maven.
* Made some changes to the normal flow obfuscation transformer that might make it (very slightly) hard to understand.

## 0.4.1

* Maven-ized the project.
* Removed more ripped-off code.

## 0.4.0

* Completely changed the normal flow obfuscation transformer so that it doesn't have anymore skidded code (lol).
* Added the StackAnaylzer which doesn't work very well at the moment.

## 0.3.9

* Some internal changes, nothing new.

## 0.3.8

* Added super light string encryption setting to GUI.

## 0.3.7

* Renamer transformer kind of works now. There are still cases where it causes abstraction errors and exempting doesn't work at the moment.

## 0.3.6

* Changed number obfuscation transformer so that it splits integers into a xor expression. This is far faster than the old transformer.
* Renamer transformer is still broken, so don't use it expecting it to work each time.

## 0.3.5

* Added super light string encryption transformer which is intended for performance. Note that this transformer is very insecure and is not recommended in code which requires security.

## 0.3.4

* Heavy string encryption transformer is now the normal string encryption transformer. The normal string encryption transformer is now the heavy string encryption transformer with some flow and an extra layer of encryption. The light string encryption got changed completely.

## 0.3.3

* Changed both the Light invokedynamic and normal invokedynamic transformers so they are less ripped-off from IndyProtector.
* Some other edits here and there that I completely forgot.

## 0.3.2

* Lots of GUI fixes.

## 0.3.1

* Quick GUI fix causing Field exempts not to work.

## 0.3.0

* Completely rewrote Renamer transformer.
* Radon now depends on some edits to org/objectweb/asm/ClassNode.

## 0.2.5

* Added Shuffler and InnerClassRemover transformers.
* Updated GUI.

## 0.2.4

* Added more JavaDocs + removed debugging code from heavy invokedynamic.

## 0.2.3

* Added heavy string encryption to gui.

## 0.2.2

* Completely changed light flow obfuscation transformer.

## 0.2.1

* Stuck some more hand-made flow obfuscation into the heavy string encryption transformer.

## 0.2.0

* Added the "heavy" string encryption transformer.

## 0.1.9

* GUI now attempts to auto-fetch rt.jar and jce.jar (stole this idea from ZKM).

## 0.1.8

* Added rest of transformers to GUI.

## 0.1.7

* Un-refactored SnakeYAML and ASM.
* Changed the way transformers are enabled.
* Added more JavaDocs (whoooo).

## 0.1.6

* Added a super buggy heavy invokedynamic transformer that hides not only methods, but fields as well.

## 0.1.5

* Added method size checking to prevent adding too many bytecode instructions to the point where the JVM refuses to run methods for being too big.

## 0.1.4

* Added field signatures and method signatures to crasher.
* Toned down normal flow obfuscation transformer so that it doesn't make method ridiculously huge over useless bytecode transformations.

## 0.1.3

* Changed up Number Obfuscation transformer a bit.

## 0.1.2

* Added custom regex matching system for exempts.

## 0.1.1

* Fixed renaming bug with Bukkit/Bungee plugins.

## 0.1.0

* Completely redesigned transformer system.

## 0.0.14

* Squashed an annoying buggy in the new NumberObfuscation transformer thanks to https://github.com/JoachimVandersmissen/TestingProject

## 0.0.13

* Completely re-worked NumberObfuscation transformer and cleaned some stuff up in general.

## 0.0.12

* Cleaned up StringPool and NumberObfuscation transformers.

## 0.0.11

* Added Expiry transformer.

## 0.0.10

* Switched to ASM6 (FIELD RENAMING STILL NOT WORKING WHYYYYY)

## 0.0.9

* Fixed some JavaDocs.
* Embedded logging into the transformers.
* Relocated bigLDC generator into StringUtils.
* Lots of transformer changes in general.

## 0.0.8

* Sped up library loading.

## 0.0.7

* Fixed a really dumb mistake I made.
* Stole VincBreaker's crasher algorithms (which are probably samczsun's) so lol.
* Stole VincBreaker's idea to use spaces as class names.
* So hopefully I don't invoke the rage of VincBreaker? xD

## 0.0.6

* Even more bug fixes and more blah-blah.
* Beefed up normal flow obfuscation transformer a bit.

## 0.0.5

* Bug fixes plus lots of JavaDocs to make code look a lot bigger than it actually is LOL.
* Removed Light flow obfuscation transformer.
* Normal flow obfuscation transformer is now the Light flow obfuscation transformer.
* Ugly bytecode transformer is now the Normal flow obfuscation transformer.

## 0.0.4

* Make GUI functional.

## 0.0.3

* Added non-functional GUI.

## 0.0.2

* Added transformers plus CLI.

## 0.0.1

* Added base skeleton and blah-blah.