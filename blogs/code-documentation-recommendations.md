## Code Documentation Recommendations
We will look into two different types of documentation: public API documentation and source code documentation. For purposes of this blog post, we will consider public API documentation to be any type of documentation that is used to explain code that consumers can call into. We will consider source code documentation to be any type of documentation that can only be seen by someone who is looking at the source code.

### Public API Documentation Recommends
For this documentation, we want to explain everything that someone calling into our code might want to know. We want to make sure that we are documenting behavior and not implementation details. This means that the consumer is primarily just concerned with what the required input is and what the corresponding output is. In addition to that, there may be internal side effects that occur in the middle of the method such as sending an email. These internal side effects are still considered behavior as they have an effect on the outside world. Anything else that is happening internally but has no effect on the outside world is an implementation detail that the consumer does not need to know about.

We want to document what a method does (behavior) but not how it does it (implementation). For example, the documentation might say that our code looks up all zip codes for a given city and state. The documentation does not need to say that Hibernate is used to query the database to look this information. This is because the consumer doesn't need to know Hibernate in order to use this API.

Another example of behavior vs implementation can be thought of when turning on a computer. You need to know that pressing the power button on the computer turns the computer on. You do not need to know what happens inside of the computer after that power button is pressed.

API documentation should include documentation for things like public classes, public methods, and public fields.

When documenting the intput to our methods, there are few things we want to communicate:
* what each argument represents or stands for
* possible values that are not acceptable

For return values, we should document what is being returned and whether or not the return value could be null (or empty in the case of a collection). 

