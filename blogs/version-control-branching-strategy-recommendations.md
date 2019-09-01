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

Some clients might have slightly different use-cases which might make it feel tempting to have multiple versions of code in order to support different use-cases. We should instead aim to have a configuration option where the client can adjust a configuration setting to make the software to behave the way they want.<sup>c</sup> This way, we can have the same version of code support both client workflows. We can have the configuration default to the more common workflow or we can prompt the user to choose a configuration setting on their first time using it.

## A branching strategy for high delivery performance and maintaining one version of code
### When doing development
Whenever starting a new task, branch off trunk. Whenever you have produced a small amount of working code, you can get your code reviewed and then merge into trunk. This may not be enough code to complete the task or the feature. As long as you have produced a small amount of working software, it is OK to merge.

Why would we consider merging our code if we haven't actually completed a task or feature yet. There is a non-linear relationship between lifespan of a branch and difficulty of integration.<sup>a</sup> This is where the idea of Continuous Integration came from. If we merge into trunk more frequently, we will have less integration issues. The recommendation from Kent Beck, founder of Extreme Programming, is to merge into trunk at least once per day but preferably multiple times per day.<sup>c</sup> There is also a non-linear relationship between number of branches and integration issues.<sup>a</sup> We should use pair programming to reduce the numer of active branches.<sup>b,c</sup>

Okay cool so branch off trunk, do some coding, merge into trunk. Repeat. Nice and easy. The fact that everyone is branching off the same branch and merging into the same branch means all of our code is getting integrated together more frequently which will reduce integration problems. Okay cool so we understand branching for development. What about for releasing? We will look at two branching strategies.
### Releasing
#### Branch for release
In this setup, you branch off of trunk to create a release branch whenever you are ready to release.<sup>b</sup> You can then go through the release process with the release branch. This prevents the practice of the code freeze. Because we are branching off of trunk to release, other developers can continue to develop and merge code in to trunk without affecting the release.

What if there is a defect in testing the release branch? You should branch off of trunk, try to reproduce the defect, fix the defect, merge your branch to trunk, and then cherry pick the commit from trunk to the release branch. The cherry pick is used so that you only inherit the defect fix and don't potentially inherit code that is for a new feature that was not intended to be released. Why are we doing the fix on trunk and cherry picking to the release branch instead of doing the fix on the release branch and cherry picking to trunk? The simple answer to this question is that doing the fix on trunk and cherry picking to trunk handles the scenario of forgetting to cherry pick a little better. Let's think about this. An exploratory test just failed against our release branch that caused this defect to be seen. We might do the code fix on trunk and forget to cherry pick to our release branch. The test that failed will be re-ran again and it will fail, notifying us that we didn't get the code fix. We then cherry pick the commit to our branch and the test passes. If we do the fix on the release branch and forget to cherry pick the commit to trunk, we might be in trouble. The test that just failed will be ran and will pass. However, when we go do our next exploratory test in the future, it might not test this same workflow. That is because exploratory tests by definition are not scenarios. Wouldn't our automated tests catch this though? If the commit that fixed the bug also contains the corresponding automated test that proves that the defect was fixed and we forgot to cherry pick that commit, then no we will not catch that with our automated test.

What should we do when fixing a defect in production? Do the same thing. Fix the issue in trunk and then cherry pick to the release branch. There is one exception to this. If the defect is critical, then we should fix it on the release branch first and then cherry pick that fix to trunk. This is because the added amount of time it would take to get the defect fixed on the release branch as a result of having to fix the defect on trunk first is not worth it for a critical defect. Since the defect is critical, we would want to get it fixed as soon as possible on the release branch and then release. After that, we can worry about fixing it on trunk.  

The last thing I want to point out is if you can't reproduce the defect on trunk, then just fix the defect on the release branch instead.
#### Release from trunk
One of the downsides of the branch for release is that everytime there is a defect in production, you have to get the defect fixed in two places: trunk and the release branch. One way around this is to do the release from trunk approach. 

In the release from trunk approach, we do not create a branch when it is time to release. We just release from the trunk branch. If something goes wrong in production, we would do the fix in trunk and then release from trunk. This makes it such that when there is a defect in production, we no longer have to do the fix in two branches. There may be a possible concern from this. We are merging all of our development code to trunk, even when the feature we are working on is not complete. Wouldn't that mean that if there were a defect in production and we release from trunk after fixing it, we would then ship out a partially completed feature that is not ready to be used yet? The answer to this question: feature toggles.

Feature toggles allow us to turn on or off features without having to make a code change.<sup>b</sup> This is most commonly implemented by using a configuration file. The entry point of an application will then read from the configuration file to determine whether to have the feature turned on or off. You can have the feature turned off by default and then you can turn the feature on in your development and test environment while leaving it turned off in production. If the new feature is a button, having the feature turned off just means hiding the button. The code for the button is there but the button is hidden from the user so the functionality that would occur upon clicking on the button is not able to be called into in production. Another common way to implement feature toggles is to have the entry point of your application read a flag from the database to determine whether to turn the feature on or off. In development and test environments, you can type code to insert the flag into the database to turn the feature on. The code can be set up to where if it doesn't find the flag in the database, it will just leave the feature turned off. Once the new feature is completed, the feature flag should be removed.  

So in this scenario, we merge every code change to trunk and just make sure that any features that are not fully completed are somehow inaccessible to the user in production by means of a feature toggle. This strategy works very well when we are adding functionality since we can just turn the new feature off. What about when we are modifying existing functionality? Here is where branch by abstraction comes in.

Branch by abstraction is a technique for modifying existing functionality while still keeping trunk releaseable.<sup>b</sup> The name of this technique is misleading as it is not creating a new branch. It entails
1. Create an abstraction around the area of code that needs to be changed. This might be an interface depending on the programming language you are using 
2. Refactor the existing code to call into this abstraction  
3. Create a new implementation of the abstraction. This might be an implementation of the interface  
4. Use a feature flag to determine whether or not to call into the new implementation or the old implementation. The old implementation should be called into by default  
5. Finish off working on and testing the new implementation   
6. Update the code to call into the new implementation by default and remove the feature flag  
7. Optionally remove the old implementation  
8. Optionally remove the abstraction

#### Which release approach should we choose?
This is primmarily a battle of whether fixing defects on two branches or implementing feature toggles is more time consuming. Feature toggles would probably take some time to set up initially since you have to figure out how to implement the config file or database read for the first feature toggle you make. But after that, all future feature flags will be implemented the exact same way with only the name of the feature flag changing. So in the long run, the feature toggle route will most likely take less time than fixing defects on two branches. On the other hand, what if we historically have very few defects in production? If we are rarely having to fix defects on two branches as a result of rarely having defects at all, then that might be faster than implementing feature toggles, even in the long run.

Another thing to keep in mind is-what if two different teams are working on the same component for two different releases? A component could be shared by many different teams within an organization. This can really complicate things if you are merging code into trunk that is not releaseable. Feature toggles would allow all teams to merge all code changes into trunk while keeping trunk releaseable. You might say "we will own this component and not let anyone else make code changes to it" but that is a silo'd approach that is not recommended in agile IT organizations.

So with the above information in mind, I prefer the release from trunk approach. It will generally take less time and have less difficulty of coordination.

## Conclusion
To summarize the key points:
* maintain one version of code in development and production
* all branches should be created off of trunk and merged back into trunk within 1 day of creation
* it is OK to merge code into trunk that does not complete a task or feature
* use feature flags to hide partially completed features in production while making them visible in development and testing
* use branch by abstraction to modify existing functionality
* use pair programming to reduce the number of active branches

## Sources
a. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.  
b. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.  
c. Beck, Kent and Andres, Cynthia. Extreme Programming Explained. Addison-Wesley, 2004.
