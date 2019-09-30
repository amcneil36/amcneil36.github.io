## Release Planning

Releasing is the only thing that adds value to the customer so it makes sense to have some planning done on the release level. This is known as release planning. Release planning for new project generally involves planning 3-6 months of work. When planning a release, there are tradeoffs that need to be considered such as scope, date, and cost. It can almost always be expected that some things won't go according to plan so we need to have some sort of flexibility in our release plan to account for this. If we try to fix scope and date, we generally don't have enough room to make adjustments for when we get behind schedule. Adding humans late in a project makes the project later so having a flexible cost for the project doesn't do as much help as one might think. So in order to have enough flexibility for when something goes wrong, we need scope or date flexible. This leads us to the two most common ways to release plan which is to plan a fixed scope release or a fixed date release.

### Fixed Scope Release
In the fixed scope release, we end up with a release plan that includes the functionality we want to include in the next release. There will likely be a date range where all of this functionality is predicted to be finished by but the actual date of the release will just be whenever everything is done. If the work takes longer than predicted, we end up just releasing at a later date.

### Fixed Date Release
In the fixed date release, we end up with a release plan that has a date for when we want to release. There will be some functionality included on the release plan but it would be negotiable. Unlike the fixed scope release, the fixed date release will sometimes fix cost. If we have a release date, we can fix cost by simply not allocating more resources than originally planned. If the team gets behind schedule, we should be able to reduce scope and still release since we have done the highest priority work first.

### Should we do a fixed scope release or a fixed date release?
For defect fixes and minor enhancements, I like the idea of a fixed scope release where you just release whenever the code changes are done. For larger changes, especially for a project's initial release, I prefer the idea of a fixed date release. Fixing scope involves means you are indicating that you upfront know what the customer wants. What if the customer changes their mind? What if the market changes? We need to be able to have some flexibility to account for these things when working on a major release. However, for defect fixes and minor enhancements, customers changing their mind or changes in market is not as big of a deal since these code changes are so small and fast. We can release on each defect fix and can release on each minor enhancement completed as long as our release process is automated (which it should be anyway).

Customers also like the idea of receiving dates for major releases because major releases might be something that they need to make plans around. Perhaps your team is interested in starting a new Java project but the next major Java version is going to be released soon so you are thinking of waiting for that to come out before starting the project. Since Java communicates the dates they plan to release their major versions, you can use this information to determine if you want to start now with the current Java version or wait until the next Java version.. Knowing when the next minor Java version is going to come out probably won't be information you need to know in order to prioritize when to start on this new Java project.

### Planning a Fixed Scope Release
Planning a fixed scope release can be done by:
1. Identify your MVP or MMF depending on business needs
1. Add up the number of story points the work 
1. Use team's past velocity to provide a range of dates that the release is predicted to be done by
    * this is commonly done by taking the team's lowest velocity in the past few sprints for the lower bound estimate and the team's highest velocity in the past few sprints as the upper bound

If the fixed scope release is being used for a minor enhancement or defect fix, you don't need to go through all this work. You can simply just do the task or story and then release it when it is done.

### Planning a Fixed Date Release
Planning a fixed date release can be done by:
1. Identify your MVP or MMF depending on business needs
1. Add up the number of story points the work 
1. Add another 25-40% story points of work depending on risk, uncertainty, and consequences of not meeting the deadline
1. Add this to the amount of story points of work
1. Use team's velocity to calculate a relase date based on the amount of story points of work



-----------------------------------------------------------------
Release Planning
	• How to plan a fixed date release
		○ Determine if your release will be an mvp or mmf
			§ Mvp = release done with least amount of effort needed to learn something
			§ Mmf = smallest amount of features that deliver significant value to the customer
		○ Add up the number of story points this entails
		○ Add another 25-40% more story points of work depending on risk, uncertainty, and consequences of not meeting the deadline. 
		○ Choose a release date based on the amount of story points this sums up to and your velocity
	• Do not make assumptions about who will do what
	• How flexible is the release plan?
		○ Not very
		○ Can be changed every sprint
	•mmf vs mvp 
	Whole team works on the next release
		○ Some peeps might get pulled into defects
		○ Otherwise don't need to be working on two different releases unless your team is large at which point your 
		
		team can be broken down further
