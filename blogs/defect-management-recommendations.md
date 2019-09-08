# Defect Management Recommendations

It can be difficult to determine how to choose between working on defects and working on features. It might also be unclear as to how much time should be spent working on defects. We will discuss some options out there for how to answer these questions. We will start by discussing defect management recommendations for departments of one team where there is just one team working on a product. After that, we will discuss defect management recommendations for departments where multiple teams are working on the same product.

### Minimizing waste when it comes to defect management
In a perfect world, we would have everyone on the team work on the same release which is the next release. We wouldn't switch to something else until we release. This will make for the earliest releases to market. Let's say we have feature A which is 1 month of work and feature B which is also 1 month of work. Let's say that feature A and B can be released individually. It doesn't make sense to complete part of feature A and then switch to feature B without releasing feature A. Having multiple partially complete features at a time is just more waste because value is not delivered until we release.

The same thing could be said about defects. Suppose there is some known defect called Defect A. Also, suppose defect A will take a bit of work and can be worked on by everyone at the same time. Also suppose we have a feature we are about to work on called feature A. It would not make sense to complete part of feature A, switch to fixing defect A, and then come back to finish feature A. We should do all of feature A followed by all of defect A or do all of defect A followed by all of feature A. The product owner will identify whether feature A or defect A is of a higher priority and that one will be done first.

Great! So whenever we start working on a feature, we should never have anyone switch to working on a defect until we release our feature, right? Well unfortunately, it's not that simple. If we were somehow aware of all defects in production, then yes; switching to work on a defect after already having completed part of a feature would just mean that we should have done the defect first. However, we won't be aware of all of the defects in production at any given time. Some of the defects won't be discovered by or reported by our clients until we are in the middle of working on a feature. If this defect is more important than the feature we are working on, then at least one person needs to stop working on the feature to start working on this defect. As a result we will often times be simultaneously working on one feature as well as zero or more defects.

Not having a good idea of how much time you will be spent in support can make it difficult to do our release planning. We don't know which date to go with if we have no clue how much we might get pulled into support. The same thing could be said about sprint planning for teams utilizing scrum. Let's take a look at some approaches to deal with defects and enhancements.

#### Fully Allocate 1 or 2 people to support at all times. The rest can all do enhancements or all join in support.
This is the easiest route for handling defects. You will have 1 or 2 people fully allocated to support at all times. You will then have the remaining people all do enhancements or all join in on support. Most of the time, these remaining people will be doing enhancements instead of support. Whenever enhancements are being worked on, we should rotate the 1 or 2 people out of support and into the enhancement group. When that happens, we would also rotate 1 or 2 people out of the enhancement group and into support. Having anyone permanently do support for a long time will lead to burn out, less learning, and consequently turnover. That is why it is important to rotate out the people who are doing support.

The good thing about having 1 or 2 fully allocated people to support at all times is that it makes velocity a lot more predictable for the feature that is being worked on. Suppose we have a team of 7 and choose to have 2 people fully allocated to support at any given time.  It is safe to assume that we will spend roughly 30% of our time on support for the entirety of our time that the feature is being worked on. When we plan our sprint, we don't have to worry about the possibility of being interrupted by defects in production since we already have 2 individuals who are fully allocated to deal with that. The same could be said about planning a release. With our more predictable velocity, it is easier to plan how long work will take and thus the releases and sprints are easier to plan.

The people in support should use Kanban. Kanban works a lot better than Scrum for unpredictability that results in defects being reported at any given time. Sprint planning in scrum is very hard to plan when there can be interuptions. If a defect were to come in, you would try to place the defect in the next sprint if it's not critical. Otherwise you would have to pull it in the sprint. With Kanban, you can take any incoming important defect and just place it at the top of the product backlog and get to it next.

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
