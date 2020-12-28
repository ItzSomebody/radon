# Radon Java Obfuscator [![Build Status](https://travis-ci.org/ItzSomebody/Radon.svg?branch=master)](https://travis-ci.org/ItzSomebody/Radon) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/07b0849ead3f47f1a6950a0353f43541)](https://www.codacy.com/app/ItzSomebody/Radon?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ItzSomebody/Radon&amp;utm_campaign=Badge_Grade)

## Radon 2.0 will no longer be maintained - it will be replaced by Radon 3.0.0 (see note at bottom)

Radon is a Java bytecode obfuscator that I conceptualized in my freshman year
of high school. I use Radon to experiment with obfuscation and to learn various
parts of Java bytecode.

Check the [wiki for usage](https://github.com/ItzSomebody/Radon/wiki) or maybe
even the [Discord](https://discord.gg/RfuxTea).

This project is **not** likely to be production-safe nor bug free. If you would
like some pointers to production-safe (more or less) and less buggy Java
bytecode obfuscators, I would suggest one of these:
* [Paramorphism](https://paramorphism.serenity.enterprises/) -
[Discord](https://discordapp.com/invite/k9DPvEy)
* [Proguard](https://www.guardsquare.com/en/products/proguard)
* [Zelix KlassMaster](http://www.zelix.com/)

Additionally, here are some other obfuscators for Java that you could check out
for fun:
* [Allatori](http://www.allatori.com/)
* [DashO](https://www.preemptive.com/products/dasho/overview)
* [JBCO](http://www.sable.mcgill.ca/JBCO/)
* [JObf](https://github.com/superblaubeere27/obfuscator)
* [NeonObf](https://github.com/MoofMonkey/NeonObf)
* [Obzcure](https://obzcu.re/) -
[Discord](https://discordapp.com/invite/fUCPxq8) (Dev is away for a few months)
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

I've noticed that a number of people have been asking when radon 3 will be released. Realistically, the answer is probably not for awhile. The main reasons for this are because I've been quite busy and I haven't been motivated to work with Java bytecode as of recently. Truthfully, there is little work done on radon 3 at the moment and this is also partially due to numerous redesigns I have done on the codebase. Regardless, you can see the progress so far via the "3.0.0" branch. 

The current rewrite of radon is actually a multi-module project which is split into the following:
* xyz.itzsomebody.codegen
* xyz.itzsomebody.commons
* xyz.itzsomebody.radon

The codegen module is actually the top of my priority list right now (at least, as far as radon goes). It is intended to be a high-level API to generate instructions so the transformers don't explicitly rely on asm-tree and ASMifier as much. Until this is completed, I think it's pretty safe to say that I won't be working on any transformers.

The commons module will contain an instruction pattern matcher and a few flow analyzers, so there probably won't be any flow transformers until this one is done (I haven't even started on it, FYI).

The radon module is the obfuscator itself.

For those of you who want a GUI... well..., I don't think a GUI is coming any time soon. But I plan to make one! :)

P.S. If I actually somehow manage to get to the GUI, it will be in JavaFX because it's a bit less cancerous than Java Swing. :P