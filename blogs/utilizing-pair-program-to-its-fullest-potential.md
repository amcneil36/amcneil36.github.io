## Utilizing pair programming to it's fullest potential
### Introduction
This blog post is going to assume that you are already convinced that [pair programming is worth using](https://amcneil36.github.io/blogs/pair-programming-is-it-worth-it). But when should we use pair programming and how can we get the most out of it? We'll talk about how to maximize the benefit of pair programming.

### Pair programming styles
#### Driver/Navigator
The most common way to do pair programming is the driver/navigator setup where the driver is the person who is typing and thinking about what to do whereas the navigator is the person looking on and thinking about the big picture. This is what you will want to do most of the time excepth when one person is extremely new and the other person is an expert which we will talk about below.

#### Backseat Navigator
The less experienced the driver is, the more help the driver is going to need. With extremely new people, it can make sense to use the backseat navigator approach when the new person is driving and paired with somemone more experienced. In the backseat navigator setup, the navigator has more of an emphasis on instructing the driver on what to do.

#### Tour Guide
In the tour guide setup, the person driving has more of an emphasis on informing the navigator about what is going on. Whenever a beginner and an exerpeinced person are paired together and the experienced person is driving, it makes sense to do the tour guide approach so that the beginner can understand what is going on. The tour guide needs to go slow or else the beginner looking on will not understand anything and won't learn. The tour guide approach can also be useful when catching someone up after they just came back from an absense.

#### Ping Pong Pairing
In this setup, one person starts by writing a failing unit test. The people will then swap roles and the next person will write the code necessary to pass the unit test in addition to any refactoring that is needed. This person will then write a failing unit test and then swap roles to let the other person write the code to make the unit test pass. The process repeats. I recommend using this approach.

### Which work should be pair programmed?
Founder of Extreme Programming, Kent Beck, likes the idea of all work being pair programmed<sup>6</sup>. Mike Cohn, author of *Succeeding with Agile<sup>7</sup>,* likes the idea of pair programming where appropriate but not pair programming all of the tasks. I will side with Mike on this one. There are a couple factors that influence the return on investment of pair programming that I recommend taking into account to determine if pairing for a particular task is worth it. 

#### Difficulty of the task
The tougher the task is, the more it makes sense to pair program.<sup>1</sup>. Adding a second brain to a difficult task might go a long way to speed up a difficult task. But adding a second brain to an easy, trivial task doesn't speed up the task as much. So if you take a look at the toughest work that needs to be done for your project, those items are good pair programming candidates. 

#### Experience of the team member
The newer a person is to a team, the more it makes sense for that person to pair program. This might have been able to have been inferred from the above point but I wanted to seperate this one out into it's own point to talk about one topic in particular: onboarding. A study was done with 30 teams on the effect of pair programming when onboarding new hires versus not pair programming when onboarding new hires<sup>4</sup>. With pairing, the assimilation time for new hires was 12 workdays whereas without pairing, the assimilation time for new hires was 27 workdays. When pairing, mentors had to spend 26% of their time mentoring whereas without pairing, mentors had to spend 36% of their time mentoring.

#### Team size
The more programmers there are on a team, the more it makes sense to pair program<sup>1</sup>. So the more programmers there are on a team, the more communication overhead there is, the more people will step on each other's toes, the more merge conflicts there will be, and the more difficulty there is in planning in such a way that team members are able to handle dependencies between tasks. There is a non-linear relationship between the number of branches and the number of integration issues<sup>3</sup> so being able to cut the number of branches in half with pair programming means it will make integration more than twice as easier.

#### Dependencies between tasks
The more dependencies there are between tasks for the sprint, the more it makes sense to pair program. Imagine a scenario where there is a team of 4 programmers. Let's say that there are 4 high value tasks and 2 low value tasks that need to be worked on. Furthermore, let's say that 2 of the high value tasks are dependent on the other high value tasks. With 4 programmers and no pair programming, you would start out with 2 high value tasks in progress and 2 low priority tasks in progress. This is inconvenient because this means that we are going to be finishing some of the low value tasks before some of the high value tasks. If we are pair programming, we can have 2 high value tasks in progress and when those finish, we can go on to the next 2 high value tasks. This way, the low value tasks are worked on last.

#### Passion
The more fun your team members find pair programming, the more they should pair program. In fact, if they try pair programming and don't like it, they shouldn't pair program at all. Pair programming when you aren't interested in doing it is going to produce worse results than not pairing at all<sup>1</sup>.

### Rotating Pairs
Many teams implementing pair programming have reported greater success from rotating out who is pair programming with who. I have heard of people rotating anywhere from once every hour up until once every 3 or 4 days. There was a study done in an attempt to determine the most efficient way to do pair rotation<sup>5</sup>. The team involved in the study reported the highest velocity when pairs were rotated every 90 minutes with the more experienced people being rotated around. Beginners would sign up for tasks and be the owners. This means that the beginner would keep working on the task until is is complete where as their partner (the more experienced person who is not owning any tasks) is the one who is rotating out every 90 minutes.  The author went on to explain in a separate post that he considers 90 minute intervals to only be optimal for teams that are experienced in pair programming. He likes the idea of teams new to pair programming to rotate pairs every 3 or 4 days and work their way down to 90 minute rotations. I want to bring up that some teams have reported rotating every 90 minutes to be too frequent for them so I don't think 90 minute rotations would work for every team. I think you should try working towards 90 minute rotations but if you feel it isn't working for you after you have tried it, then revert back to a less frequent rotation and stick with that.

### Pair Programming desk setup
Have the two people pairing sit side by side. Having one person sit behind the other person will cause that person to breathe down the other person's neck which we don't want.

Place the monitor equally between the two people. Both people should have their own keyboard and mouse that are plugged into the monitor so that either person can take over without having to slide the mouse or keyboard. If you cannot set up your desk with two keyboards and two mice, have the keyboard and mouse directly in front of the driver in the way that is the most comfortable for the driver.

### Pairing with various experience levels
#### Novice/Expert pairing
I would consider this to be by far the best pair because of the knowledge transfer from the expert to the novice. This will build up new teammates about as fast as any strategy and has the most return on investment. Have the novice be the driver for around 2/3 of the time for this. Try something along the lines of having the novice be the driver for 20 minutes or so and then let the expert be the driver for around 10 minutes and then repeat.

####  Novice/Novice pairing
The novice/novice pairing is considered to be the least effective type of pairing<sup>1</sup>. What commonly happens is that you have two people get stuck on something for 3 hours, only to ask an expert for help who figures it out in 2 minutes. The novice driver might make a mistake or do something that is sub-optimal and the navigator might not catch it due to being a novice. The continuous review aspect of pair programming is less noticeable in the novice/novice pairing as a result. There also isn't much of any knowledge transfer. Placing at least one expert in a pair allows for knowledge transfer and good feedback to help make you less likely to get stuck. Consider swapping driver/navigator roles every 10 minutes or so in this setup.

#### Expert/Expert pairing
I would consider this type of pairing to be more effective than the novice/novice pairing but less effective than the novice/expert pairing. Having at least one expert in the pair means that we are less likely to get stuck and are more likely to continuously progress. If the driver makes a careless mistake, the navigator is likely to catch it since the navigator is an expert. Consider swapping driver/navigator roles every 10 minutes or so in this setup.

### Pair Programming ettiquite
When being the navigator, due not immediately inform the driver of a typo that they have made<sup>1</sup>. The driver might be aware of the typo that they have made and just be in the progress of finishing off what they are typing before going back to correct the typo. Give the driver a few seconds just to be sure that the driver is not aware of the typo before informing the driver of the typo. It is also not necessary to state the obvious or to try to micro manage every line of code that the driver needs to type in the event that the driver partially knows what they are doing. If the driver has no idea what they are doing, then it makes sense to kind of "back seat drive" where you give more instructions.

If the driver is getting tired or frustrated, this is generally a good time to swap roles with the navigator<sup>1</sup>.

As a driver, you should be talking out loud as you think so that the navigator is able to understand your thought process<sup>1</sup>.

### Should there still be formal code reviews on pair programmed tasks?
Most teams have their process set up where all code needs to be reviewed before being merged to trunk. What about with pair programming? For tasks that have been pair programmed, should this code still get reviewed by others prior to merging to trunk? Or should a task that is pair programmed be merged to trunk without being reviewed by others?

#### An argument for no formal code reviews on pair programmed work
Kent Beck recommends merging pair programmed work to trunk without going through a formal review process. The founder of extreme programming says "...ExtremeProgramming projects do not require explicit reviews. Drop them from your methodology." That excerpt can be found [here](http://wiki.c2.com/?ExtremeProgrammingCodeReviews). Kent Beck recommends team members merging to trunk at least once per day but preferably multiple times per day. This is done to reduce integration issues. This is a lot easier to do when no formal code reviews are needed.

It is natural for some tasks within a user story to depend on each other<sup>8</sup>. If you finish coding up a task in the user story and upload your code for a formal review, your next task within the user story might be something that depends on the code that is in review. As you are waiting for your code to get reviewed, you have a delimma. You could branch off of what is in review or you could pick a task in another user story. Branching off something in review is problematic because you are making a branch that is becoming more divergent from master. Furthermore, any code change made on the branch in review has to be merged into your new child branch. Picking a task from another user story is also problematic because you are switching to another user story without finishing your current user story so you are slowing down how frequently you produce a potentially shippable product increment that you can demo for feedback. Ending the sprint with a few fully completed user stories is a lot better than ending the sprint with many partially completed user stories since it is the fully completed user stories that deliver feedback and are demo'd<sup>7</sup>.

#### An argument for formal code reviews on pair programmed work
Some industries are more regulated than others which might make it harder to get away with merging to trunk with no formal code review. Furthermore, some of the tasks on a project are more risky than others where mistakes can cost a lot of money. On top of that, some team members may feel uncomfortable with the idea of code getting merged to trunk that they did not get a chance to look at.

#### So what is the answer?
I don't think there is a one size fits all answer to this question. The more regulated your industry is, the more it makes sense to do formal reviews. Secondly, the greater the risk of the task, the more it makes sense to do formal reviews. Thirdly, the less experience that the members of the pair has, the more it makes sense to do formal reviews. And fourthly, the more experience the people who were not part of the pair are, the more it makes sense to do formal reviews. I think you would want to take a look at these factors and make the decision about whether or not to do a formal review on a particular task based on that. I think it would be fair to do a mix of formal reviews and no formal reviews for tasks based on weighing these four factors against your task.

Jez Humble, author of *Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation*<sup>9</sup> proposes a common ground alternative. You can merge to trunk without having gone throurgh formal code review and let the other team members optionally review post commit. I would imagine that as long as commiting to trunk doesn't release to the client, this would be a low-risk compromise. 

## Sources
1. Williams, Laurie and Kessler, Robert. Pair Programming Illuminated. Addison-Wesley, 2002.  
2. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.  
3. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.  
4. Shukla, A. (2002). "Pair Programming and the Factors Affecting Brook's Law," Master's Thesis, North Carolina State University
5. Belshee, Arlo. Promiscuous Pairing and Beginner’s Mind: Embrace Inexperience. Silver Platter Software, 2005.
6. Beck, Kent and Andres, Cynthia. Extreme Programming Explained. Addison-Wesley, 2004.
7. Cohn, Mike. Succeeding With Agile: Software Development Using Scrum. Addison-Wesley, 2013.
8. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.
9. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.
