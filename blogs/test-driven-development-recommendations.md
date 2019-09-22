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
13. Repeat from Step 9 until all unit tests are done
14. Repeat from Step 8 until the acceptance test is passing
15. Refactor
16. Repeat from Step 4 until all given/when/thens have been tested

### Recommendations for brainstorming scenarios to test
When brainstorming which acceptance tests to write, we should try to upfront brainstorm most of the scenarios that we might want to eventually go on to test. However, it is not necessarily the case that we will be able to upfront come up with every scenario that we will want for the user story. It may be the case that as we start coding up the story, we learn about more scenarios that we had not thought about before. That is OK. We can add scenarios to our list at any time as we work on the story. The same holds true for brainstorming unit test cases.

### Recommendations for choosing which scenarios to test first
When it comes to choosing which failing acceptance test to start with, we should choose a simple happy path.<sup>b</sup> This will give us early confidence about our progress of the user story. The sad paths should be left for last. When brainstorming which failing unit test to write next, we can follow the same approach that is used for determining the acceptance test to write. It is important to note that once we write a failing acceptance test, we don't want to write another failing acceptance test until we get our acceptance test passing. Focusing on one acceptance test at a time will make it easier. The same could be said for unit tests. We would ideally have one failing acceptance test and one failing unit test at a time.

### TDD Unit Test Example
Plan: we want to make a class called Adder with a method called 'add' which adds two integers together.

We will use TDD to solve this problem.

The first step is to write down some scenarios that we are interested in testing. We should aim to get most of the scenarios written down right now. This list we are making is not finalized though. As we attempt to solve these problems, we might discover new problems. If this happens, we can always add or remove scenarios from our list of tests.
```
add two positive numbers
add one positive and one negative number
add two negative numbers
```
We want to start with a happy path that is simple. We should focus on negative paths, error-cases, and edge cases last. This means that the first test we will write is the test for adding two positive numbers. We will write the test case before the implementation code
```
# neither of these two lines will compile but that is OK
Adder adder = new Adder();
assertThat(adder.add(2,3), is(5));
```
We can now create an Adder class with an add method to remove the compilation error
```
public class Adder{
 public int add(int n1, int n2){
  return 0;
 }
}
```
We can run our test to see that it fails. We need to make it pass. There are three strategies that can be used to make the test pass.

#### Solving the problem with 'fake it' strategy
The 'fake it' strategy involves hard-coding a value to make your test pass and gradually replacing hard-coding with variables until you have real code. Using the 'fake it' strategy, we can make our pass test by just returning 5.
```
public class Adder{
 public int add(int n1, int n2){
  return 5;
 }
}
```
Now that we were able to get this test to pass, we can refactor it.
```
public class Adder{
 public int add(int n1, int n2){
  return n1 + n2;
 }
}
```
We now re-run our test to make sure it still passes.

#### Solving the problem with Triangulation
The triangulation strategy involves starting off by passing a failing test with a hard-coded return value just like how the 'fake it' strategy started
```
public class Adder{
 public int add(int n1, int n2){
  return 5;
 }
}
```
Our test of adding 2 and 3 now passes. Instead of refactoring to completely solve the problem in a more generic way, we write a second test that is similar to the first test but with slightly different numbers.

Adder adder = new Adder();
assertThat(adder.add(6,7), is(13));
This test fails since our code is hard-coded to return 5. We cannot update our 'add' method to return 13 to pass this test since that will cause the first test to fail.

We have to think of a generic way to solve the problem. We brainstorm a generic way to solve the problem.

public class Adder{
 public int add(int n1, int n2){
  return n1 + n2;
 }
}
We now have both of our tests passing which indicates that our code is working in a way that is generic. We may optionally delete one of the two tests if we feel it is no longer adding value.

#### Solving the problem with Obvious implementation
The two previous techniques involved solving the problem by taking small steps. This technique solves the problem with larger steps. When it is obvious as to what the implementation code should look like, go ahead and write it.
```
public class Adder{
 public int add(int n1, int n2){
  return n1 + n2;
 }
}
```
Our test is now passing.

#### When to use which strategy
When the implementation is obvious, use the obvious implementation approach. If the problem is a little harder, go with the fake it approach. If you still can't figure out the problem after trying the fake it approach, then try the triangulation approach.

#### Baby steps vs large steps
Taking baby steps involves writing a failing test and re-running the test after each minor code change to the implementation.  

Taking large steps involves writing a failing test and re-running the test less frequently as you make code changes to the implementation. You might run a failing test after typing it up and then not run it again until you predict you have written all the real implementation code to make the test pass. 

Most of the time we will want to take very small steps so that we do not end up in long troubleshooting sessions. However, taking extremely small steps when solving trivial problems can be tedious and can slow down velocity without making the problem any easier to solve. Thus the easier the problem, the larger the steps you should take. The harder the problem, the smaller the steps you should take. For example, you might run the unit tests after every 1-3 lines of implementation code written for a difficult problem but every 4-7 lines of implementation code written for an easy problem. These unit tests should be very fast so there isn't much excuse to going very long before running them in either case.  

#### Continuing on the example
We have now completed one of our test cases. We still have two more.
```
add two positive numbers # completed
add one positive and one negative number
add two negative numbers
```
We decide to work on adding two negative numbers
```
# neither of these two lines will compile but that is OK
Adder adder = new Adder();
assertThat(adder.add(-1,-2), is(-3));
```
Now that we have written our test, we run it. In this situation, the test actually passes because the implementation code that we previously wrote for adding two positive numbers happens to cause this test to pass. Most of the times that we add a test, it will fail. Due to the example I picked, this happened to not be the case. We are about to start writing our last test but it just dawned on us that someone could pass in 0 as input. For whatever reason, we decide this is a valid scenario we need to test. As a result, we add it to our list
```
add two positive numbers # completed
add one positive and one negative number
add two negative numbers # completed
add two zeroes
add one zero and one non-zero number
```
We decide to write the test for adding a positive and negative number. Just because of the nature of this example, the test passes when we run it without us having to add or modify any implementation code. This will usually not be the case. We continue on and write the next test and repeat the process until all test cases pass. We are now done!

#### Additional Recommendations/Questions answered
Write the unit test backwards
Write tests in this order:
1. write the test name so we know what we are testing
2. write the call to the code
3. write the expectation/assertion
4. write the setup
5. write the teardown
The purpose of this order is to focus on what we want the system to do rather than how we want it to do it

##### Only write enough implementation code to pass your current failing test case, ignoring other planned test cases

Lets say you have written down 5 unit test scenarios for the new class you are working on. After writing the first failing test, you might feel tempted to write the entire implementation for the whole class. After writing the entire implementation for the whole class, your test would pass. You would then write down the rest of the test cases for the remaining 4 unit test scenarios and they would all pass. Don't do this. Only write enough code to make your current failing test case pass. After that you can optionally refactor. After that, it is time to write your next failing unit test before writing any implementation code. This will keep the feedback loop short

##### Continuous Integration states that I should be merging to master multiple times per day. But now that I am writing the acceptance test first, it breaks the build for my branch. It takes more than a day to make my acceptance test pass. How can I still be merging to master multiple times per day while using ATDD?
This problem was brought up in the book 'Growing Object-Oriented Software, Guided by Tests.' Incomplete features should be hidden behind a feature flag. If a feature is hidden behind a feature flag, then it is OK to not have an acceptance test run against that feature in the CI build. If a feature is not hidden behind a feature flag, then the acceptance tests need to all pass for that feature and be part of the CI build. There should be a suite that can run all acceptance tests for features that have been implemented. This suite needs to run as part of the CI build and pass. There can then be a second suite which is for features that have not been fully implemented which needs to be for features hidden behind a feature flag and not running as part of the CI build. This suite for features that have not yet been implemented could be ran at any time locally and will almost certainly fail. Each individual developer will never have more than one failing acceptance test since they make that acceptance test pass before writing another acceptance test. However, if there are two developers working on different tasks in the same GitHub repository, there could be more than one failing acceptance test in total for the GitHub repository. That is OK. Keep in mind that the same philosophy does not apply to unit testing. All unit tests should always run and always pass as part of the CI build.

##### When needing to add/change constructor signature, method signature, or a field on the class, should I update the test first or the implementation?
We should update the test first. You would write code in the test file that produces a compilation error and then update the implementation to remove the compilation error.

##### When doing TDD for unit testing new complex classes that require mocks, my velocity slows down a lot. I have to up-front predict all of the mocked dependencies my class will need and which methods I am going to mock on the mocked classes. It results in a lot of up-front design before coding so that I will be able to write the full test case with all the mocks set up properly before writing any implementation. What should I do?
We don't want to have to do a lot of up-front design before coding because as we code, we will learn more about the problem we are trying to solve which might contradict some initial assumptions that we made. It is also very difficult and time-consuming to predict all of the mocks our class is going to need. Instead of writing the initial failing unit test with all of the mocks, you can write that failing unit test with no mocks. You will then start writing up the implementation code normally. As you are writing the implementation code, you might observe there is going to have to be an external dependency added such as PatientDAO in order to retrieve patient demographics information. You might feel tempted to go ahead and add PatientDAO to your implementation code constructor followed by fixing the tests to compile. Or you might feel tempted to go ahead and add a mockPatientDAO object to your constructor in the test file followed by adding the PatientDAO class in the implementation code to make your test compile. Don't do either of these. Lets try to get the test passing before we refactor (the refactor would be adding a dependency to the constructor). Another option is to hard-code a PatientDAO with the new operator inside of the implementation code. We don't want to do that either because that cannot be mocked and hides dependencies from the consumer. Instead of adding a PatientDAO to your implementation code, just hard-code a return value in your method to pass the failing unit test. Now you can go and add a mockPatientDAO to the constructor in your unit test followed by adding a PatientDAO to the constructor in the implementation code to fix the compilation error in the test. You can then replace the hard-coding in the implementation with the PatientDAO call so that the test is now passing without hard-coded data.

## Sources
a. Freeman, Steve. Growing Object-Oriented Software, Guided by Tests. Addison-Wesley, 2012.  
b. Beck, Kent. Test-Driven Development By Example. Addison-Wesley, 2003.  
