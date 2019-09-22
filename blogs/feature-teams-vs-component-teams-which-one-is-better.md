## Feature teams vs component teams-which one is better?
### Overview of Feature teams and Component teams

Clients will request a feature which is some sort of functionality which is useful to them. Features will almost always require code changes in multiple components.<sup>1</sup> A component can be thought of as a re-usable asset which may be used to help complete a portion of one or more features. 

There are two different types of ways in which teams might organize in order to deliver software. There are feature teams and component teams.

**Feature team:** this would be a team that takes whole customer-centric features and completes them. This team would have all of the expertise necessary to make code changes in all components needed to complete a feature. Since features almost always require code changes in front-end and back-end, this team will need to have front-end expertise and back-end expertise. Because they are cross-functional, there is a reduced need to coordinate with other teams to complete a feature.<sup>2</sup> A feature team would ideally be long-lived, completing many features together one at a time.

**Component team:** this would be a team that is assembled around particular component(s) that are used to complete part of a feature. The remainder of the feature would be completed by one or more other component teams who are also specialized in components that complete part of a feature. A component team would typically be working on only front-end components or only back-end components. Component teams will commonly work with the same components over and over again throughout multiple features. As a result, a component team will be very specialized in the work that they do.

### Which one is better?
For feature teams, we do not have to coordinate much with other teams so planning is significantly easier. However, feature teams have to learn quite a bit more technologies, languages, and frameworks since they are going to be making a code change in many different components.<sup>3</sup> Component teams don't need to be able to work in near as many areas so they can have a more narrow focus and be more specialized. On the other hand, a component team has to coordinate with other teams in order to complete features so planning is quite a bit tougher. So which route is better?

### There are quite a few drawbacks of component teams

##### Increases number of failure points from one (in a feature team scenario) to the number of component teams<sup>4</sup>
Imagine four component teams working together to complete a feature. Now imagine three of those four teams finish their work on time. It only takes one component team to slip a date to delay a release.

##### Communication between teams is much more expensive than communication within your own team<sup>5</sup>
Communicating within your own team is easy. Your team sits right next to each other and in the same office. Your schedules all work together since your team has recurring meetings scheduled for timeslots you are available at. Your team has shared, agreed upon (hopefully) standards that you follow. When it comes to communicating with other teams, they may be far away. You have to find a schedule that works for both your team and other teams. The standards that your team has might conflict with the standards the other teams have. There also tends to be more miscommunication.<sup>4</sup>

##### Which team writes the requirements?
Requirements almost never divide along components. They mostly divide along multiple components.<sup>5</sup> This makes it hard to know who and which team should work on creating the requirements.

##### Component teams rarely are able to start working on a feature at the same time<sup>3</sup>
Component teams will each have their own schedule and bandwidth. It is pretty common to see one component team have bandwidth to start working on a feature in April but another component team isn't available to start until July. What happens here?

##### A feature might require significantly more work in some components than others
What ends up happening here is that one component team would finish all of the work for their component significantly before other teams. This is a complicated situation because the component team that is ahead will have to type up all of this code that is not yet fully integrated because the other components they depend on are way behind. When integration is deferred until late in a project, it almost never goes well. When a particular component is finished early, it would be convenient if the team members can swarm to help components that are behind so that the feature can be released faster. However, with component teams this doesn't happen since component teams don't touch other team's components. So if one component is behind, there is no technique to speed it up. The release will be delayed.

##### Difficulty producing a potentially shippable product increment at the end of each sprint<sup>4</sup>
It generally requires code changes in each layer and for the code to be fully integrated in each layer to produce a potentially shippable product increment. This means that if a component team wants a potentially shippable product increment by the end of the sprint, they have to coordinate with other teams. What usually happens instead is each component is developed in isolation and wired together at the very end of the project. This leaves us with no potentially shippable product increment at the end of each sprint, no value added to the client each sprint, and nothing that we can get much feedback from at the sprint review. 

##### Big up front design and freezing the interfaces between components
Since there is a larger overhead with meeting people from other teams, component teams might do extensive up-front design and finalize an interface between components so that they can go back to their place and not have to worry about what the other team is doing. However, it is incredibly difficult to up-front predict how everything is going to work out. This usually ends up with teams having the additional overhead of having to use the adapter pattern to communicate with the interface that was finalized months in advance.<sup>2</sup> However, it is better to have our design be emergent. We want people who are working on one component for a feature sitting next to someone who is working on another component for the feature. The interface between components will be loosely defined and will gradually emerge as the programmers learn more about the problems they are solving through feedback from their tests.<sup>2</sup> It is easy to coordinate changes to an interface between components when you are sitting right next to each other. With component teams, we might not have this luxury of being able to sit next to each other. 

##### Competing requests<sup>4</sup>
Imagine a component team that exists to make software for other teams. Now imagine that four different teams request a component team to help them in completing a feature. How would the component team know how to prioritize competing requests? Does the component team do first come first serve? What if Team A reaches out to component team C before Team B does but Team B has significantly more clients and business value?

##### Component teams may not allow for the highest priority features to progress at any point in time
Suppose that in one quarter there are tons of high priority requests for code changes to be made by a component team on a component. The component team may not have enough resources to get to all these requests so some of these high priority requests wouldn't be able to be worked on which will delay features. With the feature team set up, if many teams have high priority requests for code changes in a component, they could all work on making code changes in the component which allows their features to all progress. This is why the feature team set up is considered to be a setup that is for optimizing value.  

##### Requesting a code change in a component team set up generally takes more calendar days to complete than in a feature team setup
A component team can generally make requested code changes your team needs in less man hours than if your team made the changes since the component team is more knowledgeable about their component that needs the changes. However, these code changes will generally take more calendar days if done by the component team than if your team did the changes. This is because in the component team setup, you will generally have to wait some time for the component team to be able to start working on the requested code changes due to scheduling conflict. With the feature team setup, you can start working on changes to the component as soon as you figure out you need to make code changes in it. The additional calendar days needed in the component team set up is generally too much to offset the reduction of man hours since it commonly results in a delayed release.

### The downside of Feature teams is manageable
A feature team is not going to be as specialized at the work it is doing as the component team is
- while this is true, members within a feature team can still specialize in different areas and give themselves a preference towards choosing tasks that suit their expertise.<sup>6</sup>
- someone knowledgeable about a component that a feature team needs to change can point the feature team in the right direction about which packages to focus on learning<sup>2</sup>
  - a package by feature structure can be used to make it easier to determine which packages need code changes
  - the feature team member does not need to worry about learning the whole component

### Conclusion
We should strongly favor feature teams over component teams in a software organization.<sup>1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16</sup> Component teams should be rare. Feature teams make it much easier to code a little bit of each layer and fully integrate the layers by the end of the sprint. This is because all of the expertise needed to do this is on the same team. This goes well with agile, promoting iterative and incremental development for early feedback. A feature team will start working on a feature and keep working on it until it is done. We don't have to worry about a partially completed feature sitting on the shelf waiting in queue due to a hand-off to another team. This reduces cycle time which gets our product out to customers faster. So while component teams might make sense in that they allow everyone to work in their expertise, they suffer from resource allocation issues and scheduling conflicts.

What about for situations in which we have many teams, say 100 teams working on the same product at the same time? Would this be a scenario in which we want to use component teams instead of feature teams? Craig Larman and Bad Vodde say no.<sup>2</sup> We can take a large feature, break it down into smaller features, and assign each small feature to a feature team. <sup>2</sup>

So when should component teams be used? For that, I have made a new blog post which can be found [here](https://amcneil36.github.io/blogs/when-should-a-component-team-be-used).

## Sources
1. Axelrod, Arnon. Complete Guide to Test Automation, Techniques, Practices, and Patterns for Building and Maintaining Effective Software Projects. Apress, 2018  
2. Craig Larman and Bas Vodde. Practices for Scaling Lean & Agile Development: Large, Multisite, and Offshore Product Development with Large-Scale Scrum. Addison-Wesley, 2012.
3. Craig Larman and Bas Vodde. Large-Scale Scrum: More with LeSS. Addison-Wesley, 2016.
4. Rubin, Kenneth. Essential Scrum: A Practical Guide To The Most Popular Agile Process. Addison-Wesley, 2013.
5. Humble, Jez and Farley, David. Continuous Delivery: Reliable Software Releases Through Build, Test And Deployment Automation. Addison-Wesley, 2010.
6. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.
7. Cohn, Mike. Succeeding With Agile: Software Development Using Scrum. Addison-Wesley, 2013.
8. Narayan, Sriram. Agile IT Organization Design. Pearson Education, 2015.
9. Kim, Gene, et al. The DevOps Handbook: How to Create World-Class Agility, Reliability, and Security in Technology Organizations. IT Revolution, 2016.
10. Forsgren, Nicole, et al. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations. IT Revolution, 2018.
11. Gruver, Gary, et al. A Practical Approach to Large-Scale Agile Development: How HP Transformed LaserJet FutureSmart Firmway. Addison-Wesley, 2015.
12. Pichler, Roman. Agile Product Management With Scrum. Addison-Wesley, 2010.
13. Appelo, Jurgen. Management 3.0: Leading Agile Developers, Developing Agile Leaders. Addison-Wesley, 2011.  
14. Mary and Tom Poppendieck. Leading Lean Software Development: Results Are Not The Point. Addison-Wesley, 2009.  
15. Craig Larman and BasVodde. Scaling Lean & Agile Development: Thinking and Organizational Tools for Large-Scale Scrum. Addison-Wesley, 2008.  
16. Grosse, Alexander and Loftesness, David. Scaling Teams, Strategies for Building Successful Teams and Organizations. O'Reilly Media Inc., 2017.
