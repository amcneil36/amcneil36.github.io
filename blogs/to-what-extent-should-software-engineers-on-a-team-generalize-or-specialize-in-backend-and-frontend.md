# To what extent should software engineers on a team generalize or specialize in the back-end and front-end?

We learned previously that scrum teams should be able to do both front-end and back-end code. But should each individual team member be able to write both front-end and back-end code?

Lets discuss 3 options for software engineers on a scrum team:
- each individual software engineer is equally comfortable in front-end as back-end
- some software engineers specialize in front-end, not worrying about back-end. Remaining software engineers specialize in back-end, not worrying about front-end
- some software engineers aim to have in-depth knowledge in front-end and broad knowledge in back-end. Remaining software engineers aim to have broad knowledge in front-end and in-depth knowledge in back-end.

## Option # 1
### pros
It is very easy to do sprint planning because you can assign the tasks to just about anyone. Secondly, if one area of the project is having some issues and needs extra attention, anyone would be able to step up and do a reasonable job in helping out in that area. Teammates can also communicate well together because they have a similar understanding.

### cons
Unfortunately, this approach typically leads to lower code quality. Everyone is working in an area that they are "medium" at. No one is working in an area that they are truly great at. As a result, we don't want this approach.

## Option # 2
## pros
Software engineers are only working in an area they are specialized in so they should be able to work very fast and produce high quality code

## cons
If we were in utopia, this would definitely be the best option. Imagine the universe works out beautifully to where all our user stories we do automagically take the same amount of time doing front-end code as back-end code. We can have half of our software engineers specialize in front-end and half of them specialize in back-end. Or maybe all our user stories are 1/3 front-end and 2/3 back-end so we have 1/3 of our software engineers specialize in front-end and 2/3 of them specialize in back-end. This would be very efficient and easily the way to go.

Unfortunately, we don't live in utopia. Some user stories will be 50% front-end and 50% back-end. Other user stories will be 85% front-end and 15% back-end. We could also have user stories that are 15% front-end and 85% back-end. As a result, we cannot set up a team of specialists such that the front-end and back-end of a user story become finished around the same time when the specialists are only allowed to work in the area of their specialization. When a user story is front-end heavy, the back-end specialists will finish their work for the user story before the front-end specialists. Since the back-end specialists are only allowed to do back-end code, they have no choice but to start working on another user story now. This increases the amount of user stories in progress. We generally want to lessen the number of user stories in progress. Scrum asks that we produce a potentially shippable product increment by the end of each sprint. Having many partially complete user stories is not near as good as having a few fully completed user stories. With a few fully completed user stories, we are adding value to the customer and able to demo for feedback. Demo'ing partially completed user stories is inefficient as it results in the user story being demo'd twice (once when incomplete and again when complete).
