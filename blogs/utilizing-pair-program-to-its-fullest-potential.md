## Utilizing pair programming to it's fullest potential
### Introduction
This blog post is going to assume that you are already convinced that [pair programming is worth using](https://amcneil36.github.io/blogs/pair-programming-is-it-worth-it). But when should we use pair programming and how can we get the most out of it? We'll talk about how to maximize the benefit of pair programming.

### Which work should be pair programmed?
Some people like the idea of pair programming all work while some don't. I don't like the idea of pair programming all work. There are a couple factors that influence the return on investment of pair programming that I recommend taking into account to determine if pairing for a particular task is worth it. 

The tougher the task is, the more it makes sense to pair program.<sup>1</sup>. Adding a second brain to a difficult task might go a long way to speed up a difficult task. But adding a second brain to an easy, trivial task doesn't speed up the task as much. So if you take a look at the toughest work that needs to be done for your project, those items are good pair programming candidates. 

The newer a person is to a team, the more it makes sense for that person to pair program. This might have been able to have been inferred from the above point but I wanted to seperate this one out into it's own point to talk about one topic in particular: onboarding. A study was done with 30 teams on the effect of pair programming when onboarding new hires versus not pair programming when onboarding new hires<sup>4</sup>. With pairing, the assimilation time for new hires was 12 workdays whereas without pairing, the assimilation time for new hires was 27 workdays. When pairing, mentors had to spend 26% of their time mentoring whereas without pairing, mentors had to spend 36% of their time mentoring.

The more programmers there are on a team, the more it makes sense to pair program<sup>1</sup>. So the more programmers there are on a team, the more communication overhead there is, the more people will step on each other's toes, the more merge conflicts there will be, and the more difficulty there is in planning in such a way that team members are able to handle dependencies between tasks. There is a non-linear relationship between the number of branches and the number of integration issues<sup>3</sup> so being able to cut the number of branches in half with pair programming means it will make integration more than twice as easier.

The more dependencies there are between tasks for the sprint, the more it makes sense to pair program. Imagine a scenario where there is a team of 4 programmers. Let's say that there are 4 high value tasks and 2 low value tasks that need to be worked on. Furthermore, let's say that 2 of the high value tasks are dependent on the other high value tasks. With 4 programmers and no pair programming, you would start out with 2 high value tasks in progress and 2 low priority tasks in progress. This is inconvenient because this means that we are going to be finishing some of the low value tasks before some of the high value tasks. If we are pair programming, we can have 2 high value tasks in progress and when those finish, we can go on to the next 2 high value tasks. This way, the low value tasks are worked on last.

The more fun your team members find pair programming, the more they should pair program. In fact, if they try pair programming and don't like it, they shouldn't pair program at all.

### Rotating Pairs
Many teams implementing pair programming have reported greater success from rotating out who is pair programming with who. I have heard of people rotating anywhere from once every hour up until once every 3 or 4 days. There was a study done in an attempt to determine the most efficient way to do pair rotation<sup>5</sup>. The team involved in the study reported the highest velocity when pairs were rotated every 90 minutes with the more experienced people being rotated around. Beginners would sign up for tasks and be the owners. This means that the beginner would keep working on the task until is is complete where as their partner (the more experienced person who is not owning any tasks) is the one who is rotating out every 90 minutes.  The author went on to explain in a separate post that he considers 90 minute intervals to only be optimal for teams that are experienced in pair programming. He likes the idea of teams new to pair programming to rotate pairs every 3 or 4 days and work their way down to 90 minute rotations.

### Pair Programming desk setup
Have the two people pairing sit side by side. Having one person sit behind the other person will cause that person to breathe down the other person's neck which we don't want.

Place the monitor equally between the two people. Both people should have their own keyboard and mouse that are plugged into the monitor so that either person can take over without having to slide the mouse or keyboard. If you cannot set up your desk with two keyboards and two mice, have the keyboard and mouse directly in front of the driver in the way that is the most comfortable for the driver.

## Sources
1. Williams, Laurie and Kessler, Robert. Pair Programming Illuminated. Addison-Wesley, 2002.  
2. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.  
3. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.  
4. Shukla, A. (2002). "Pair Programming and the Factors Affecting Brook's Law," Master's Thesis, North Carolina State University
5. Belshee, Arlo. Promiscuous Pairing and Beginner’s Mind: Embrace Inexperience. Silver Platter Software, 2005.
