# Radon Contribution Guide

## Table of Contents
* [GitHub](#github)
    * [Issue Reports](#issue-reports)
    * [Pull Requests](#pull-requests)
* [Styleguides](#styleguides)
    * [Git Commit Messages](#git-commit-messages)
    * [Java Styleguide](#java-styleguide)
    * [Transformer Documentation](#transformer-documentation)

## GitHub
### Issue Reports
Found an issue with Radon? Awesome. Make sure you read the following before reporting it:

#### Before Submitting an Issue Report
* **Check the FAQ in the readme**. Radon has had numerous "bugs/errors/issues" reported which were completely
preventable due to configuration.
* **Check if the issue has already been reported**. Should said issue already exist, add a comment on the issue rather
than starting a new one.
* **Check if you made a YAML error**. You can use an online YAML verifier like
[this one](http://www.yamllint.com/) to catch errors quickly.
* **If possible, please spend a little time debugging before reporting the issue!** This could save a ton of time for
you and anyone who decides to fix the bug. If you are able to locate the precise issue in Radon's codebase, chances are 
a fix can be implemented much sooner.

#### Submitting an Issue Report as a Bug
All bugs are tracked as GitHub issues for organizational purposes. For that reason, please do not report bugs in the
Discord and expect a fix as I am prone to forgetfulness.

When opening a bug report, please make sure to follow the below:
* **Use a clear and descriptive title**.
    * Example of a **good** title: "Using the FastInvokedynamic transformer on any Spigot plugin results in NPE from
      radon"
    * Example of a **good** title: "Adding an exclusion hangs the renamer"
    * Example of a **bad** title: "Adding an exclusions causes bug"
    * Example of a **bad** title: "Radon bug"
* **Be as specific and descriptive as possible when describing the issue**. It really annoys me and other people when
your description is vague. I should be able to understand exactly what the issue is with no questions asked.
* **Be as detailed as you can when describing the expected behavior.** Same reason as above.
* **Provide the git commit hash you build Radon from.** This allows me or anyone investigating the issue to instantly
find the version of Radon you are using. Saying "latest" or similar will result in an instant close of your issue with
no help provided. If you're not going to make an effort to make a proper report, then I'm not going to make an effort to
help.
* **Provide the exact configuration used in code blocks**. This means no blanking out lines and no screenshots. I
apologize in advance if privacy is desired, but issues that could have been solved in 5 seconds have lasted multiple
hours because people hid parts of their configuration while requesting help.
* **Provide the relevant errors and exceptions in code blocks.** Please do not put screenshots here and make sure to put
each error in its own markdown code block. Also, please make sure you specify when the error happens.
* **Be as specific and detailed as possible when doing reproduction steps**. This is where some of the worst
miscommunications have happened which resulted in a ton of wasted time. Please just put in a little effort so that I
know *exactly* what you did.
* **Attach / link what you attempted to obfuscate**. This is a mandatory for all software problems created by the
obfuscation. If you are unable to attach or link the software you obfuscated, attach a proof-of-concept JAR which also
causes the same issue to happen. If possible, please also provide the obfuscated output.

Extra: [this video](https://www.youtube.com/watch?v=53zkBvL4ZB4&vl=en) by
[LiveOverflow](https://github.com/LiveOverflow) and this [webpage](http://www.catb.org/esr/faqs/smart-questions.html)
are good references for properly asking a question.

#### Submitting an Issue Report as an Enhancement / Feature Request
All enhancements and feature requests are tracked as GitHub issues for organizational purposes.

When opening an enhancement / feature request, please make sure to follow the below:
* **Use a clear and descriptive title**. Please also prefix the title with [Enhancement/FR] so there's no room for
confusion.
    * Example of a **good** title: "[Enhancement] Use dynamic decryption methods for XXXX instead of YYYY"
    * Example of a **good** title: "[Feature Request] Transformer to flatten the CFG"
    * Example of a **bad** title: "suggestion"
    * Example of a **bad** title: "thing to make radon better"
* **Provide an in-depth description of what the feature should do.** You need to be as descriptive as possible to the
point where people shouldn't have to ask question to understand what the feature/enhancement will do.

#### Pull Requests
Pull requests should fulfill one of the following:
* Spelling / grammatical errors in documentation, Javadocs, or markdown files.
* Fix issues reported on the issue tracker.
* Implement an enhancement / feature request on the issue tracker.
    * If a transformer is implemented, it needs to be added as an enum field to
        1. `xyz.itzsomebody.radon.transformers.Transformers` and
        2. (Optional) Be documented accordingly.
* Update code or documentation to adhere to the specified style guides.
* Replace poorly-written or spaghetti code with properly-written and thought-out code.

The rules for guidelines are pretty simple:
* Follow the style guides.
* Fix your commits should status checks fail.
* If the PR adds features / code, make sure you also appropriately document said additions.
* No need to squash commits -- I will personally do that if I think it's necessary.

## Style guides
#### Git Commit Messages
* Use present tense.
* Use the imperative mood.

#### Java
All Java code should adhere to the provided EditorConfig file. Even though the column limit is set to 120, feel free to
ignore that.

#### Markdown
All markdown files are required to be hard-wrapped at 120 characters. The only exceptions are extremely long URLs and
code snippets.
