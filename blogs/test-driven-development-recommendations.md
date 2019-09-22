## Test-Driven-Development Recommendations
Test-Driven-Development (TDD) involves writing a failing test and then writing the implementation code needed to pass the test. Some people do TDD with unit tests. They write a failing unit test and then write the code needed to pass the test. Some people do TDD with acceptance tests where they write a failing acceptance test first and then write the code needed to pass the acceptance test (ATDD). The best way to do TDD is to use it for both acceptance tests and unit tests.<sup>a</sup> This is sometimes referred to as double loop TDD.

### Double Loop TDD
Double Loop TDD involves an outer loop where we write our failing acceptance test and an inner loop where we write one or more failing unit tests. We repeat this until our acceptance test passes and then we write another failing acceptance test. Here is an image from [Waldemar Mękal’s presentation on double loop TDD](https://www.youtube.com/watch?v=c9FdwL1_TBE&feature=youtu.be)

![test](http://cezary.mcwronka.com.hostingasp.pl/wp-content/uploads/2016/03/DoubleLoopTDD-1024x652.png)
Here are the steps I came up with for implementing double loop TDD:
1. Brainstorm user stories
1. Pick a user story
1. Brainstorm Given/when/thens
1. Choose one given/when/then
1. High-level design for acceptance test (if needed)
1. Code up acceptance test (will fail)
1. High level design for implementation code
1. Brainstorm unit test cases
1. Choose one unit test
1. Write a failing unit test
1. Write the minimum amount of code to make the test pass
1. Refactor
1. Repeat Step 9 until all unit tests are done
1. Repeat Step 8 until the acceptance test is passing
1. Refactor
1. Repeat Step 4 until all given/when/thens have been tested (if needed)
1. Repeat Step 3 until the user story is completed

## Sources
a. Freeman, Steve. Growing Object-Oriented Software, Guided by Tests. Addison-Wesley, 2012.
