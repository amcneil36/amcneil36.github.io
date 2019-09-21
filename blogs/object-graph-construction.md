## Object Graph Construction
In your application, you may have many objects created and destroyed at different points in time. The way we code up our application has a significant difference on how th object graph will look. Should we try to have most of our objects instantiated at the entry-point of our application? Should objects only be instantiated right before they are used? Should our classes internally create all of the objects they need or should they ask for the objects they need? We will take a look.

### Having your class internally create objects it depends on
Suppose we have a class that creates an object it depends on. Lets say the object it makes is called DatabaseAuthenticatorImpl which implements DatabaseAuthenticator.
```
public class Foo {

 private final DatabaseAuthenticator databaseAuthenticator;
 
 public Foo(){
  this.databaseAuthenticator = new DatabaseAuthenticatorImpl();
 }
 .
 .
 .
}
 ```
In this situation, we are programming to implementation. Since our dependency is hard-coded with the new operator, we cannot subclass it for more re-usability. We also are forced to have our class pick an implementation of the DatabaseAuthenticator interface so we cannot switch implementations without making a non-passive change. To make our code more simple and re-usable, we can use dependency injection.

### Dependency Injection
Suppose instead, we design our class like this
```
public class Foo {

 private final DatabaseAuthenticator databaseAuthenticator;
 
 public Foo(DatabaseAuthenticator databaseAuthenticator){
  this.databaseAuthenticator = databaseAuthenticator;
 }
 .
 .
 .
}
```
Now our Foo class is more re-usable. We are passing in the interface through the constructor so the consumer can choose any implementation of the DatabaseAuthenticator interface they want. Furthermore, they can potentially use subclassing on any implementations of the interface provided that the implementation is not a final class. Consumers can switch which implementation of DatabaseAuthenticator they use without a non-passive change being made to Foo. Our Foo class is also much more simple now. It doesn't need to know how to create a DatabaseAuthenticator. 
