### Overview of Feature teams and Component teams

Clients will request a feature which is some sort of functionality which is useful to them. Features will almost always require code changes in multiple components. A component can be thought of as a re-usable asset which may be used to help complete a portion of one or more features. 

There are two different types of ways in which teams might organize in order to deliver software. There are feature teams and component team.

**Feature team:** this would be a a team that takes whole customer-centric features and completes them. This team would contain all of the expertise necessary to make code changes in all components necessary to complete a feature. Since features almost always require code changes in front-end and back-end, this team will need to have front-end expertise and back-end expertise. Because they are cross-functional, there is a reduced need to coorindate with other teams to complete a feature. A feature team would ideally be long-lived, completing many features together one at a time.

**Component team:** this would be a team that is assembled around particular component(s) that are used to complete part of a feature. The remainder of the feature would be completed by one or more other component teams who are also specialized in components that complete part of a feature. A component team would typically be working on only front-end components or only back-end components. Component teams will commonly work with the same components over and over again throughout multiple features. As a result, a component team will be very specialized in the work that they do.

### Feature teams vs Component teams
For feature teams, we do not have to coordinate much with other teams so planning is significantly easier. However, feature teams have to learn quite a bit more technologies, languages, and frameworks since they are going to be making a code change in many different components. Component teams don't need to be able to work in near as many areas so they can have a more narrow focus and specialize. On the other hand, a component team has to coordinate with other component teams in order to complete features so planning is quite a bit tougher. So which route is better?

### There are quite a few drawbacks of component teams


up-front freezing the design between interfaces

increased number of failure points

it is hard to write test and requirements for a single component in isolation since a functionality will cross more than one component

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


