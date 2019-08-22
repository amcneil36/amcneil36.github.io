## Utilizing pair programming to it's fullest potential
### Introduction
This blog post is going to assume that you are already convinced that [pair programming is worth using](https://amcneil36.github.io/blogs/pair-programming-is-it-worth-it). But when should we use pair programming and how can we get the most out of it? We'll talk about how to maximize the benefit of pair programming.

### Should every task be pair programmed? If not, how do we decide which tasks should be pair programmed?
There are a couple factors that influence the return on investment of pair programming. 

The tougher the task is, the more return on investment there is from pair programming.<sup>1</sup>. Adding a second brain to difficult task might go a long way to speed up a difficult task. But adding a second brain to an easy, trivial task doesn't speed up the task as much.

The more programmers there are on a team, the more it makes sense to pair program<sup>1</sup>. It is natural for some tasks within a user story to depend on each other<sup>2</sup>. So the more programmers there are on a team, the more communication overhead there is, the more people will step on each other's toes, the more merge conflicts there will be, and the more difficulty there is in planning in such a way that team members are able to handle dependencies between tasks. Imagine a scenario where there is a team of 8 programmers. Let's say that there are 8 high priority tasks and 8 low priority tasks that need to be worked on. Furthermore, let's say that 4 of the high priority tasks are dependent on other high priority tasks. 

## Sources
1. Williams, Laurie and Kessler, Robert. Pair Programming Illuminated. Addison-Wesley, 2002.
2. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.
