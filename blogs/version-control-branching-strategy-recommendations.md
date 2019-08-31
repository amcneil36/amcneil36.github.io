# Version Control Branching Strategy Recommendations

There are many different ways that teams have utilized branching in their version control system to develop and deploy software. We will discuss some key points and techniques for branching.

## Branching characteristics that correlated with higher delivery performance 
A DevOps study was done on thousands of organizations from 2015-2017, analyzing characteristics that were correlated with higher delivery performance.<sup>a</sup> This study defined delivery performance as a combination of lead time, release frequency, and mean time to recovery. Teams with the highest delivery performance had the following characteristics:
* fewer than three active branches at a time
* branches with short lifespans (less than a day)
* no code freezes
* no stabilization periods

As we brainstorm how we want to do our branching, we should keep these points in mind.

## Maintain One Version of Code
It can be a support nightmare when multiple versions of code are out in production.<sup>b</sup> In order to investigate, you need to revert back to whichever version of code that the client is on. When you fix the code, you need to merge those changes into the other vesrions of code. Similarly, having multiple versions of code in development will have the same issues. You do a bug fix in production and you need to merge those changes into all of the development versions. Having multiple versions of code in development also makes it tougher understand the code base and leads to integration issues later down the road when it is time to merge everything together. So whenever possible, maintain one version of code in production and one version of code in development.

## A branching strategy for high delivery performance and maintaining one version of code
### When doing development
Whenever starting a new task, branch off trunk. Whenever you have produced a small amount of working code, you can get your code reviewed and then merge into trunk. This may not be enough code to complete the task or the feature. As long as you have produced a small amount of working software, it is OK to merge.

Why would we consider merging our code if we haven't actually completed a task or feature yet. There is a non-linear relationship between lifespan of a branch and difficulty of integration.<sup>a</sup> This is where the idea of Continuous Integration came from. If we merge into trunk more frequently, we will have less integration issues. The recommendation from Kent Beck, founder of Extreme Programming, is to merge into trunk at least once per day but preferably multiple times per day.<sup>c</sup>

## Sources
a. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.  
b. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.  
c. Beck, Kent and Andres, Cynthia. Extreme Programming Explained. Addison-Wesley, 2004.
