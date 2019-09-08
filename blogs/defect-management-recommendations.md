# Defect Management Recommendations

It can be difficult to determine how to choose between working on defects and working on features. It might also be unclear as to how much time should be spent working on defects. We will discuss some options out there for how to answer these questions. We will start by discussing defect management recommendations for departments of one team where there is just one team working on a product. After that, we will discuss defect management recommendations for departments where multiple teams are working on the same product.

## When a department has only one team working on a product

### How do we prioritize between defects and enhancements?

#### Fully Allocate 1 or 2 people to support at all times
This is the easiest route for handling defects. You can take one or two people and fully allocate them to support. They will only work on defect fixes and tech debt. The rest of the team will work on enhancements for the next reelase.

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
