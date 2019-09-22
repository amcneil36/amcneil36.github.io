## Test-Driven-Development Recommendations
Test-Driven-Development (TDD) involves writing a failing test and then writing the implementation code needed to pass the test. Some people do TDD with unit tests. They write a failing unit test, write the minimum code needed to pass the test, and then refactor their solution. This is known as red-green-refactor. Some people do TDD with acceptance tests (ATDD) where they do red-green-refactor with acceptance tests. The best way to do TDD is to use it for both acceptance tests and unit tests.<sup>a</sup> This is sometimes referred to as double loop TDD.

### Double Loop TDD
Double Loop TDD involves an outer loop where we do red-green-refactor on our acceptance tests. In order to make our acceptance test go from red to green, we do red-green-refactor on unit tests (inner loop). Here is an image from [Waldemar Mękal’s presentation on double loop TDD](https://www.youtube.com/watch?v=c9FdwL1_TBE&feature=youtu.be) that displays this process

![](http://cezary.mcwronka.com.hostingasp.pl/wp-content/uploads/2016/03/DoubleLoopTDD-1024x652.png)
Here are the steps explained at a lower level that I recommend using for implementing double loop TDD to complete user stories:
1. Brainstorm user stories
2. Pick a user story
3. Brainstorm given/when/thens (acceptance criteria)
4. Choose one given/when/then
5. High-level design for acceptance test (if needed)
6. Code up failing acceptance test
7. High level design for implementation code needed to pass the acceptance test
8. Brainstorm unit test cases
9. Choose one unit test
10. Write a failing unit test
11. Write the minimum amount of code to make the test pass
12. Refactor
13. Repeat Step 9 until all unit tests are done
14. Repeat Step 8 until the acceptance test is passing
15. Refactor
16. Repeat Step 4 until all given/when/thens have been tested
17. Repeat Step 3 until the user story is completed

### Recommendations for brainstorming scenarios to test
When brainstorming which acceptance tests to write, we should try to upfront brainstorm most of the scenarios that we might want to eventually go on to test. However, it is not necessarily the case that we will be able to upfront come up with every scenario that we will want for the user story. It may be the case that as we start coding up the story, we learn about more scenarios that we had not thought about before. That is OK. We can add scenarios to our list at any time as we work on the story. The same holds true for brainstorming unit test cases.

### Recommendations for choosing which scenarios to test first
When it comes to choosing which failing acceptance test to write next, we should choose a simple happy path.<supb</sup> This will give us early confidence about our progress of the user story. The sad paths should be left for last. When brainstorming which failing unit test to write next, we can follow the same approach that is used for determining the acceptance test to write. It is important to note that once we write a failing acceptance test, we don't want to write another failing acceptance test until we got our acceptance test passing. Focusing on one acceptance test at a time will make it easier. The same could be said for unit tests. We would ideally have one failing acceptance test and one failing unit test at a time.

## Sources
a. Freeman, Steve. Growing Object-Oriented Software, Guided by Tests. Addison-Wesley, 2012.  
b. Beck, Kent. Test-Driven Development By Example. Addison-Wesley, 2003.
