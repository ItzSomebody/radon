# Radon Java Obfuscator [![Build Status](https://travis-ci.org/ItzSomebody/Radon.svg?branch=master)](https://travis-ci.org/ItzSomebody/Radon) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/07b0849ead3f47f1a6950a0353f43541)](https://www.codacy.com/app/ItzSomebody/Radon?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ItzSomebody/Radon&amp;utm_campaign=Badge_Grade)

## Radon is no longer maintained

Radon is a Java bytecode obfuscator that I conceptualized in my freshman year
of high school. I use Radon to experiment with obfuscation and to learn various
parts of Java bytecode.

Check the [wiki for usage](https://github.com/ItzSomebody/Radon/wiki) or maybe
even the [Discord](https://discord.gg/RfuxTea).

This project is **not** likely to be production-safe nor bug free. If you would
like some pointers to production-safe (more or less) and less buggy Java
bytecode obfuscators, I would suggest one of these:
* [Proguard](https://www.guardsquare.com/en/products/proguard)
* [Skidfuscator](https://github.com/terminalsin/skidfuscator-java-obfuscator) - [Discord](https://discord.gg/QJC9g8fBU9)
* [Zelix KlassMaster](http://www.zelix.com/)
* [zProtect](https://zprotect.dev/) - [Discord](https://discord.com/invite/dnGKGuwvGH)

Additionally, here are some other obfuscators for Java that you could check out
for fun:
* [Allatori](http://www.allatori.com/)
* [DashO](https://www.preemptive.com/products/dasho/overview)
* [JBCO](http://www.sable.mcgill.ca/JBCO/)
* [JObf](https://github.com/superblaubeere27/obfuscator)
* [NeonObf](https://github.com/MoofMonkey/NeonObf)
* [Obzcure](https://obzcu.re/) -
[Discord](https://discordapp.com/invite/fUCPxq8) (Dead)
* [Paramorphism](https://paramorphism.serenity.enterprises/) -
[Discord](https://discordapp.com/invite/k9DPvEy) (Dead)
* [Sandmark](http://sandmark.cs.arizona.edu)
* [Stringer](https://jfxstore.com/stringer/)
* [yGuard](https://www.yworks.com/products/yguard)

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

## License

GNU General Public License v3.0 (The cancer license)

# Regarding Radon 3.0.0

The progress on Radon 3 is permanently dead for the foreseeable future. Check the `radon-3` branch to see where work was stopped.
