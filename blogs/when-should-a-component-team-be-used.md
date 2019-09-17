## When should a component team be used?
We previously talked about [why we should favor using feature teams over component teams](https://amcneil36.github.io/blogs/feature-teams-vs-component-teams-which-one-is-better). However, we didn't establish that component teams should be completely avoided. We just established that we should use feature teams for most scenarios. So when should component teams be used? Let's look at an example.

Suppose your team needs code changes made in some component that it has never used before. There are two different setups we could have:
1. feature team setup where our team will make code changes in the component
2. component team setup where a component team will make code changes in the component

In the feature team setup, there is an overhead of having to learn about the component before making code changes to it. This is why the changes in the component would most likely take more man hours in the feature team setup. However, the component team setup often results in queues before the request can be serviced which generally leads to more elapsed time from the time of the request to the time of the request being completed. 

For components that are easy to work with and learn, it might be, for example, 1.3 times more man hours for your team to do the code change in the component than for the component team to make the code change. We would generally be fine with spending 1.3 times more man hours since it prevents the additional overhead of cross-team dependencies and work sitting in queue. What about for difficult to work with components?

When components are difficult to work with, the initial overhead of having to learn about a component before making code changes in it starts to become too great for it to be worth it to use the feature team setup. For components that are difficult to work with, it could easily take 20 times more man hours for our team to do the code change than for the component team to do the code change.

The more difficult the component is to work with, the larger the overhead of learning the component will be and the more time it will take your team to make the code change relative to a component team to make the change. For working with a simple component, it might only take 1.3 times longer for our team to make a code change in a component than if a component team made the code change. This will usually be worth it. We might accept this if the alternative is our work sitting in queue. But for a difficult component, it could take 20 times longer for our team to make a code change in a component as opposed to a component team making the code change. At this point, we are probably OK having a component team service our request.
