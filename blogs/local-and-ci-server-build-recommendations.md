## Local and Ci-Server Build Recommendations
### The build must be able to be started from the command line
We want our build to be able to be started from the command line.<sup>a</sup> This will help make the build able to be ran from any developer's machine as well as on a ci-server.

### Commit build recommendations
#### 10 minute build
We want our build that is ran on commit to be able to be ran in 10 or fewer minutes.<sup>a,b</sup> Having a build that takes longer than this makes it take a while to fix errors in the build since there has to be such a long wait to make sure that the build completes. People will also be less inclined to merge to trunk as frequently if the build has a long wait time. 

#### Static analyzers to run
* code formatters
  * allows people to develop on whichever IDE they want
* code coverage tool
  * pick a threshold to ensure that the unit tests achieve
* check for good/bad programming practices
* security vulnerability checks

#### Unit tests
All unit tests should run on the commit build. Any unit test under 1/10 of a second is a slow test.<sup>c</sup> Therefore we should have no issue with unit tests taking the build a much longer time to finish.

#### Acceptance tests
We would ideally run all acceptance tests on the commit build. However, we did say that we want a 10 minute build and in some cases, running all of the acceptance tests would take longer than 10 minutes. If our acceptance tests are causing our build to take longer than 10 minutes, then we should look into doing the following:
* run the acceptance tests in parallel
* share expensive tests between resources

## Sources
a. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.
b. Beck, Kent and Andres, Cynthia. Extreme Programming Explained. Addison-Wesley, 2004.
c. Feathers, Michael. Working Effectively with Legacy Code. Pearson Education, 2005.
