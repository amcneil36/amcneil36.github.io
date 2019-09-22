## Code Documentation Recommendations
We will look into two different types of documentation: public API documentation and source code documentation. For purposes of this blog post, we will consider public API documentation to be any type of documentation that is used to explain code that consumers can call into. We will consider source code documentation to be any type of documentation that can only be seen by someone who is looking at the source code.

### Public API Documentation Recommends
For this documentation, we want to explain everything that someone calling into our code might want to know. This is so that people do not need to start pulling up our source code and reading through our code to determine how to use it. They should get everything they need to know through our API documentation. We want to make sure that we are documenting behavior and not implementation details. This means that the consumer is primarily just concerned with what the required input is and what the corresponding output is. In addition to that, there may be internal side effects that occur in the middle of the method such as sending an email. These internal side effects are still considered behavior as they have an effect on the outside world. Anything else that is happening internally but has no effect on the outside world is an implementation detail that the consumer does not need to know about.

We want to document what a method does (behavior) but not how it does it (implementation). For example, the documentation might say that our code looks up all zip codes for a given city and state. The documentation does not need to say that Hibernate is used to query the database to look this information. This is because the consumer doesn't need to know Hibernate in order to use this API.

Another example of behavior vs implementation can be thought of when turning on a computer. You need to know that pressing the power button on the computer turns the computer on. You do not need to know what happens inside of the computer after that power button is pressed.

API documentation should include documentation for things like public classes, public methods, and public fields.

When documenting the intput to our methods, there are few things we want to communicate:
* what each argument represents or stands for
* possible values that are not acceptable

For return values, we should document what is being returned and whether or not the return value could be null (or empty in the case of a collection). 

Documenting behavior instead of implementation has the added benefit that we do not need to change documentation after every minor refacdtoring.

When it comes to testing, we should also be testing our behavior. We essentially just need to make sure that our API does what our documentation says it does. Testing behavior also has the added benefit of not needing to fix all of the tests after every minor refactoring.

### Source Code Documentation
As stated above, we are considering source code documentation to be any type of documentation that someone looking at the source code might see. Most people who are calling into your API would never look at any of your source code documentation. The people who would read the source code documentation would be anyone who is a contributor to the repository that is storing the source code. As a result, source code documentation is more intended to help out the developers who are making changes to the source code. Unlike public API documentation, we really shouldn't need a lot of source code documentation. In many cases, there are ways to extract methods with a descriptive name to eliminate source code comments. Suppose we have this method with a source code doc explainign what a clump of code does:
```
.
.
// retrieves person from the database
Transaction transaction = openTransaction();
Connection connection = establishConnection();
Person person = personDAO.retrievePerson();
connection.close();
transaction.close();
.
.
```
This logic could be refactored to a private method. After that, our code would look like this:
```
.
.
Person person = retrievePersonFromDatabase();
.
.
```
Any time that you see a source code documentation explaining what a block of code does, you can replace the block of code with a private method and remove the source code documentation. This makes the code cleaner and quicker to read.

Another technique for eliminating source code is introducing an explaining variable. Suppose we have the following source code and source code doc:
```
.
.
// if the person is an adult
if (person.age > 18){
.
.
}
.
.
```
This can be refactored into 
```
.
boolean isPersonAnAdult = person.age > 18
if (isPersonAnAdult){
.
}
.
```
#### Documenting intention
Sometimes you might look at code and ask "why was this coded this way? Shouldn't we have just coded it this other way instead?" Perhaps there is a one-off scenario and you needed someone coded a specific way for a certain reason. Documenting the reason for taking a certain approach could prevent future time being wasted by another developer trying to refactor the code to an approach you already tried that didn't work. Another example of this would be having your code not check for something 
