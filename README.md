# [Abandoned] Radon Java Bytecode Obfuscator

## Radon is no longer maintained

It's important to note that Radon is intended for __***experimentation only***__. If your software breaks in production
because you protected it with Radon then that is completely on you.

If you have a quick question about how something works, you can join my [Discord](https://discord.gg/RfuxTea)
server and ask.

This project is **not** likely to be production-safe nor bug free. If you would
like some pointers to production-safe (more or less) and less buggy Java
bytecode obfuscators that are properly maintained, I would suggest one of these:
* [Proguard](https://www.guardsquare.com/en/products/proguard) (mostly only
class/method/field name obfuscation)
* [Skidfuscator](https://github.com/terminalsin/skidfuscator-java-obfuscator) - [Discord](https://discord.gg/QJC9g8fBU9)
* [Zelix KlassMaster](http://www.zelix.com/)

Since we're on the topic of obfuscation, consider taking a look at
[Recaf](https://github.com/Col-E/Recaf) ([Discord](https://discord.gg/Bya5HaA))
if you have not already. Recaf is actively maintained by the contributers to
stay on top of a variety obfuscation techniques found in the wild. I would
highly recommend learning to use Recaf for those who are into Java reverse-engineering.

[Skidfuscator](https://github.com/terminalsin/skidfuscator-java-obfuscator) is probably
one of the most promising Java bytecode obfuscation projects I have seen and has a much
more sanely-managed codebase than most obfuscators do (including Radon). I would recommend
anyone trying to get into Java obfuscation to spend time learning how Skidfuscator works.

Another resource (slightly dated, but still a good amount of information) is
GenericException's [SkidSuite](https://github.com/GenericException/SkidSuite) repo.
Some common cheap obfuscation techniques that have become more common in the last 
few years are documented there which anyone who is interested in Java reverse-engineering
should know.

Additionally, here are some other obfuscators/protectors for Java that you could
check out for fun or learning (though not necessarily I would recommend using are):
* [Allatori](http://www.allatori.com/) - Commericial. Somewhat popular choice in industry.
* [avaj](https://github.com/cg-dot/avaj) - FOSS. Has a nice way of generating decryption subroutines on string constants. Also has some CFG flattening which is always nice to see.
* [BisGuard](http://www.bisguard.com/) - Commercial. Relies entirely on class encryption (last time I checked, at least) so protection has quite a bit of room for improvement.
* [Bozar](https://github.com/vimasig/Bozar) - Points for FOSS. Has some cheap tricks that I have seen being used in the Minecraft community.
* [Branchlock](https://branchlock.net/) - Commercial. Shows up a bit in the Minecraft community.
* [Caesium](https://github.com/sim0n/Caesium) - FOSS. Has a transformer that implements a well-known HTML injection into any Java reverse-engineering tool that parses HTML tags. 
* [ClassGuard](https://zenofx.com/classguard/) - Commercial. Relies mostly on class encryption with hardcoded AES keys in native libs. Pretty easy IDA/Binary Ninja/Ghidra exercise if you want to flex on your blog on something. 
* [DashO](https://www.preemptive.com/products/dasho/overview) - Commercial. Shows up a bit in industry and has some interesting ideas (albeit probably outdated) in flow obfuscation.
* [JBCO](http://www.sable.mcgill.ca/JBCO/) - FOSS. Some interesting flow obfuscation techniques that still work in modern Java. Based on the [Soot](https://github.com/soot-oss/soot) library which is also something worthwhile checking out.
* [JObf](https://github.com/superblaubeere27/obfuscator) - FOSS. Pretty outdated. Some of the transformations done show up in the Minecraft community so it can be worthwhile spending a bit of time takinng a look at this.
* [JObfuscator](https://www.pelock.com/products/jobfuscator) - Commericial. Never seen this used before so I cannot really give any comments.
* [NeonObf](https://github.com/MoofMonkey/NeonObf) - Mostly points for FOSS.  Made up of the easier to defeat obfuscation techniques.  NeonObf is also the name inspiration for Radon.
* [Obzcure](https://obzcu.re/) [Discord](https://discordapp.com/invite/fUCPxq8) (Dead) - Commericial. Web-based obfuscation service with some inspiration taken from Radon and [SkidSuite2](https://github.com/GenericException/SkidSuite/tree/master/archive/skidsuite-2). Used to go by the name "SpigotProtect" so you might see some Spigot plugins using the obfuscation from this product if you look around hard enough.
* [Paramorphism](https://paramorphism.serenity.enterprises/) - [Discord](https://discordapp.com/invite/k9DPvEy) (Dead) - Commerical. Was one of the most unusual and unique obfuscators at the time it was an active project in that relied a lot more on the JVM's unusual way of loading JAR archives including zip entries with duplicated names and the [fake directory trick](https://github.com/x4e/fakedirectory). Used to be more commonly used before people started ripping ideas from Paramorphism.
* [qProtect](https://mdma.dev/) - Commericial. Implements a lot of the more common obfuscation techiques into a single tool. Shows up a bit in the Minecraft community.
* [Sandmark](http://sandmark.cs.arizona.edu) - FOSS. Really old obfuscator research project led by Christian Collberg at the University of Arizona. Some interesting ideas in watermarking are here and some of the flow obfuscation ideas are good.
* [SkidSuite2](https://github.com/GenericException/SkidSuite/tree/master/archive/skidsuite-2) - FOSS. Some pretty basic obfuscation techniques, nothing too special.
* [Stringer](https://jfxstore.com/stringer/) - Commercial. Pretty infamous for its complicated AES-based encryption/decryption routines and price. Does not really offer a whole lot of protection, but sometimes does show up in industry.  
* [yGuard](https://www.yworks.com/products/yguard) - FOSS. Functionally equivalent to ProGuard as far as I can tell.
* [zProtect](https://zprotect.dev/) - [Discord](https://discord.com/invite/dnGKGuwvGH) - Commercial. Newer obfuscator. I have not seen any samples from it so I do not have an opinion on it.

## Build Instructions

Run the following (and hope nothing breaks):
```
./gradlew build
```
Or if you're on Windows:
```
gradlew.bat build
```

Should that somehow not work, use the following instead:
```
./gradlew clean shadowJar
```

P.S. For those wondering why there aren't any prebuilt releases, if you can't figure out how to use Gradle, should you really be using an obfuscator? ;) [end of snarkiness]

## FAQ

* **Q: Is this uncrackable/undeobfuscatable?**  
*A: No. Nothing is impossible to deobfuscate or reverse-engineer. Furthermore, Radon is far from being hard to 
  deobfuscate. On a scale of 1 to 10 on how hard Radon is to deobfuscate, I'd say 2 in the best possible scenario.*
  
* **Q: Why is this open-sourced?**  
*A: I made Radon as a way to experiment with obfuscation and to become familiar with the JVM bytecode instruction set 
  and as a codebase if anyone wants to mess around. Furthermore, I strongly support the FOSS ideology.*
  
* **Q: Doesn't Radon being open-sourced make it easier to deobfuscate?**  
*A: Probably.*
  
* **Q: Can Spring apps be obfuscated with this?**  
*A: Out of the box, no. Starting from Radon 3, I will never add support for Spring or multiversion JARs.*
  
* **Q: What does '... "org/somelib/TableFactoryBuilder" not found in classpath' mean?**  
*A: Radon internally determines how to construct certain entities in the classfile based on the class hierarchy of the 
  JAR being obfuscated. For this reason, Radon needs access to library classes used by the project. Make sure you add 
  the appropriate libraries so this doesn't show up.*
  
* **Q: Can I use all transformers for maximum protection?**  
*A: You could, but you should note that it's very likely that the program will break and/or there will be a large 
  overhead in file size and/or performance.*
  
* **Q: Why do certain combinations of transformers break the program?**  
*A: Radon is intended to be an experimental project, not a commercial product protector. Not all features are meant to 
  work together.*
  
* **Q: What version of Java is required to run Radon 3?**  
*A: Java 11+.*
  
* **Q: What version of Java is my software required to written in to work with Radon?**  
*A: Radon can theoretically obfuscate software written in any Java version provided that the `asm` library is compatible
  with the classfile version.*
  
* **Q: Does Radon support Android?**  
*A: While the answer is technically yes, I will not provide support for issues pertaining to Android apps.*
  
* **Q: Does Radon have Gradle, Maven, or Ant integration?**  
*A: No. You are more than welcome to make a PR to add such functionality if desired.*
  
* **Q: Will there be a GUI for Radon 3?**  
*A: Yes, however, a GUI is very low on my priority list, so it might be awhile before one is actually made.*

* **Q: Will there be a Radon 4?**  
*A: Probably not.*

## License

GNU General Public License v3.0 (The cancer license)
