# Defect Management Recommendations

### Should defects go in a separate backlog?
No. One product one backlog. Having defects in a separate backlog than enhancements makes it more difficult to prioritize. The product owner now has to look at two different backlogs in order to determine what to do next. Is the top item on the enhancement backlog or the top item on the defect backlog more important? With enhancements and defects in the same backlog, it is easy. The backlog is sorted by priority. If a particular defect is more important than a particular enhancement, then the defect goes higher up in the backlog than the enhancement and vice-versa. Having the defects and enhancements in the same backlog makes it very easy to visualize which work to do next.

### Should we have a zero defect backlog?
Some teams do what is called a zero defect backlog approach. For some teams, this means that whenever a defect is reported, at least one person has to immediately stop the enhancement they are doing and fix the defect right away. Other teams are more flexible with the zero defect backlog approach and have a policy of just needing to get any incoming defect fixed within, say, 4 months of being reported.

Martin Fowler and Kent Beck are opposed to the idea of a zero defect backlog. The problem with the zero defect backlog approach is that it assumes that stopping to fix an incoming defect in production is always going to be what is in the best interest of the customer. We have to think in terms of opportunity cost here. Time spent fixing a defect is less time spent working on a feature and vice-versa. There will be scenarios in which stopping work on a feature to fix a defect is what is in the best interest of the customer. There will be scenarios in which finishing the feature before fixing the defect is what is in the best interest of the customer. The customer has to make a judgement call about whether to continue working on the feature or to stop to fix the defect.

### Minimizing waste when it comes to defect management
In a perfect world, we would have everyone on the team work on the same release which is the next release. We wouldn't switch to something else until we release. This will make for the earliest releases to market. Let's say we have feature A which is 1 month of work and feature B which is also 1 month of work. Let's say that feature A and B can be released individually. It doesn't make sense to complete part of feature A and then switch to feature B without releasing feature A. Having multiple partially complete features at a time is just more waste because value is not delivered until we release.

The same thing could be said about defects. Suppose there is some known defect called Defect A. Also, suppose defect A will take a bit of work and can be worked on by everyone at the same time. Also suppose we have a feature we are about to work on called feature A. It would not make sense to complete part of feature A, switch to fixing defect A, and then come back to finish feature A. We should do all of feature A followed by all of defect A or do all of defect A followed by all of feature A. The product owner will identify whether feature A or defect A is of a higher priority and that one will be done first.

Great! So whenever we start working on a feature, we should never have anyone switch to working on a defect until we release our feature, right? Well unfortunately, it's not that simple. If we were somehow aware of all defects in production, then yes; switching to work on a defect after already having completed part of a feature would just mean that we should have done the defect first. However, we won't be aware of all of the defects in production at any given time. Some of the defects won't be discovered by or reported by our clients until we are in the middle of working on a feature. If this defect is more important than the feature we are working on, then at least one person needs to stop working on the feature to start working on this defect. As a result we will often times be simultaneously working on one feature as well as zero or more defects.

### Should we have a separate development team for support?
Suppose we have a development team that works on creating new features as well as supporting existing features in production. What some people will do is take this team and break it into two teams: one team that is only responsible for creating new features and one team that is only responsible for support. Suppose we have 5 software engineers. We might put 1 or 2 of them on support while the remainder work on features. The people on the support team are fully allocated to support and must investigate incoming defects as well as fix incoming defects. If they have some free time as a result of no incoming defects, they would work on tech debt. The people on the feature team would continue to work on new features and any issues found in production would go to the support team. Any issues found during testing internally would be fixed by the feature team.

Some groups using this technique will periodically do rotations where people from the support team get rotated back into the enhancement team and vice versa. Other groups might not choose to implement rotations. The problem with being permanently allocated to support is that it leads to high turnover due to not being as enjoyable. It also doesn't provide for as much of a learning experience. The best learning experience for a software engineer comes from obtaining experience in both enhancements and defect corrections. Working in support provides insight valuable insights that can help you when you switch back to enhancements. So if we chose to go the route of having a separate team for support, we want to implement support rotations. Now that we understand how having a separate team for support works, it's time to answer the question: should we have a separate team for support?

Martin Fowler and Kent Beck say no. The problem with having separate teams for support and enhancements is that it results in people working on work that may not be the highest priority work at the time. The people on support are fully allocated to support. If there aren't many incoming defects during a particular time period, these people still have to do support. This could result in them working on low priority defects that are not as important as the feature that is being worked on. Things could also happen the other way around. There could be many high priority defects coming in that are swamping the support team. The people on the enhancement team would end up working on enhancements that are not as high priority as the incoming defects since they are fully allocated to the enhancement team.

Having the same team do both support and enhancements allows us to make sure we are working on the highest priority items at all times. This allows the customer to make trade off decisions about whether to prioritize the feature or the defect. During times where more defects are reported than normal, we can allocate more people to support as needed. When there are less defects reported than normal, we can allocate more people to features instead. Having this flexibility to easily adjust our allocation to enhancements or defects based on changes in priorities  is what we want.

#### Allocate a buffer for each sprint for support
In the previous scenario, we had 1 or 2 people break off from the team to do support. In this scenario, we have the whole team doing Scrum together and anyone could potentially work on a defect during the sprint. If a high or critical priority defect comes in during the sprint, the defect will be added to the sprint and looked at right away. If a medium priority defect comes in during the sprint, the product owner will make a judgement call as to whether or not to add that defect to the current sprint. When the team does their sprint planning, they will not sign up for work that fills up the entire sprint. They will have a buffer so that there is room to add defects that are reported during the sprint if needed. Also during the sprint planning meeting, the team can pull in unfinished defects that had been worked on during the previous sprint. In addition to that, the product owner will make a judgement call as to whether or not the medium priority defects found in the previous sprint should be fixed now or fixed after the feature is released. As a result, any given sprint planning meeting could result in signing up for only defects, only enhancements, or a mix of both. 

When making the judgement call in the middle of the sprint about whether or not to pull a medium priority defect into the sprint, the product owner can look at the buffer we have allocated to support and see if there is still room for defects within the sprint.

Unlike the previous approach, any developer could potentially do support during any sprint. We still want to use a rotation to determine who does which defect so that it is not always the same people doing defect fixes.

If there are no interuptions during the sprint, then our velocity towards our feature is going to be higher. If there are many high priority interuptions during a sprint, then our velocity towards our feature is going to be lower for the sprint. This is why the product owner needs to look at our buffer to make a judgement call as to whether or not to pull medium priority defects into the sprint in the middle of the sprint. If we are half-way through the sprint and have already used up most of our buffer, it probably doesn't make sense to pull in a medium priority defect to the sprint that was just discovered because that would put us at risk of accomplishing our sprint goal.

A similar statement could be made about planning our releases. In the previous approach, we might always allocate 2/7 of our people to support so we know that roughly 30% of our time is spent on support. If we have a large variety of amount of time spent working in support, it is going to be difficult to plan a release date for our feature. As a result, we should give a buffer for support when release planning. Suppose we give ourself a buffer of 30% for support during release planning. If we were to be half way to our release date and already used more of our buffer than predicted, we would probably want to hold off on more of the medium priority defects until after the feature is released. A burn down chart should be used to look at how our feature is coming along in relation to the release date so that the product owner can have an easier time making the judgement call between working on medium priority defects now vs after the feature is released.

This approach is a bit more complicated to implement than the previous approach because the product owner has many judgement calls to make about whether or not to add medium priority defects to the sprint. The product owner also has to prioritize between defects and enhancements here unlike in the approach above. The benefit we see with this approach is that we are more likely to be working on higher priority items at any point in time. This comes at the expense of having a tougher time keeping velocity consistent.

#### Whole team does Kanban!
We could do away with sprints completely and just do Kanban. Whenever a defect comes in, the product owner doesn't have to make any judgement call about whether or not to pull it into the sprint. The product owner just has to make a judgement call as to whether or not the defect is placed ahead of or behind the stories for the feature in the backlog. Thus, Kanban makes it easier to deal with defects and unpredictability than Scrum. If our feature starts to get behind schedule, we can consider trying to place a higher percentage of incoming defects further down in the backlog to where they are worked on after the feature is completed and released. We can have any person do defects and enhancemnts at any time with this approach. We should also make sure that we are rotating who is doing defect fixes.

#### Which option should we go with?
Option 1 is very easy to do, has a predictable velocity, and makes it easy to get valuable feedback at the demo. The downside is that the most important items might not be worked on at any point in time due to having 1 or 2 people that need to do support no matter what. Also, in scenarios in which there are many defects reported at the same time, we might have to pull people off of the enhancement team to help with defects anyway. We would need a team that has a decent amount of defects or tech debt for it to be worth considering having anyone fully allocated to support for a whole sprint. 

Option 2 does a better job than Option 1 of making sure the highest priority items are worked on at all times. However, option 2 is the most complicated option because you have to plan a buffer into the sprint and then make a judgement call when a defect is reported as to whether or not to pull it into the sprint. Since we are doing scrum, we should still be getting valuable feedback at the demo.

Option 3 is very easy to do and is the best at handling disruptions. This option does a good job of making sure the higher priority items are worked on. The only downside is that since we aren't using sprints, we might not receive quite as valuable feedback at the demo.

Now it is time to decide! I am going to go ahead and rule out option 1. Suppose you are doing option 1 where you fully allocate 2 out of 7 people to support every sprint. This is around 30% support allocation. You could technically do something similar in option 2 where in option 2 you allocate a 30% buffer to every sprint for support work. You have the flexibility to end up spending slightly more or less time doing support than that depending on how many defects are reported during that sprint. This added flexibility is what allows for option 2 to be better than option 1 since it helps us work on higher priority items at any point in time. Option 1 also suffers from having someone diverged from the feature for a longer time and potentially being confused when they return to the feature. It can also be tough to allocate someone to support for a lengthy time if they are the only one who is good in a particular area. This makes the feature tougher to work on.

Now it's a battle between option 2 and 3. This is essentially just a battle between Scrum and Kanban. There is no universal agreement as to which one is better. However, it is pretty clear that there are situations where Scrum makes more sense than Kanban and vice versa. 

Suppose that your team has a large amount of incoming defects. If you are doing Scrum, it will be very hard to plan sprints because a lot of high priority defects could come in at any time and throw off your plans made in sprint planning. You could make a larger buffer but having a large buffer just makes sprint planning unclear as to what will actually be done which starts to make sprint planning less helpful. We could negate some of this by just making our sprint shorter. With a shorter sprint, we can stick with our sprint planning plans more because pushing an incoming defect to the next sprint is no longer as big of a problem. So it could be said that with more interuptions, a smaller sprint is needed. However, if there are many defects, we will get to a point where our sprint becomes so small that we have a hard time producing a potentially shippable product increment at the end of the sprint for feedback. At that point, we might as well be doing Kanban instead. With Kanban, we can have defects pouring in frequently and we don't have to make that difficult decision about "do we pull this into the sprint" or not.

Now suppose you are instead on a team that has minimal incoming defects. If you are doing Scrum, your plans made in sprint planning will be more likely to hold up since there will be less interuptions. You would be more likely to be doing tasks that immediately deliver value and receive feedback in a demo.

So there you have it. For teams that are highly interupted with things such as defects, I like option 3 better. For teams that are not interupted as much, I prefer option 2. The only exception to this would be for teams that rarely receive valueable feedback in a demo. If you don't receive valuable feedback in a demo, then you don't get as much benefit from timeboxing. In that scenario, I would recommend Kanban, regardless of how few incoming defects there are.




	• Fully allocate 1 or 2 people to support every sprint where they will do support no matter what
		○ Predictable velocity but might cause someone to be working on a lower priority item than the feature
	• Ignore defects unless critical. Put them in next sprint and assign to some people. If critical then fail the sprint
		○ Makes velocity less predictable. Could say something like "30% of the time we do support" so some defects are pushed to later sprint
		○ Might make more sense to do kanban though
	• Allocate 30% of each sprint to unplanned work where we can pull in w/e defects we want, even ones that are not critical
	• Have the whole team do kanban!!
Do we use a separate backlog?
	• No. Doing so makes it hard to tell if the top item on the defect backlog is higher priority than the top item on the enhancement backlog. When finishing a feature, it might be good to have everyone do defects for a lil. We can have views within the product backlog.
	
Spend 20-30% of the time doing support

Bugs in production vs bugs found in testing

Large departments

Rotate whole teams into support
	• Doesn't work with 2 teams because then we are 50% support
	• Benefit of having teams stay together
	• Support team will be cross functional
	• Makes sense for large departments
	• Makes sense when teams have similar knowledge of features/products
	• Can be annoying if a feature is almost done and that team is rotated into support

Rotate few people from each team into support
	• Works for any team size, especially small teams
	• Problematic if one team only has one FE expert or one BE expert and that person goes into support
	• Makes the support team diverse if they have worked on many different features

Strategy when doing support:
	• Do investigating and try to fix
	• Fix if not lower priority
	• Investigate more important than fix
	• Client reported more important
	• Try to reproduce defect internally from the front-end
	• Write an automated test that shows the defect failing
