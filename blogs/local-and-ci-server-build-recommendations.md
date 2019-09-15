## Local and Ci-Server Build Recommendations
### The build must be able to be started from the command line
We want our build to be able to be started from the command line.<sup>a</sup> This will help make the build able to be ran from any developer's machine as well as on a ci-server.

### 10 minute build on commit
We want our build that is ran on commit to be able to be ran in 10 or fewer minutes.<sup>a,b</sup> Having a build that takes longer than this makes it take a while to fix errors in the build since there has to be such a long wait to make sure that the build completes. People will also be less inclined to merge to trunk as frequently if the build has a long wait time. 

### What to run on commit



## Sources
a. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.
b. Beck, Kent and Andres, Cynthia. Extreme Programming Explained. Addison-Wesley, 2004.
