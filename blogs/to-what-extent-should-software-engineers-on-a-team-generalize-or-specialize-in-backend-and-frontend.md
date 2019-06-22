# To what extent should software engineers on a team generalize or specialize in the back-end and front-end?

We learned previously that scrum teams should be able to do both front-end and back-end code. But should each individual team member be able to write both front-end and back-end code?

Lets discuss 3 options for software engineers on a scrum team: generalist, specialist, and generalizing-specialist.

**Generalist:** each individual software engineer is equally comfortable in front-end as back-end  

**Specialist:** some software engineers specialize in front-end, not worrying about back-end. Remaining software engineers specialize in back-end, not worrying about front-end  

**Generalizing-Specialist:** some software engineers aim to have in-depth knowledge in front-end and broad knowledge in back-end. Remaining software engineers aim to have broad knowledge in front-end and in-depth knowledge in back-end

## Option # 1
### pros
It is very easy to do sprint planning because you can assign the tasks to just about anyone. Secondly, if one area of the project is having some issues and needs extra attention, anyone would be able to step up and do a reasonable job in helping out in that area. Teammates can also communicate well together because they have a similar understanding.

### cons
Unfortunately, this approach typically leads to lower code quality. Everyone is working in an area that they are "medium" at. No one is working in an area that they are truly great at. If we were in a situation in which we don't have the ability to choose who does which task, then having everyone be equally good in front-end and back-end would make sense. But, we have the flexibility to choose who does which task so it is more efficient if we can have people specialize in an area and then prioritize giving them tasks in that area. As a result, option 1 will not work.

## Option # 2
## pros
Software engineers are only working in an area they are specialized in so they should be able to work very fast and produce high quality code

## cons
If we were in utopia, this would definitely be the best option. Imagine the universe works out beautifully to where all our user stories we do automagically take the same amount of time doing front-end code as back-end code. We can have half of our software engineers specialize in front-end and half of them specialize in back-end. Or maybe all our user stories are 1/3 front-end and 2/3 back-end so we have 1/3 of our software engineers specialize in front-end and 2/3 of them specialize in back-end. This would be very efficient and easily the way to go.

Unfortunately, we don't live in utopia. Some user stories will be 50% front-end and 50% back-end. Other user stories will be 85% front-end and 15% back-end. We could also have user stories that are 15% front-end and 85% back-end. As a result, we cannot set up a team of specialists such that the front-end and back-end of a user story become finished around the same time when the specialists are only allowed to work in the area of their specialization. 

When a user story is front-end heavy, the back-end specialists will finish their work for the user story before the front-end specialists. Since the back-end specialists are only allowed to do back-end code, they have no choice but to start working on a new user story now. This increases the amount of time that code in one layer is written without being fully integrated into the other layer. Deferring integration lengthens the feedback loop and provides less sense of understanding about the state of the work. We don't truly know if our code will work until it is fully integrated.

Switching to a new user story also increases the amount of user stories in progress. We generally want to lessen the number of user stories in progress. Scrum asks that we produce a potentially shippable product increment by the end of each sprint. Having many partially complete user stories is no good. Partially complete user stories do not add value to the customer and do not promote getting valueable feedback at the sprint demo. Demo'ing partially completed user stories is also less efficient as it results in the user story being demo'd twice (once when incomplete and again when complete).

Lastly, what happens if a specialist takes a vacation? We need to be able to cover for this person when that happens.

## Option # 3

Here's the bottom line. It's good to have a software engineer specialize in an area and prioritize giving them tasks in that area. But the software engineer needs to be able to step up and work on tasks in an area outside of their specialization. Imagine a user story is 70% complete and the sprint is about to end. All that is left is some front-end code but back-end is your main specialization. You need to be able to step up and help out in the front-end to finish the end of the sprint. Or imagine there is a front-end task that is blocked by a back-end task. As someone who is a specialist in front-end, you still need to be able to step up and help with the back-end to help remove blockages. Being able to step up and remove bottlenecks is what separates this option from option 2. As a result, this is the best option.

---------------------------
note this applies to all teammates
at some times, certain areas of the project are more important (we need back-end help)
if your a front-end specialist, you would end up continuing with front-end tasks and get ahead of the back-end code
whoever is writing requiremetns needs to be able to do other stuff because getting ahead on requirements is no good
same for the tester-trying to write test cases for stuff that is not near ready to be started is waste
make decisions at the last responsible moment
what about having a specialist that works on multiple teams?
