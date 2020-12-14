## Additional Coding Recommendations

#### 1) Favor depending on an interface over depending on an implementation of an interface.
Lets say you have FooImpl which implements FooInterface.
* Lets say that Class A takes in a FooImpl in it's constructor. If a non-passive code change is made to FooImpl, now Class A has received a non-passive change
* Lets say that Class B takes in a FooInterface in it's constructor. If a non-passive code change is made to FooImpl, Class B has not received a non-passive change

#### 2) Favor interfaces over subclassing when needing  re-usability
* There are a few downsides of subclassing:
  * You are coupled to the superconstructor's implementation. If the superconstructor has any side effects or parameters, you are forced to go through those side effects and pass in whichever parameters are needed
  * If a method m1 of a superclass calls into some other method m2 of the superclass, then having your subclass override m2 can actually change the behavior of m1. This can create for very difficult to debug scenarios
  * If any code change is made to the class you are subclassing, then your subclass could be affected despite you not making a code change. This can also create a difficult to debug scenario
* With interfaces, the benefits you receive include
  * Complete control over your constructor(s)
  * As long as no method signatures are created/modified, you will not be the victim of a non-passive code change
    * when a non-passive code change is made to an interface, you will find out at compile time which is easier to troubleshoot
  * Not needing an implementation of your dependencies by the time you are ready to start coding your class. If you depend on an interface, you can fully unit test your class, passing in a mock for the interface. This makes it easier to do vertical slices in agile development. Integration tests, however, are not possible until an implementation is completed.
#### 3) Favor interfaces over abstract classes
Abstract classes are less-reusable than interfaces due to the issues outlined above on subclassing.
#### 4) Test behavior, not implementation
* Consumer's don't experience implementation. They experience behavior. Implementation is something that should be easy and free to change at any time
* When testing behavior, we might assert that the return value is correct
* When testing implementation, we might verify that a mock called a certain method
  * However, there is one exception. If the method you are verifying has a side effect like inserting into the database, then that is considered testing behavior
* Testing implementation results in tests that have an unclear purpose
* Refactoring the code without changing behavior should not break your unit tests. If your unit tests break after every minor re-factor, it will take significantly more work to maintain your tests
#### 5) Know what to test in an application
* Never directly call into any package-private or private methods in a unit test as this is testing implementation details. In order to make sure that a package-private or private method is working as intended, call into it through your public/protected API
* Unit test all public/protected methods. Mock out all side effects in your unit tests. In this situation, we don't care whether or not a database connection works
* Write an integration test (no mocking) for public/protected methods that have side effects mocked out in their unit tests. It may not be necessary to do integration test all public/protected methods with side effects if you feel comfortable enough from your acceptance tests that call into them
* Write acceptance tests against acceptance criteria. This is end-to-end testing where we test the entire workflow of the application
#### 6) Override hashcode, equals, and toString on entity/pojo/value object classes
* The purpose of overriding hashcode and equals is so the consumer is provided a way to check for value equality. Consumers can already check for reference equality by using the == symbol
* Overriding toString makes it easier to see what the values of each field are
#### 7) Do not make code changes that exist solely for the sake of testing. Here are some anti-patterns below:
* Having two constructors where one constructor (usually package-private) only exists for testing
* Increasing visibility of a method from private to package-private solely for the sake of directly invoking the method in a test
* Increasing the visibility of a field from private to package-private solely for the sake of setting it in a test
* Adding in a setter to a field solely for the sake of setting it in a test
#### 8) All of your code should be easily testable by passing in mocks to parameters
Do not do anything hacky in your tests. If doing something hacky is the only way to test your code, then you are following poor design practices. This means that you are too coupled to implementation details. Here are some anti-patterns below that might appear when some people try to unit test poorly designed code.
* Modifying global state in order to run your tests
* Using reflection to set the value of private fields
* Using a 3rd party framework to mock static/final/new operators
* Subclassing, mocking, spying the class under test
#### 9) Constructors should have no side effects or logic. They should just do assignment
Having a constructor throw if invalid parameters are passed in is fine. Other than that, the constructor should only do field assignment. For example, it should primarily be "this.field1 = parameter1; this.field2 = parameter2;" etc. Do not create any new objects or put side effects in the constructor.
* Having work in your constructor reduces re-usability as any subclass is coupled to your constructor
* Having side effects in a constructor will slow down your application's startup if it is one of the class' used in your entry point's object graph. Having side effects in the constructor will also create for difficult to predict and difficult to design software. For example. Say that class Foo requires for a transaction to be active in order to instantiate it. When you are doing the design for your task, you might not be aware that Foo's constructor requires a transaction to be active. You might decide which classes take in a Foo in their constructor, only to figure out later that your design may not work as a transaction may not be active at the necessary time
#### 10) Either fix warnings or suppress them.
If you are going to suppress a warning, add a source-code comment explaining why you are suppressing it. The correct approach most of the time will be to fix the warning. However, don't fix a warning just because it is a warning. If you have done some research and you feel that the warning is not worth the time to fix or that it is not really an issue, go ahead and suppress it.
#### 11) Aim for one assert per test
If a test with multiple asserts fails, it takes longer to determine what actually went wrong.
#### 12) No tests should affect other tests. The order that the tests are ran should not affect the output
If tests are modifying any objects that are used throughout the class, those objects should be re-initialized before each test.
#### 13) Never return null
Returning null requires the consumer to have to do a null check which makes the code more cluttered. We can return default values, an optional, or throw instead.
#### 14) Do not use interfaces for POJO/entity/value objects
This causes adding a field to the class to be a non-passive change, assuming a corresponding getter needs to be added to the interface.
#### 15) A client should not be forced to depend on methods that it does not use (Interface Segregation Principle)
* Multiple methods should go on the same interface if overriding one method will likely result in needing to override the other methods
* Interfaces should favor having few methods over many methods as implementing an interface means that all methods need to be overidden
#### 16) Favor immutable fields over mutable fields
This makes code thread-safe and easier to follow.
#### 17) Methods should be small-generally five lines of code or less
* If a method starts doing too much, refactor code into a private method or another class
* For loops and the content within a for loop should be extracted into their own method
#### 18) Classes should generally be 300 lines of code or less
If a class starts doing too much, extract some of the work out into another class.
#### 19) Favor one logical statement per line of code over method chaining except for when using fluent interfaces
* Fluent interfaces are designed to where method chaining is easier to read and typically have a lot of methods on a class whose return type is the class. These classes read like English when there is method chaining done
  * Examples may include Mockito API, builder pattern, java.util.Optional, streams, and many more functional programming libraries. Use method chaining with these
* Method chaining (when being used on something other than a Fluent Interface) generally makes code harder to read. It also makes code harder to troubleshoot as the stack trace may point to a line of code that has multiple method calls. Refactoring code that is method chained with the Introduce Explaining Variable refactor can make your code easier to follow
#### 20) Ya Ain't Gonna Need It (YAGNI)
* Implement things that you need now instead of things that you think you will need later
* Delete code that is no longer getting called from anywhere
* Don't open a spike unless you have a task that is blocked by the spike
#### 21) Keep It Simple Stupid (KISS)
* Try to minimize complexity in your classes
  * Replace conditional with polymorphism
* Try to avoid doing anything hacky
* Avoid global state, side effects, and mutability as much as possible
#### 22) Don't pass in one object as a means of getting another object. Pass in directly what you need (law of demeter)
* This promotes information hiding, decreases coupling, makes the class more re-usable, and makes the class easier to understand (KISS)
* A cashier at McDonalds doesn't need your wallet in order to place your order. The cashier needs your $5. It should not matter if the $5 comes from a wallet, or a purse
* Don't have a FooFactory be a dependency of your class if the way for creating a Foo is known at compile time (in other words, known how to be made by a DI framework). Have your class depend on Foo instead. If the way for creating a Foo depends on runtime arguments, then it is fine to depend on a FooFactory instead
* There can be an exception to the rule of passing in directly what is needed. Lets say that you have User object which just has getter/setter for the fields firstName, middleName, lastName, age
  * If a method requires only firstName, have the method take in the firstName instead of the User object
  * If a method requires the User object, pass in the User object instead of passing in all of the fields of the user
  * If a method requires two or more fields of the User, then whether or not you should pass in the User object or the fields of the User object is subjective. I'll provide my two cents
    * If having your method being coupled to the User object is not going to cause code duplication elsewhere, then pass in the whole User object. This would mean that you do not have a use-case where you need, for example, firstName and lastName fields that might come from something other than a User object. Generally, the more fields of a POJO that a method needs, the more likely it is that you would want to pass in the POJO instead of the individual fields. This is because passing in the User object helps reduce the number of parameters, helping readability
#### 23) Only validate parameters at the boundary points of your application
* This would be any portion of your code where you cannot control what value of a parameter is passed in (generally the entry-point of an application or a parameter that comes from the result of an external call)
* You should code your application up such that the internals will not be able to receive bogus input in the first place (except for boundary points)
  * Being able to assume that you will receive valid input will allow you to keep your tests and classes simpler (KISS)
#### 24) Don't repeat yourself (DRY)
* Once you have common code repeated three times, extract it out into a common method (Rule of Three)
  * if the common code is only repeated twice but it is a ton of lines of code, go ahead and extract
#### 25) Do not optimize for performance without having metrics
Suppose you have two possible solutions to a problem in mind. One solution is predicted to be easier to implement than the other but is predicted to have worse performance. You should generally try the easier solution and performance test it to see if the performance is OK. We want to performance test our solution as early as possible. This might even include writing some dummy code to mirror the easier solution and performance testing that. This way, less code gets tossed if our performance test is unsatisfactory. If our performance test results in slow results, then try the more complicated solution. Most of the time performance will not be an issue.

## Sources
1. Fowler, Martin and Beck, Kent. Refactoring: Improving the Design of Existing Code. Addison-Wesley, 1999.  
2. Martin, Robert. Clean Code: A Handbook of Agile Software Craftsmanship. Prentice Hall, 2009.  
3. Bloch, Joshua. Effective Java. Pearson Education, 2018.  
4. McConnell, Steve. Code Complete: A Practical Handbook of Software Construction. 2004.  
5. Martin, Robert. Clean Architecture: A Craftsman's Guide to Software Structure and Design. Prentice Hall, 2018.  
