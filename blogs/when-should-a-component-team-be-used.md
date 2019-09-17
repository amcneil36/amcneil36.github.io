## When should a component team be used?
We previously talked about [why we should favor using feature teams over component teams](https://amcneil36.github.io/blogs/feature-teams-vs-component-teams-which-one-is-better). However, we didn't establish that component teams should be completely avoided. We just established that we should we should use feature teams for most scenarios. So when should component teams be used? Let's look at an example.

Suppose your team needs code changes made in some component that it has never used before. There are two different setups we could have:
1. feature team setup where our team will make code changes in the component
2. component team setup where a component team will make code changes in the component

In the feature team setup, there is an overhead of having to learn about the component before making code changes to it. This is why the changes in the component would most likely take more man hours in the feature team setup. However, the component team setup often results in queues before the request can be serviced which results in more elapsed time from the time of the request to the time of the code change being done which could delay your release. This is antithical to the frequent and early releases to market that agile promotes. Thus, this increase in elapsed time is often more than enough to offset the amount of man hours that would be saved in the component team setup. For components that are easy to work with and learn, it will usually be worth it to spend the extra man hours having your own team make the code changes so that your team does not get blocked by having work sit in queue. However, this may not be worth it for difficult components to work with. 

If there is a 
