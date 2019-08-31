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

Okay cool so branch off trunk, do some coding, merge into trunk. Repeat. Nice and easy. What about for releasing? We will look at two branching strategies.
### Releasing
#### Branch for release
In this setup, you branch off of trunk to create a release branch whenever you are ready to release.<sup>b</sup> You can then go through the release process with the release branch. Because we are branching off of trunk to release, other developers can continue to develop and merge code in to trunk without affecting the release.

What if there is a defect in testing the release branch? You should branch off of trunk, try to reproduce the defect, fix the defect, merge your branch to trunk, and then cherry pick the commit from trunk to the release branch. The cherry pick is used so that you only inherit the defect fix and don't potentially inherit code that is for a new feature that was not intended to be released. Why are we doing the fix on trunk and cherry picking to the release branch instead of doing the fix on the release branch and cherry picking to trunk? The simple answer to this question is that doing the fix on trunk and cherry picking to trunk handles the scenario of forgetting to cherry pick a little better. Let's think about this. An exploratory test just failed against our release branch that caused this defect to be seen. We might do the code fix on trunk and forget to cherry pick to our release branch. The test that failed will be re-ran again and it will fail, notifying us that we didn't get the code fix. We then cherry pick the commit to our branch and the test passes. If we do the fix on the release branch and forget to cherry pick the commit to trunk, we might be in trouble. The test that just failed will be ran and will pass. However, when we go do our next exploratory test in the future, it might not test this same workflow. That is because exploratory tests by definition are not scenarios. Wouldn't our automated tests catch this though? If the commit that fixed the bug also contains the corresponding automated test that proves that the defect was fixed and we forgot to cherry pick that commit, then no we will not catch that with our automated test.

What should we do when fixing a defect in production? Do the same thing. Fix the issue in trunk and then cherry pick to the release branch. There is one exception to this. If the defect is critical, then we should fix it on the release branch first and then cherry pick that fix to trunk. This is because the added amount of time it would take to get the defect fixed on the release branch as a result of having to fix the defect on trunk first is not worth it for a critical defect. Since the defect is critical, we would want to get it fixed as soon as possible on the release branch and then release. After that, we can worry about fixing it on trunk.  

The last thing I want to point out is if you can't reproduce the defect on trunk, then just fix the defect on the release branch instead.
### Release from trunk


## Sources
a. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.  
b. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.  
c. Beck, Kent and Andres, Cynthia. Extreme Programming Explained. Addison-Wesley, 2004.
