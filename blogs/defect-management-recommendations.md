# Defect Management Recommendations

### Place defects in the same backlog as enhancements
Having defects in a separate backlog than enhancements makes it more difficult to prioritize. The product owner now has to look at two different backlogs in order to determine what to do next. Is the top item on the enhancement backlog or the top item on the defect backlog more important? With enhancements and defects in the same backlog, it is easy. The backlog is sorted by priority. If a particular defect is more important than a particular enhancement, then the defect goes higher up in the backlog than the enhancement and vice-versa. Having the defects and enhancements in the same backlog makes it very easy to visualize which work to do next. This is where the saying "one product one backlog" comes from.

### We don't need to always fix defects immediately
Some teams do what is called a zero defect backlog approach. For some teams, this means that whenever a defect is reported, at least one person has to immediately stop the enhancement they are doing and fix the defect right away. Other teams are more flexible with the zero defect backlog approach and have a policy of just needing to get any incoming defect fixed within, say, 4 months of being reported.

In *Extreme Programming Explained,<sup>1</sup>* Martin Fowler and Kent Beck advise against doing a zero defect backlog approach. The problem with the zero defect backlog approach is that it assumes that stopping to fix an incoming defect in production is always going to be what is in the best interest of the customer. We have to think in terms of opportunity cost here. Time spent fixing a defect is less time spent working on a feature and vice-versa. There will be scenarios in which stopping work on a feature to fix a defect is what is in the best interest of the customer. There will be scenarios in which finishing the feature before fixing the defect is what is in the best interest of the customer. The product owner has to make a judgement call about whether to continue working on the feature or to stop to fix the defect.

### Minimizing waste when it comes to defect management
In a perfect world, we would have everyone on the team work on the same release which is the next release. We wouldn't switch to something else until we release. This will make for the earliest, most frequent releases to market. Let's say we have feature A which is 1 month of work and feature B which is also 1 month of work. Let's say that feature A and B can be released individually. It doesn't make sense to complete part of feature A and then switch to feature B without releasing feature A. Having multiple partially complete features at a time is more waste because value is not delivered until we release.

The same thing could be said about defects. Suppose there is some **known** defect called defect A. For simplicity of this example, suppose defect A will take a bit of work and can be worked on by everyone at the same time. Also suppose we have a feature we are about to work on called feature A. It would not make sense to complete part of feature A, switch to fixing defect A, and then come back to finish feature A. We should do all of feature A followed by all of defect A or do all of defect A followed by all of feature A. The product owner will identify whether feature A or defect A is of a higher priority and that one will be done first.

So, whenever we start working on a feature, we should never have anyone switch to working on a defect until we release our feature, right? Well unfortunately, it's not that simple. Notice that the example above was for a **known** defect. If we were somehow aware of all defects in production, then yes; switching to work on a defect after already having completed part of a feature would just mean that we should have done the defect first. However, we won't be aware of all the defects in production at any given time. Some of the defects won't be discovered or reported by our customers until we are in the middle of working on a feature. If this defect is more important than the feature we are working on, then at least one person needs to stop working on the feature to start fixing this defect. As a result, we will often simultaneously have one or more teammates working on working on a feature and one or more teammates working on a defect fix.

### Have the same team handle enhancements and support
Suppose we have a development team that works on creating new features as well as supporting existing features in production. What some people will do is take this team and break it into two teams: one team that is only responsible for creating new features and one team that is only responsible for support. Suppose we have 5 software engineers. We might put 1 or 2 of them on support while the remainder work on features. The people on the support team are fully allocated to support and must investigate incoming defects as well as fix incoming defects. If they have some free time as a result of no incoming defects, they would work on tech debt. The people on the feature team would continue to work on new features and any issues found in production would go to the support team. Any issues found during internal testing of the new feature being worked on would be fixed by the feature team instead of the support team.

Some groups using this technique will periodically do rotations where people from the support team get rotated back into the feature team and vice versa. Other groups might not choose to implement rotations. One problem with being permanently allocated to support is that it leads to high turnover due to support not being as enjoyable as working on enhancements. It also sets up a structure such that the people coding defects are different from the people who are fixing defects which results in people not learning from their mistakes.<sup>2</sup> The best learning experience for a software engineer comes from obtaining experience in both enhancements and defect fixes. Working in support provides valuable insight that can help you when you switch back to enhancements. So, if we chose to go the route of having a separate team for support, we want to implement support rotations.<sup>2</sup> Now that we understand how having a separate team for support works, it's time to answer the question: should we have a separate team for support?

In *Extreme Programming Explained,<sup>1</sup>* Martin Fowler and Kent Beck advise against having a separate team for support. The problem with having separate teams for support and enhancements is that it results in people working on work that may not be the highest priority work at the time. The people on support are fully allocated to support. If there aren't many incoming defects during a particular time period, these people still have to do support. This could result in them working on low priority defects that are not as important as the feature that is being worked on. Things could also happen the other way around. There could be many high priority defects coming in that are swamping the support team. The people on the enhancement team would end up working on enhancements that are not as high priority as the incoming defects since they are fully allocated to the enhancement team.

Having the same team handle support and enhancements allows us to make sure we are working on the highest priority tasks at all times. This allows the customer to make trade off decisions about whether to prioritize the feature or the defect. During times where more defects are reported than normal, we can allocate more people to support as needed. When there are less defects reported than normal, we can allocate more people to features instead. Having this flexibility to easily adjust our allocation to enhancements or defects based on changes in priorities is what makes having a single team for handling enhancements and support the better route.

### Avoid scheduling recurring "support only" timeslots
Some teams might come up with a team rule where every quarter they need to have 2 sprints where they only do defect fixes. Or maybe they come up with a team rule where every year they need to spend at least one quarter where they only do defect fixes. This does not mean that the team doesn't do defect outside of this time. It just means that when this time arrives, they will only do defect fixes.

This idea should be avoided because it may cause us to not be working on the highest priority items at any point in time. If you look at the product backlog and the most important items are defects, then it is fine to have the whole team do defect fixes during that time. However, if no defects are identified as the highest priority items, we don't want the team to be forced to work on defects just because their team rule indicates that it is that time of the year to only do defect fixes.

### Dealing with issues reported in production: a two step process
When there is an issue reported in production, we can think of the way we handle the issue in terms of two steps. The first step is investigating the issue that was reported and the second step is fixing the issue. All issues reported in production should be investigated by someone right away but do not necessarily need to be fixed right away. The purpose of investigating the issue is so that we can get enough information so that we know how to prioritize it. We need to know if we should stop what we are doing to fix the issue or if it is something that could be put off to a later point in time.

The investigation portion might be making note of things such as how frequently the bug occurs, how high the impact is when the bug does occur, assigning a priority to the ticket, estimating the time it will take to fix the issue, or closing the ticket after determining it's not an issue. We should not spend much time determining if a change request is considered an enhancement or defect fix since that won't affect how early the change request is completed unless we have an SLA with the customer on defect fixes.<sup>1</sup> Lastly, estimating defects is generally tougher to do than estimating enhancements so if you feel unsure then just mark the time estimate as unknown.<sup>1</sup>

After the issue investigation is completed and it is determined that a code change is going to need to be made, the customer will make the judgement call of whether this code change needs to be made now or if it should wait.

### Dealing with defects when using Scrum
Dealing with defects when using Scrum can be unclear at first because Scrum has this philosophy with sprints where adding new items to the sprint in the middle of the sprint is generally to be avoided when possible. The reason for this is that we do our sprint planning to produce a potentially shippable product increment that we can demoed for feedback. Any change to the sprint could cause us to fail the sprint goal and not get as much feedback. However, we have to be able to pull incoming defects into the sprint when they are high priority. As a result, we have two options for our sprint planning:
1. Fill up the whole sprint with work during the sprint planning. When high priority defects come in, pull them into the sprint and allow some of the originally planned work to not be finished
2. Plan each sprint with a buffer where we leave enough of the sprint open to where we feel comfortable being able to pull in new incoming high priority defects from production and still complete all of the initial tasks that were pulled into the sprint. If no defects come in and we finish everything for the sprint early, take the next item off the top of the backlog.

I prefer scenario 2 so that we don't have as much of our sprint planning going to waste when defects arise and we are more likely to accomplish the goal of the sprint.  This makes it more likely to produce a potentially shippable product increment that we can get feedback on since we set our sprint goal smaller and more obtainable. 

When a defect is reported in the middle of the sprint, we will have someone immediately investigate it. After it is determined that a change needs to be made, we could do one of three things:
1. Pull the change request into the current sprint and start working on it now
2. Place the change request at the top of the product backlog so that it can be started on in the next sprint
3. Place the change request farther down in the product backlog so it is done some time after the feature is released  

For high priority defects, we probably want to do 1. For everything else, the product owner will make a judgement call as to which of these three to do. If we pull too many defects into the current sprint, we are at danger of not accomplishing the sprint goal. This is what the buffer is for. The more we eat into the buffer during the sprint, the more we should avoid adding change requests to the sprint.

Option 2 might sound like a violation of what we discussed earlier about minimizing waste. If we finish our sprint without releasing our feature and then switch to the defect, shouldn't we have just fixed the defect first? This is the only scenario that is the exception to that rule. By moving the defect to the next sprint, we are more likely to complete the sprint goal and get feedback in our demo at the end of the sprint. In this case, we are considering the additional feedback from our demo to be more important than finishing the defect fix earlier. This wouldn't make sense for high priority defects but might make sense for medium priority defects at the discretion of the product owner.

The more incoming defects that a team typically has to handle, the more we should:
* have a larger buffer for sprint planning
  * this allows us to handle more defects in the sprint  
* decrease the sprint duration  
  * this makes it easier to push the defect to the next sprint since that wouldn't be as long of a wait  
* spend less time doing sprint planning  
  * with more unpredictability, we don't want to spend as much time doing sprint planning since a higher percentage of our planning could go to waste

As we can see, the more unpredictability there is, the less that our plans made in sprint planning will hold up. This brings up an interesting question: if we have a lot of unpredictability due to having many incoming defects each sprint, should we consider doing Kanban instead of Scrum? I will say that the more unpredictability there is, the more I prefer Kanban. The more predictability there is, the more I prefer Scrum. So, for teams with huge amounts of incoming defects, I prefer Kanban. When there is an incoming defect for a team using Kanban, they just have to decide whether to do the defect fix right now or after our feature is done. There is no judgement call that needs to be made about pulling the ticket into this sprint or next sprint since no sprints are used in Kanban. Thus, having no sprints makes handling random changes in plans easier.

### Dealing with context switching that occurs when issues in production are reported
We talked about the concept of someone needing to drop what they are doing to investigate an issue reported in production. This context switch can make it difficult to return to the task they had originally been working on. It can also cause this task to remain incomplete for a while and potentially go on to block another team member's task. One way around this is to incorporate [pair programming](https://amcneil36.github.io/blogs/maximizing-the-return-on-investment-of-pair-programming). One of the two people who are pairing can be considered the owner of the task. If an issue is reported in production, the person who is not the owner of the in-progress task can pick up this issue. The owner can continue with the task and will see it through to completion so that nobody gets blocked by it.

Another way to deal with the context switching is to break down the tasks to be smaller. Whenever an issue  is reported in production, you could reduce the scope of your task and merge or upload a code review of what you have. You would make a new task with the leftover work that you did not get to yet. This would only work if you keep all of your code tested at all times which you should be doing anyway. In the worst case scenario where you have to stop an in-progress task, write a few sentences that explain where you are leaving off at so you can read through that when you return.

### Monitoring progress of a feature to help prioritize between enhancements and defects
One thing to be careful of when trying to prioritize between incoming defects and the current feature we are working on is we could spend a bit more time than predicted doing defects instead of the feature which could cause our feature to become behind schedule. We should use a burn down chart to visualize whether or not our feature is on schedule. The more that our feature becomes behind schedule, the more the sense of urgency we might have to fix the medium and low priority incoming defects after the feature is released.

### The cost of finding defects in different stages
A study from IBM was done in 2008 analyzing the cost of software defects found at different stages.<sup>3</sup> It had the following findings:
* The cost to fix a defect in coding was 6.5 times higher than the cost to fix a defect during design
* The cost to fix a defect during testing was 15 times higher than during design
* The cost to fix a defect in production was 100 times higher than during design. 

This means that we should almost always avoid releasing with known defects.

### Summary
We want to set up our teams in a way where we can easily prioritize between defects and enhancements. Some things to keep in mind:
* Place defects and enhancements in the same backlog
* Have the same team handle enhancements and support
* Teams with less incoming defects may benefit more from using Scrum. Teams with more incoming defects may benefit more from Kanban
* Monitor the progress of the feature with a burn down chart to help prioritize between defects and enhancements
* Issues reported in production should be investigated immediately but do not necessarily need to be fixed immediately

This helps ensure that we are always working on the highest priority items at all times.

# Sources
1. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.  
2. Sterling, Christ and Barton, Brent. Managing Software Debt. Addison-Wesley, 2011.
3. Dawson, Maurice & Burrell, Darrell & Rahim, Emad & Brewster, Stephen. (2010). Integrating Software Assurance into the Software Development Life Cycle (SDLC). Journal of Information Systems Technology and Planning. 3. 49-53.
