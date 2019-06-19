### Overview of Feature teams and Component teams

Clients will request a feature which is some sort of functionality which is useful to them. Features will almost always require code changes in multiple components. A component can be thought of as a re-usable asset which may be used to help complete a portion of one or more features. 

There are two different types of ways in which teams might organize in order to deliver software. There are feature teams and component team.

**Feature team:** this would be a a team that takes whole customer-centric features and completes them. This team would contain all of the expertise necessary to make code changes in all components necessary to complete a feature. Since features almost always require code changes in front-end and back-end, this team will need to have front-end expertise and back-end expertise. Because they are cross-functional, there is a reduced need to coorindate with other teams to complete a feature. A feature team would ideally be long-lived, completing many features together one at a time.

**Component team:** this would be a team that is assembled around particular component(s) that are used to complete part of a feature. The remainder of the feature would be completed by one or more other component teams who are also specialized in components that complete part of a feature. A component team would typically be working on only front-end components or only back-end components. Component teams will commonly work with the same components over and over again throughout multiple features. As a result, a component team will be very specialized in the work that they do.

### Feature teams vs Component teams
For feature teams, we do not have to coordinate much with other teams so planning is significantly easier. However, feature teams have to learn quite a bit more technologies, languages, and frameworks since they are going to be making a code change in many different components. Component teams don't need to be able to work in near as many areas so they can have a more narrow focus and specialize. On the other hand, a component team has to coordinate with other component teams in order to complete features so planning is quite a bit tougher. So which route is better?

### There are quite a few drawbacks of component teams

##### Increases number of failure points from one (in a feature team scenario) to the number of component teams
Imagine 4 component teams working together to complete a feature. Now imagine one of the component teams 3 of those 4 teams finish their work right away. It only takes one component team to slip a date to delay a release.

##### Communication between teams is much more expensive than communication within your own team
Communicating within your own team is easy. Your team sits right next to each other and in the same office. Your schedules all work together since your team has re-curring meetings scheduled for timeslots you are available at. Your team has shared, agreed upon (hopefully) standards that you follow. When it comes to communicating with other teams, they may be far away. You have to find a schedule that works for both your team and other teams. The standards that your team has might conflict with the standards the other teams have. There also tends to be more misommunication.

##### Which team writes the requirements?
Requirements almost never divide along components. They mostly divide along multiple components. This makes it hard to know who and which team should work on creating the requirements.

##### Component teams rarely are able to start working on a feature at the same time
Component teams will each have their own schedule and bandwidth. It is pretty common to see one component team have bandwidth to start working on a feature in April but another component team isn't available to start until July. What happens here?

##### A feature might require significantly more work in some components than others
When one 

component teams form silos and optimize locally instead of the project as a whole

code is usually integrated at the end

hand off

queue

miss-communication

potentially shippable product increment

how does a component team prioritize requests from other teams?

component teams don't know the value of what they are doing "im just doing this becuase someone told me"

too many new hires

what happens when one component needs a lot more work than the others? sure would be nice if we can swarm but nop

what if the teams coordinating have differnet standards/processes?

teams don't understand each other so they will not kno whwo to tell which layer should handle which logic
-----------------------------------
a feature team needs to know the whole system? no. the team as a whole, not each individual member requires the skills to implement the feature
within the team, people can still specialize

features are not randomly distributed to feature teams, the current knoelgde and skills of a team are factored into deciding which team works on which features


