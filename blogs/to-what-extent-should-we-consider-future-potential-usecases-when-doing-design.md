## To what extent should we consider future potential usecases when doing design?

Suppose there are many user stories for a product that are planned to be worked on shortly. You are about to start doing design for one of the user stories now. Should you only think about your user story and do design that is optimal for only your user story? Should you try to have other planned user stories in mind as you do the design for your user story? Should you take it one step further and try to have your design account for future potential stories or requirements that we don't have right now? In most cases, this is a question of how generic and re-usable we should design our code to be. In other cases, it is a question of which scenarios our design should account for. There are two different approaches that come to mind when thinking about this.

### Design for now
In this scenario, we only have our design account for current usecases. This results in writing just enough code to meet the current requirements. We dafo not spend any time trying to account for potential future usecases that we don't currently have. We don't attempt to design our code to be any more generic than needed to solve the current problem we have. If there is a scenario that we are unsure as to whether or not we will need to account for in the future, we don't account for it right now. If we find out later that we do need to account for that scenario, then we will add the code to account for that scenario then.

### Design for the future
In this scenario, we try to brainstorm future potential usecases and make our design generic enough to account for them. This would result in doing more code than what the current requirements would need. We spend extra time doing design early on in hopes that it will save time in the future.

### Which option should we go with?
It could be said that the amount we should account for potential future usecases depends on the likelihood of the future usecases being needed later, how much additional work it would take right now to account for these future usecases, and how much time would be saved overall in the future by accounting for these future usecases now. However, people tend to vastly over-estimate how likely a potential future usecase will eventually be needed and vastly underestimate how much extra work it would take to account for the future usecase right now. We should design for now instead.<sup>1,2,3</sup> 

The most noticeable downside you will observe when trying to design for the future is that it is incredibly difficult. You could be sitting there all day trying to field questions like "well what if tomorrow ______ happens and then ______ will no longer work?" "What if in the future, we need to account for ______?" You could easily turn a two day task into a complicated three month task trying to account for every scenario that could possibly arise in the future. Most of the time, you will have no idea what might change in the future, even if you think you do.<sup>2</sup> So any additional time spent trying to account for future usecases that you currently do not have will rarely go on to pay for itself. This is where we say Ya Ain't Gonna Need it (YAGNI).

When determining how generic we want our code to be, we only want it to be generic enough to work for our current problem.<sup>1</sup> If we later encounter a similar problem in the future, we should try to modify our code to be more generic, but only generic enough to solve the two problems that we have.<sup>1</sup> Repeat this process, making code slightly more generic as more problems arise.

So we just want our design to account for current usecases that we have. Let's get back to the user story design question now. We now know that when doing design for our user story, we shouldn't worry about our design needing to account for future potential user stories that we haven't came up with yet. Should our design account for other planned user stories? To some degree, having some knowledge of other planned user stories could be helpful to make sure that the design we are coming up with is going in the right direction and won't be as likely to get scrapped. However, the further out a user story is from being started, the more time there is for things to potentially change. As a result, the further a user story is from being started, the less certain we are of how this user story will look and the less we would want to take this user story into account for trying to make sure our current design will work moving forward. Let's give an example of coding for now to show why we should code for now. 

Suppose that for a task in our next user story, we will need to be able to store a person's first name and last name. Suppose for the user story after that, we predict we will need age. After that, we predict we will need height. After that, we think we will need middle name. We take this information and decide that maybe we should have a Person class that stores this information. We think to ourself that maybe in the future, the class could eventually go on to look like this:
```
public class Person {
 public String firstName;
 public String lastName;
 public String middleName;
 public int age;
 public int height;
}
```
Instead of adding in all 5 fields to the Person class now, we do just enough code to satisfy the requirements for our current user story. This results in us only adding firstName and lastName.
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
After that we are about to start our next user story. We had previously thought that we needed height but based on how things have gone along, we determined that we need weight instead.
```
public class Person {
 public String firstName;
 public String lastName;
 public int age;
 public int weight;
}
```
After that is done, we are about to start the user story that requires middle name but it turns out it is time for the sprint demo. The customer sees how everything looks in the demo and decides they no longer want middle name to display in the application so we drop that user story. Notice how our Person class ended up being a little different than initially predicted.

There were a couple of benefits that we have learned about as we went through this exercise of writing the minimum code needed to satisfy the requirements of our current user story. One benefit of writing only enough code for our current user story is that less code has to get tossed when requirements change. If we wrote all of the fields for the Person class in our first user story, some of our code would have needed to be backspaced upon finding out later we didn't need some of it.

Another benefit of only writing the minimum amount of code required for our user story is that our code is less complex for a larger percentage of the project timeline.<sup>3</sup> Having code that is less complex will make it easier to make code changes. So if we only add complexity when needed, we will be able to work faster than if we were to add a lot of complexity at the start. Also, adding a lot of code that isn't getting called from anywhere due to not being needed for your current user story makes it confusing as to what the purpose of the code is for. If we had coded up the entire Person class on the first user story that only needed first name and last name, it would have been unclear to others what the purpose of the middleName, height, and age fields was. Only coding what we need helps us Keep It Simple Stupid (KISS).

### Summary
We should be coding for now. It would be OK to briefly think about other planned user stories to make sure your design is going in the right direction but even those user stories can be likely to change. When working on a user story, only write the minimum amount of code to satisfy the requirements for your current user story. Do not attempt to account for any additional scenarios that your user story does not require. When in doubt, leave it out. 

## Sources
1. Shore, James and Warden, Shane. The Art of Agile Development. O'Reilly Media, 2008.  
2. Beck, Kent. Extreme Programming Explained, Embrace Change. Addison-Wesley, 2012.  
3. Shalloway, Allan and Beaver, Guy. Lean-Agile Software Development: Achieving Enterprise Agility. Addison-Wesley, 2010.  
