## To what extent should we consider future potential use cases when doing design?
Suppose there are many user stories for a product that are planned to be worked on shortly. You are about to start doing design for one of the user stories now. Should you only look at your user story and do design that is optimal for only your user story? Should you try to do your desgin for your user story in such a way that the other planned user stories will be easier? Should you take it one step further and try to do the design such that future potential user story that we haven't came up with yet will be easier? In most cases, this is a question of how generic and re-usable our code should be. In other cases, it is a question of which scenarios our code should account for. There are two different approaches that come to mind when thinking about this.

### Coding for now
In this scenario, we write just enough code to meet the current requirements. We do not spend any time trying to make our code work for potential future use-cases that we don't currently have. We don't attempt to make our code any more generic than needed to solve the current problem we have. If there is a scenario that we are unsure as to whether or not we will need to account for in the future, we don't account for it right now. If we find out later that we do need to account for that scenario, then we will add the code to account for that scenario then.

### Coding for the future
In this scenario, we try to brainstorm future potential use-cases and make our code generic enough to account for them. This would include doing more code than what the current requirements would need. We spend extra time doing design early on in hopes that it will save time in the future.

### Which option should we go with?
It could be said that the amount we should code for potential future use-cases depends on the liklihood of the future use case being needed later as well as how much additional work it would take right now to account for that use-case. However, people tend to vastly over-estimate how likely a potential future use case will eventually be needed and vastly understimate how much extra work it would take to account for the future use-case. For almost all scenarios, we should just code for now instead.

The most noticeable downside you will observe when trying to code for the future is that it is incredibly difficult. You could be sitting there all day trying to field questions like "well what if tomorrow ______ happens and then ______ will no longer work?" "What if in the future, we need to account for ______?" You could easily turn a two day task into a complicated three month long task trying to account for every scenario that could possibly arise in the future. Most of the time, you will have no idea what might change in the future, even if you think you do. So any additional time spent trying to account for future use-cases that you currently do not have will rarely go on to pay for itself.

So we primarily just want our design to account for current use-cases that we have. Let's get back to the user story design question now. We now know that when doing design for our user story, we don't have to worry about our design accounting for future potential user stories that we haven't came up with yet. Should our design account for other planned user stories? To some degree, having some knowledge of other planned user stories could be helpful to make sure that the design we are going with is going in the right direction and won't be as likely to get scrapped. However, the further out a user story is from being started, the more time there is for things to potentially change. As a result, the further a user story is from being started, the less certain we are of how this user story will look and the less we would want to take this user story into account for trying to make sure our current design will work moving forward. Let's give an example of this. 

Suppose that for our next user story, we will need to be able to store a person's first name and last name. Suppose for the user story after that, we predict we will need age. After that, we predict we will need height. After that, we think we will need middle name. We think to ourself that maybe in the future, we might eventually end up with something like:
```
public class Person {
 public String firstName;
 public String lastName;
 public String middleName;
 public int age;
 public int height;
}
```
As we start to work on our user story that needs first name and last name, we don't worry about adding in the middle name, age, or height fields yet. Instead of doing all of the code for the Person class now, we instead do just enough code to satisfy the requirements for our current user story.
```
public class Person {
 public String firstName;
 public String lastName;
}
```
After we complete this user story, we get to our next user story. We need age so we add that in.
```
public class Person {
 public String firstName;
 public String lastName;
 public int age;
}
```
After that we get to our next user story. We were thinking we need height but we realize we actually need weight instead.
```
public class Person {
 public String firstName;
 public String lastName;
 public int age;
 public int weight;
}
```
After that is done we are about to start the user story that requires middle name but it turns out it is time for the sprint demo. The customer sees how everything looks in the demo and decides they no longer need middle name so we drop that user story.

There were a couple of advantages that we received by only writing enough code for our current user story. The first advantage of writing only enough code for our current user story is that less code has to get tossed when requirements change. The second benefit is that our code is less complex for a larger percentage of the project. Having code that is less complex will make it easier to make code changes. So if we only add complexity when needed, we will be able to work faster than if we were to add a lot of complexity at the start. Also, adding a lot of code that isn't getting called from anywhere due to not being needed for your current user story makes it confusing as to what the purpose of the code is for. If we had coded up the entire Person class on the first user story that only needed first name and last name, it would have been unclear to others what the purpose of the middlename, height, and age fields was.
