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

### When using dependency injection, where do all of our objects get instantiated?
We could keep having our classes ask for the consumer to pass in the dependencies they need instead of having our classes internally instantiate the dependencies they need. But if we keep doing this, we need these dependencies to get instantiated somewhere, right? Where do these dependencies all get instantiated? Ideally, they would get instantiated as close to the entry-point of the application as possible.

Suppose we create all of our objects at the entry point of the application. This means that all of our other classes will be very re-usable and are going to have less logic in them since they are not concerned about creational logic. Our entry-point will have a bunch of dependencies hard-coded with new operators though. Doesn't that mean that consumers calling into our entry-point are now coupled to implementation details and have lost re-usability? No. An entry point is where the application starts. Nothing calls into it. If something did, then it wouldn't be an entry point.

#### Some objects cannot get instantiated at the entry-point of our application due to having constructor arguments not known at compile time
Let's say there is a PersonRepository class that inserts someone's name into the database.
```
public class PersonRepository {
 private final String name;
 
 public PersonRepository(String name){
  this.name = name;
 }

 public void insertByName(){
 .
 .
 }
}
```
Furthermore, let's say that the entry point of our application doesn't know the person's name. Maybe our application has to read some data from the database before getting a person's name. In this scenario, we wouldn't be able to instantiate a PersonRepository at the entry point of our application because PersonRepository depends on name which we don't know at the entry point of our application. This means we would need somewhere deeper down in our application to instantiate a PersonRepository. This would result in somewhere deeper down programming to implementation by creating the PersonRepository with the new operator. However, we can get around that by making a Factory
```
public class PersonRepositoryFactory {
 
 public PersonRepository createPersonRepository(String name){
  return new PersonRepository(name);
 }
}
```
We can then create the PersonRepositoryFactory at the entry-point of our application. Any class that needs a PersonRepository will take in a PersonRepositoryFactory through it's constructor.

#### Constructor inject dependencies known at compile time. Method inject dependencies known at runtime
Let's look at how the PersonRepository would have looked in the object graph had it been designed differently. Suppose PersonRepository method injected the name instead of constructor injecting it.
```
public class PersonRepository {
 
 public PersonRepository(){}

 public void insertByName(String name){
 .
 .
 }
}
```
Now we can instantiate a PersonRepository at the entry-point of our application and all classes that need a PersonRepository can depend on a PersonRepository instead of a PersonRepositoryFactory so they will be more simple.

So when deciding between constructor and method injection, I prefer constructor injecting dependencies that are known at compile time and method injecting dependencies that are known only at runtime. This allows for your objects to be created at the entry-point of the application. Sometimes you will depend on some 3rd party classes that were not designed this way so you could end up needing to create a factory method for instantiating those 3rd party classes.

### Another scenario where it makes sense to make a factory
We talked about needing to make factories in scenarios where we depend on classes who have some constructor arguments not known until runtime. There is another scenario in which a factory might make sense. The entry-point of our application might have 30 new operators if all of the logic for creating these dependencies is inside of the entry-point of our application. Instead of doing this, we should move the creational logic to a factory and just have the entry-point call into a factory to get the dependencies it needs. Since we know that factories are important, let's talk about some recommendations for them.

### Factory recommendations
Keep business logic out of factories. Factories should primarily just be called 'new' one or more times.

Objects that are needed for similar scenarios should go on the same factory in different create methods.

Just like any other classes, place dependencies known at compile time on the factory's constructor and dependencies known at runtime on the factory's method signatures.

Have the factory make direct dependencies that are needed. For example, suppose that your class needs an instance of A in order to create an instance of B.  Furthermore, suppose our application needs to call methods on B but doesn't need to call any methods on A. We should have a factory method that internally creates an instance of A then an instance of B and returns B. In other words, our factories should create the objects we directly need instead of objects that are only used to create another object. This helps simplify consuming classes.  

### Should all new operators go in factories?
It could be said that there are two types of classes. Factories and classes with business logic. The bulk of our objects will be created in factories. However, there are still some objects that can be created in our classes with business logic that don't need to be made by a factory. This would be classes like value objects, entities, and collections. These classes don't have any logic in them that we would need to override so programming to implementation and instantiating them in our business logic classes with the new operator is fine. 

### Service Locator pattern
