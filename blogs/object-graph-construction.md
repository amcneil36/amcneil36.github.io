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
Let's say that we made a PersonRepository class that inserts someone's name into the database.
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
We can then create the PersonRepositoryFactory at the entry-point of our application. However, there is a way we can get around needing a factory. Suppose we had designed PersonRepository differently:
```
public class PersonRepository {
 
 public PersonRepository(){}

 public void insertByName(String name){
 .
 .
 }
}
```
Now we can instantiate a PersonRepository at the entry-point of our application.
