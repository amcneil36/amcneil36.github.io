## When should a component team be used?
We previously talked about [why we should favor using feature teams over component teams](https://amcneil36.github.io/blogs/feature-teams-vs-component-teams-which-one-is-better). However, we didn't establish that component teams should be completely avoided. We just established that we should use feature teams for most scenarios. So when should component teams be used? Let's look at an example.

Suppose your team needs code changes made in some component that it has never used before. There are two different setups we could have:
1. feature team setup where our team will make code changes in the component
2. component team setup where a component team will make code changes in the component

In the feature team setup, there is an overhead of having to learn about the component before making code changes to it. This is why the changes in the component would most likely take more man hours in the feature team setup. However, the component team setup often results in queues before the request can be serviced which generally leads to more elapsed time from the time of the request to the time of the request being completed. 

For components that are easy to work with and learn, it might be, for example, 1.3 times more man hours for your team to do the code change in the component than for the component team to make the code change. We would generally be fine with spending 1.3 times more man hours since it prevents the additional overhead of cross-team dependencies and work sitting in queue. What about for difficult to work with components?

When components are difficult to work with, the initial overhead of having to learn about a component before making code changes in it starts to become too great for it to be worth it to use the feature team setup. For components that are difficult to work with, it could easily take 20 times more man hours for our team to do the code change than for the component team to do the code change. At that point, we would generally be fine having a component team do the work, even if it has to sit in queue for a little.

**So it could be said that the more dificult it is to work with a component, the more sense it makes to have a component team. The less difficult it is to work with a component, the less sense it makes to have a component team used.** Let's get into some example of components that might be difficult to work with.

### Examples of components that would be difficult to work with
#### Low Level Code in components
Components with a lot of low-level code would generally be scenarios in which it would take a lot of extra time to study the component prior to making the code change. This is becaues the low-level code requires more code in order to do less work.

#### Security
Components that deal with security would be components that would be hard for any developer in an organization to jump in and work with. This is because a developer would need to spend a lot of time learning about security before being able to make these code changes.

#### Legacy Code
Legacy code generally has a large overhead of learning and are very fragile to changes which makes legacy code decent candidates for a component team. 

#### Weird/Uncommon technology
When teams within an organization use similar technologies for their components, it is easier to be in a scenario in which any team could make code changes in another team's components. If a team uses a different technology, especially if this technology is not well documented or known, this would add a bit of an overhead to having other teams making the code change in the component.

#### Overly complicated data model
We should try to make our data model as simple as possible to where we don't need to make a component team that works with the data model. But if we cannot keep our data model simple, then there will be a bit of an overhead for other teams needing to make code changes to the component. So this is another scenario in which a component team may make sense.
