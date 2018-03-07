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