# Defect Management Recommendations
### Minimizing waste when it comes to defect management
In a perfect world, we would have everyone on the team work on the same release which is the next release. We wouldn't switch to something else until we release. This will make for the earliest releases to market. Let's say we have feature A which is 1 month of work and feature B which is also 1 month of work. Let's say that feature A and B can be released individually. It doesn't make sense to complete part of feature A and then switch to feature B without releasing feature A. Having multiple partially complete features at a time is just more waste because value is not delivered until we release.

The same thing could be said about defects. Suppose there is some known defect called Defect A. Also, suppose defect A will take a bit of work and can be worked on by everyone at the same time. Also suppose we have a feature we are about to work on called feature A. It would not make sense to complete part of feature A, switch to fixing defect A, and then come back to finish feature A. We should do all of feature A followed by all of defect A or do all of defect A followed by all of feature A. The product owner will identify whether feature A or defect A is of a higher priority and that one will be done first.

Great! So whenever we start working on a feature, we should never have anyone switch to working on a defect until we release our feature, right? Well unfortunately, it's not that simple. If we were somehow aware of all defects in production, then yes; switching to work on a defect after already having completed part of a feature would just mean that we should have done the defect first. However, we won't be aware of all of the defects in production at any given time. Some of the defects won't be discovered by or reported by our clients until we are in the middle of working on a feature. If this defect is more important than the feature we are working on, then at least one person needs to stop working on the feature to start working on this defect. As a result we will often times be simultaneously working on one feature as well as zero or more defects.

### Should we have a zero defect backlog?
Some teams do what is called a zero defect backlog approach. In this scenario, they try to have zero defects in their backlog. For some teams, this means that whenever a defect is reported, at least one person has to immediately stop what they are doing (if they are working on a feature) and fix the defect prior to resuming work on a feature. Other teams are more flexible with the zero defect backlog approach and have a policy of just needing to get any incoming defect fixed within, say, 4 months of being reported.

Upon first hearing this concept of a zero defect backlog, I thought it was a great idea. If a customer is paying for software, it ought to work! It would seem unfair to have a customer pay for software that has defects! So if we care about what is best for our customer, we should always halt work on features and fix the defects as they come in so we can have a zero defect backlog, right? Wrong.

Martin Fowler and Kent Beck are opposed to the idea of trying for a zero defect backlog. They think that having a zero defect backlog is rarely what is best for the customer. Why? Fixing defects takes time away from working on features.
#### Fully Allocate 1 or 2 people to support at all times. The rest can all do enhancements or all join in support.
This is the easiest route for handling defects. You will have 1 or 2 people fully allocated to support at all times. You will then have the remaining people all do enhancements or all join in on support. Most of the time, these remaining people will be doing enhancements instead of support. Whenever enhancements are being worked on, we should do a support rotation every few sprints. This would entail rotating 1 or 2 people out of support and into the enhancement group. When that happens, we would also rotate 1 or 2 people out of the enhancement group and into support. Having anyone permanently do support for a long time will lead to burn out, less learning, and consequently turnover. That is why it is important to rotate out the people who are doing support.

The good thing about having 1 or 2 fully allocated people to support at all times is that it makes velocity a lot more predictable for the feature that is being worked on. Suppose we have a team of 7 and choose to have 2 people fully allocated to support at any given time.  It is safe to assume that we will spend roughly 30% of our time on support for the entirety of our time that the feature is being worked on. When we plan our sprint, we don't have to worry about the possibility of being interrupted by defects in production since we already have 2 individuals who are fully allocated to deal with that. The same could be said about planning a release. With our more predictable velocity, it is easier to plan how long work will take and thus the releases and sprints are easier to plan.

The people in support should use Kanban. Kanban works a lot better than Scrum for the unpredictability that results in defects being reported at any given time. With scrum, you try to plan your sprint such that you can produce a potentially shippable product increment that you can demo at the end of the sprint for feedback. When it comes to defect fixes, there is not much feedback to be received on defect fixes at a demo so timeboxing a defect fix doesn't do as much. A lot of the sprint planning effort goes to waste when working in support because whatever plans you came up with get tossed out the door when high priority defects come in during the middle of the sprint.

For the group working on enhancements, I prefer Scrum. The added predictability that comes with this team from not having to worry about incoming defects results in a lot more benefit from sprint planning. Throughout the sprint, we are more likely able to work on the tasks that we predicted we would work on in our sprint planning meeting since we don't have to worry about defects coming in and throwing off our plans. The sprint planning done in scrum also optimizes the team around having the product ready to receive feedback at the end of the sprint. This is good because it helps us build the right thing.

The obvious downside to this approach is that we might not all be working on the highest priority items at any point in time. We have 1 or 2 people fully allocated to support at any given time so that our velocity on the feature is predictable. However, if there are not any high priority defects at any point in time, then the work that the people are doing in support might not be as high priority as the work being done on the feature. 

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
