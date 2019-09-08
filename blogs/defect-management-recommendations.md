# Defect Management Recommendations

It can be difficult to determine how to choose between working on defects and working on features. It might also be unclear as to how much time should be spent working on defects. We will discuss some options out there for how to answer these questions. We will start by discussing defect management recommendations for departments of one team where there is just one team working on a product. After that, we will discuss defect management recommendations for departments where multiple teams are working on the same product.

### Difficulty with prioritizing between defects and enhancements

One of the difficulties that arises when it comes to defect and enhancements is prioritization. Is it more important that we work on enhancements right now or defects? Or should we work on both at the same time? What if we start working on enhancements and then some high priorioty defects come in?

Another complication with defects is that incoming defects can make it tougher to release our features by the date we predict we will have them done by. We could be well on pace to deliver our feature on time but perhaps out of no where there are multiple client reported defects that come in. We might then choose to work on fixing those defects and then possibly miss our release date for the feature as a result.

Not only can defects mess up our release, but they can also mess up our sprint. We could be on pace to finish everything for the sprint but then get pulled into defects and no longer complete all of the work in the sprint.

#### Fully Allocate 1 or 2 people to support at all times
This is the easiest route for handling defects. You will have 1 or 2 people fully allocated to support at all times. You will then have the remaining people all do enhancements or all join in on support. Most of the time, these remaining people will be doing enhancements instead of support. Whenever enhancements are being worked on, we should rotate the 1 or 2 people out of support and into the enhancement group. As that happens, we would then rotate 1 or 2 people out of the enhancement group and into support. Having anyone permanently do support for a long time will lead to burn out, less learning, and consequently turnover. That is why it is important to rotate out the people who are donig support.

The good thing about having 1 or 2 fully allocated people to support at all times is that it makes work a lot more predictable for the group doing enhancements. 

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
