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
It can be a support nightmare when multiple versions of code are out in production.<sup>b</sup> In order to investigate, you need to revert back to whichever version of code that the client is on. When you fix the code, you need to merge that changes into the other vesrions of code. Similarly, having multiple versions of code in development will have make it tougher to understand the code base and lead to integration issues later down the road. So whenever possible, maintain one version of code in production and one version of code in development.

## A branching strategy for high delivery performance and maintaining one version of code
### When doing new tasks
Whenever starting a new task, branch off trunk. Whenever you have produced a small amount of working code, you can get your code reviewed and then merge into trunk. Because we want to have our branches ideally live less than a day, this may not be enough code to complete the task or the feature. As long as you have produced a small amount of working software, it is OK to merge. It may sound counter-intuitive to merge to trunk without having completed a task or feature but merging more frequently like this will reduce integration issues.

So you want to branch off 

## Sources
a. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.  
b. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.
