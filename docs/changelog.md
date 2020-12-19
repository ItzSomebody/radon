# Changelog

## 3.0.0 - Full rewrite
There are multiple turning points with the release of Radon 3.0.0 compared to all previous versions of Radon.

### Differences between Radon 3 and previous versions
* Radon 3 is written in Java 11. Every previous version of Radon was written in Java 8.
* Some of Radon 3's features are not portable to different JVMs whereas every previous version of Radon was intended for
HotSpot.
* Radon 3 removes all "optimizers" from Radon 2. If you need some kind of optimization done, use ProGuard instead.
* Compared to Radon 2, Radon 3 can be significantly more risky in how it performs transformations on bytecode.
* Radon 2 uses the GitHub wiki for documentation of transformers and usage. Radon 3 has no usage guide and uses GitHub
pages for documentation of transformers (however, the wiki will not be removed for ease of reference).
* Radon 3 removes the nested YAML structure in Radon 2 and instead puts all transformers on the root level.
* Radon 3 removes delegation for "shrinking" transformers via the above point.
* Radon 3 adheres to a style guide (sort of) whereas no previous version of Radon has.
* Radon 3 reimplements the exclusion system in a much more efficient matter.
* Radon 3 introduces a high-level code generator to remove some reliance on ASMifier generated code.
