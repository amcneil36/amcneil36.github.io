## Additional Coding Recommendations

#### 1. Favor depending on an interface over depending on an implementation of an interface.
Lets say you have FooInterface and FooImpl which implements FooInterface.
* Lets say that Class A takes in a FooImpl in it's constructor. If a non-passive code change is made to FooImpl, now Class A has received a non-passive change.
* Lets say that Class B takes in a FooInterface in it's constructor. If a non-passive code change is made to FooImpl, Class A has not received a non-passive change.
#### 2. Favor interfaces over subclassing when needing  re-usability
* There are a few issues with subclassing:
  * You are coupled to the superconstructor's implementation. If the superconstructor has any side effects or parameters, you are forced to go through those side effects and pass in whatever is valid for those parameters
  * If a method of a superclass m1 calls into some other method of the superclass m2, then having your subclass override m1 can actually change the behavior of m2. This can create for very difficult to debug scenarios
  * If any code change is made to the class you are subclassing, then your subclass could be affected despite you not making a code change. This can also create a difficult to debug scenario
* With interfaces, the benefits you receive include
  * Complete control over your constructor(s)
  * As long as no method signatures are created/modified, you will not be the victim of a non-passive code change
  * Not needing an implementation of your dependencies needing to be completed by the time you are ready to start coding your class. If you depend on an interface, you can fully unit test your class, passing in a mock for the interface. This makes it easier to do vertical slices in agile development. Integration tests, however, are not possible until an implementation is completed.
#### 3) Favor interfaces over abstract classes
a. Abstract classes are less-reusable than interfaces due to the issues outlined above on subclassing
#### 4) Factory pattern
a. Useful when an object is expensive to instantiate so you want to defer instantiation until right before the object is needed
b. Useful when an object depends on runtime parameters in order to be created
c. May have multiple create methods
d. May have multiple new operators
e. Should ideally not contain conditional statements or looping
#### 5) Test behavior, not implementation
a. The largest impact that the consumers will experience is the behavior. Implementation is something that should be easy and free to change at any time.
b. When testing behavior, we might assert that the return value is correct.
c. When testing implementation, we might verify that a mock called a certain method.
d. Testing implementation creates for difficult to understand tests. 
e. Refactoring the tests without changing implementation should not break your unit tests. Unit tests should only break if there is a bug introduced in your code. If your unit tests break after every minor re-factor, it will take significantly longer to maintain your tests with little benefit
f. It is okay to verify that a method was called if the method you are verifying has a side effect. Side effects are considered behavior. This is the only way to test void methods. If a void method has no side effects, then you can delete it. Other than verifying side effects, the only behavior being tested should be exceptions getting thrown (correct message + correct exception type) or a method's return values
#### 6) Know what to test in an application
a. Never directly call into any package-private or private methods in a unit test as this is testing implementation details. In order to make sure that a package-private or private method is working as intended, directly call into it through your public/protected API.
b. Unit test all public/protected methods. Mock out all side effects in your unit tests. In this situation, we don't care whether or not a database connection works.
c. Write an integration test (no mocking)  for all publc/protected methods that had any side effects mocked out in their unit tests. In this situation we care about whether or not a database connection works.
d. Write acceptance tests against requirements. This is end-to-end testing where we test the entire workflow of the application.
#### 7) Javadoc all public/protected fields/constructors/methods in the src/main/java directory. Do not JavaDoc anything in the test directory. Your JavaDocs should explain what your API does but not how it does it. In other words, consumers are conserned about behavior and not implementation. Implementation should be easy to change.
#### 8) Override hashcode, equals, and toString on entity/pojo/value object classes
a. The purpose of overriding hashcode and equals is so the consumer is provided a way to check for value equality. Consumers can already check for reference equality by using the == symbol. Value equality helps greatly when asserting on the return value in a unit test.
b. Overriding toString makes it easier to see what the values of each field are
#### 9) Do not make code changes that exist solely for the sake of testing. Here are some anti-patterns below:
a. Having two constructors where one constructor (usually package-private) only exists for testing
b. Increasing visibility of a method from private to package-private soley for the sake of directly invoking the method in a test
c. Increasing the visibility of a field from private to package-private soley for the sake of setting it in a test
d. Adding in a setter to a field soley for the sake of setting it in a test
#### 10) All of your code should be easily testable by passing in mocks to parameters. Do not do anything hacky in your tests. If doing something hacky is the only way to test your code, then you are following poor design practice. This means that you are too coupled to implementation details. Here are some anti-patterns below that might appear when some people try to unit test poorly designed code.
a. Modifying global state in order to run your tests (including usage of Madhatter, ServiceProviders)
b. Using reflection to set the value of private fields
c. Using powermock or a 3rd party framework to mock static/final/new operators
d. Subclassing, mocking, spying the class under test
e. Using a dependency injection framework in a unit test (using them in an integration test is fine though)
#### 11) Constructors should have no side effects or logic. They should just do assignment
a. Having a constructor throw if invalid parameters are passed in is fine. Other than that, the constructor should only do field assignment. For example, it should primarily be "this.field1 = parameter1; this.field2 = parameter2;" etc. Do not put any new operators or side effects in your constructor.
i. Having work in your constructor reduces re-usability as any subclass is coupled to your constructor
ii. Having side effects in a constructor will slow down your application's startup if it is one of the class' used in your entry point's object graph. Having side effects in the constructor will also create for difficult to predict and difficult to design software. For example. Say that class Foo requires for a transaction to be active in order to instantiate it. When you are doing the design for your task, you might not be aware that Foo's constructor requires a transaction to be active. You might decide which classes take in a Foo in their constructor, only to figure out later that your design may not work as a transaction may not be active at the necessary time.
#### 12) Either fix all warnings or suppress them. If you are going to suppress a warning, add a source-code comment explaining why you are suppressing it. The correct approach most of the time will be to fix the warning. However, don't fix a warning just because it is a warning. If you have done some research and you feel that it makes more sense to suppress a particular warning rather than fix it, go ahead and suppress it.
#### 13) Aim for one assert per test
a. If a test with multiple asserts fails, it takes longer to determine what actually went wrong
#### 14) No tests should affect other tests. The order that the tests are ran should not affect the output
a. If tests are modifying any objects that are used throughout the class, those objects should be re-initialized in the @Before method
#### 15) Know when to pass an object vs passing primitives vs making a criteria object
#### 16) Know when to program to put the new operator in your application
a. Factory classes can have the new operator for any  type of object
b. Remaining classes should generally not have the new operator show up except for the creation of classes such as
i. Collections
ii. POJO/entity/value objects
iii. Other classes that have no side effects and minimum logic in them
#### 17) Never return null
#### 18) Do not use interfaces for POJO/entity/value objects
a. This causes adding a field to the class to be a non-passive change
#### 19) A client should not be forced to depend on methods that it does not use (Interface Segregation Principle)
a. Multiple methods should go on the same interface if overriding one method will likely result in needing to override the other methods
b. Interfaces should favor having few methods over many methods as implementing an interface means that all methods need to be overidden
#### 20) Favor immutable fields over mutable fields
a. This makes code thread-safe and easier to follow
#### 21) Methods should be small-generally five lines of code or less
a. If a method starts doing too much, refactor code into a private method or another class
b. For loops and the content within a for loop should be extracted into their own method
#### 22) If a class starts doing too much, extract some of the work out into another class
#### 23) Avoid using source code comments to explain what your code is doing. If you feel the need to add source code comments, then your code is too complicated and is not self-documenting. You can fix this by extracting out private methods or in-line variables whose name explains what the source code comment would have said
a. Extract out code with a lot of logic into it's own private method with a long descriptive name explaining what the code is doing
b. Magic numbers need to be replaced with a constant that explains what the number is for
c. Consider extracting out boolean logic into a boolean variable or boolean method that explains the purpose.  
#### 24) Favor one logical statement per line of code over method chaining except for when using fluent interfaces
a. Fluent interfaces are designed to where method chaining is easier to read and typically have a lot of methods on a class whose return type is the class. These classes read like english when there is method chaining done.
i. Examples may include Mockito API, builder pattern, java.util.Optional, streams, and many more functional programming libraries. Use method chaining with these all day
b. Method chaining (when being used on something other than a Fluent Interface) generally makes code harder to read. It also makes code harder to troubleshoot as the stack trace may point to a line of code that has multiple things going on. Refactoring code that is method chained with the Introduce Explaining Variable refactor can make your code easier to follow.
#### 25) Ya Ain't Gonna Need It (YAGNI)
a. Implement things that you need now instead of things that you foresee you will need
b. Delete code that is not getting called from anywhere
c. Don't open a spike unless you have a task that is blocked by the spike
#### 26) Keep It Simple Stupid (KISS)
a. Try to minimize complexity in your classes
i. Replace conditional with polymorphism
b. Try to avoid doing anything hacky
c. Avoid global state, side effects, mutability as much as possible
#### 27) Don't pass in one object as a means of getting another object. Pass in directly what you need (law of demeter)
a. This promotes information hiding, decreases coupling, makes the class more re-usable, and makes the class easier to understand (KISS)
b. A cashier at McDonalds doesn't need your wallet in order to place your order. The cashier needs your $5. It should not matter if the $5 comes from a wallet, a purse, or a pocket
c. Don't have a FooFactory be a dependency of your class if the way for creating a Foo is known at compile time (in other words, known how to be made by a DI framework). Have your class depend on Foo instead. If the way for creating a Foo depends on runtime arguments, then it is fine to depend on a FooFactory instead
i. Using similar logic, never pass in a TracerManager to a class if all the class needs is a Tracer. Pass in the Tracer
d. There can be an exception to the rule of passing in directly what is needed. Lets say that you have User object which just has getter/setter for the fields firstName, middleName, lastName, age
i. If a method requires only firstName, have the method take in the firstName instead of the User object
ii. If a method requires the User object, pass in the User object instead of passing in all of the fields of the user
iii. If a method requires two or more fields of the User, then whether or not you should pass in the User object or the fields of the User object is subjective. I'll provide my two cents
1) If having your method being coupled to the User object is not going to cause code duplication elsewhere, then pass in the whole User object. This would mean that you do not have a use-case where you need, for example, firstName and lastName fields that might come from something other than a User object. Generally, the more fields of a POJO that a method needs, the more likely it is that you would want to pass in the POJO instead of the individual fields. This is because passing in the User object helps reduce the number of parameters, helping readability.
#### 28) Only validate parameters at the boundary points of your application
a. This would be any portion of your code where you cannot control what value of a parameter is passed in (generally the entry-point of an application or a parameter that comes from the result of an external call)
b. You should code your application up such that the internals will not be able to receive bogus input in the first place (except for boundary points)
i. Being able to assume that you will receive valid input will allow you to keep your tests and classes simpler (KISS)
#### 29) Don't repeat yourself (DRY)
a. Once you have common code repeated three times, extract it out into a common method (Rule of Three)
i. if the common code is only repeated twice but it is a ton of lines of code, go ahead and extract
#### 30) Know when to refactor previously existing code
a. Martin Fowler: "You don't decide to refactor, you refactor because you want to do something else, and refactoring helps you do that other thing." I will provide some examples below:
i. You have to do a task that depends on other code in your project that you are having a hard-time understanding. You can refactor the code that you depend on until you understand it well enough. You can then start doing your coding task. You will upload both the refactoring code and your code that you eventually planned to do for review. Whether or not you want them separate reviews depends on the length of the refactor
ii. You have to do a task that depends on other code in your project and you believe that a simple refactor to other code would make your task easier (perhaps less lines of code or less complexity). You may refactor the code that you depend on in order to make your task easier and upload both for review (either in the same review or separate)
#### 31) Unit test all newly written code no matter what. When doing refactoring or modification of any existing non-tested code: add tests first, refactor, fix tests if needed. You shouldn't add unit tests to legacy code unless you are changing it.
#### 32) Favor unchecked exceptions over checked exceptions. Don't use status objects for success/failure unless the caller is from a different programming language that is not capable of handling an exception
#### 33) Class names should be nouns, method names should be verbs
