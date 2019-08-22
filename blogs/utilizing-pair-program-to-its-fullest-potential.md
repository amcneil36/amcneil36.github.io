## Utilizing pair programming to it's fullest potential
### Introduction
This blog post is going to assume that you are already convinced that [pair programming is worth using](https://amcneil36.github.io/blogs/pair-programming-is-it-worth-it). But when should we use pair programming and how can we get the most out of it? We'll talk about how to maximize the benefit of pair programming.

### Which work should be pair programmed?
There are a couple factors that influence the return on investment of pair programming. 

The tougher the task is, the more return on investment there is from pair programming.<sup>1</sup>. Adding a second brain to difficult task might go a long way to speed up a difficult task. But adding a second brain to an easy, trivial task doesn't speed up the task as much.

The more programmers there are on a team, the more it makes sense to pair program<sup>1</sup>. So the more programmers there are on a team, the more communication overhead there is, the more people will step on each other's toes, the more merge conflicts there will be, and the more difficulty there is in planning in such a way that team members are able to handle dependencies between tasks. There is a non-linear relationship between the number of branches and the number of integration issues<sup>3</sup> so being able to cut the number of branches in half with pair programming means it will make integration more than twice as easier.

The more dependencies there are between tasks for the sprint, the more it makes sense to pair program. Imagine a scenario where there is a team of 4 programmers. Let's say that there are 4 high value tasks and 2 low value tasks that need to be worked on. Furthermore, let's say that 2 of the high value tasks are dependent on the other high value tasks. With 4 programmers and no pair programming, you would start out with 2 high value tasks in progress and 2 low priority tasks in progress. This is inconvenient because this means that we are going to be finishing some of the low value tasks before some of the high value tasks. If we are pair programming, we can have 2 high value tasks in progress and when those finish, we can go on to the next 2 high value tasks. This way, the low value tasks are worked on last.

Onboarding new hires.

## Sources
1. Williams, Laurie and Kessler, Robert. Pair Programming Illuminated. Addison-Wesley, 2002.  
2. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.  
3. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.
