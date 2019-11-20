## Sprint Planning
Sprint planning is typically done by doing the following:
1. Determine the team's capacity for the upcoming sprint (the amount of story points the team feels comfortable completing)
1. Select the top item off the product backlog
1. Discuss the story and break it down into tasks
1. Add the story to the sprint
1. Go back to step 2) and repeat until the team is at capacity

The team should try to fill up the sprint with enough work to be as close to being at capacity as possible without going over. Suppose that your capacity is 20 story points for this upcoming sprint. Let's say that you are in the middle of sprint planning and already have 17 story points worth of work pulled into the sprint. Furthermore, let's say that the next story off the top of the backlog that you are looking at is 4 story points. Pulling in this story would cause you to go over capacity so you don't want to do that. You should try to see if you can break down this story into 2 stories and then pull in one of the 2 stories so that you are not over capacity. If this story can't be broken down further, then you should look deeper down in the product backlog to see if there is a story that is 3 story points or less that can be pulled in. If not, it is OK to just have 17 story points signed up for in the sprint.

### Push vs Pull system
In sprint planning, the product owner will identify which work is the most important. From there, there are generally two different approaches for how sprint planning is done. One way is to use a push system in which a leader will determine who does what. Another way to decide who does what is to use a pull system where each individual team member decides which work they will do. The recommended way to do sprint planning is to use a pull system. This comes from the notion from the Agile Manifesto that teams should be self organizing. Teams are more productive and produce a higher quality product when they are self organizing.

### How small should the user stories be broken down to?
In scrum, there is an emphasis on completing user stories. Code corresponding to a user story can only be released if the user story is done. We want to be completing user stories and demo'ing these completed user stories at the end of every sprint. Therefore, it is important to make sure that our user stories are small so that we feel comfortable finishing them within a sprint.

Suppose that we have one large user story that we pull into the sprint and that's it. Maybe we feel there is a 70% chance that this user story can be completed in a sprint. We either finish the user story and everyone is happy or we do not finish the user story and have nothing truly completed by the end of the sprint. This is a huge risk. We can decrease this risk by breaking down the large user story into smaller user stories and pulling the smaller user stories into the sprint. This way, even if we don't finish every user story that was pulled into the sprint, we still have some user stories that were truly completed in the sprint.

So the smaller your user stories are, the more likely it is that you will have some fully completed user stories that can be demoed for feedback at the end of the sprint. So it could be said that the smaller the user story, the better. However, we don't want to go all out making the user stories as small as possible because doing that would increase the overhead of managing the product backlog. Aiming to have the user stories be small enough such that the team completes 1-1.3 user stories per person each sprint is a good number for a two week sprint. So for a team of 7, this would mean completing 7-9 user stories each sprint.

### Should all tasks be assigned to people during sprint planning?
When it comes to when to sign up for tasks, one of three approaches are typically taken:
1. 100% of the tasks are assigned to people during sprint planning. No change in assigness are allowed.
1. 100% of the tasks are assigned to people during sprint planning. Assignees can be changed as needed throughout the sprint.
1. Each team member signs up for one task during sprint planning which is their first task. The rest of the work pulled into the sprint will be unassigned and can be picked up as people become free.

Option 1 should be avoided. This option typically results in more of an individual mindset rather than a team mindset. You might feel less inclined to help a teammate because helping that teammate would decrease the chance that you finish all work assigned to you for the sprint. This is a sub-optimal way to work. Secondly, option 1 assumes that we have a good idea about how long each task will take. What if someone's tasks end up taking way less time than predicted and the other person's tasks end up taking way longer than predicted? The person who finishes early would end up working on some less important task that never got pulled into the sprint since this option states we can't change assignees.

Options 2 and 3 are similar since one of them has no assignees for unstarted work and the other option just has "loosely" assigned people for the unstarted work. Depending on just how loosely assigned the unstarted work is, option 2 could be more or less the same as option 3. Both these options are quite a bit better than option 1 because they promote a team mindset more. You can help others with their work and don't have to worry about having other tasks assigned to you ending up incomplete because someone else might be able to pick them up. I have a minor preference for option 3 as it promotes teamwork the most and also promotes a blameless culture but I wouldn't be disappointed to see a team doing option 2. As long as you aren't doing option 1, you at least have the right idea.

### Is it one programmer per story? Every programmer on the same story? Somewhere in the middle?
One option is to have one programmer per story. This person would just keep working on the story until it is completed. Alternatively, this option might be done with pair programming where one person pair programs an entire user story with someone else where they are working on the same branch and same computer the whole time. This makes coordination not near as difficult. You focus on just your story and that's it.

The situation above makes coordination a lot easier but it suffers from two problems. One problem is that it causes there to be many user stories in progress at the same time. Secondly, we run into the risk of finishing the sprint with many partially completed user stories but no fully completed user stories. One way around this is to put every programmer on the same user story. If you have every programmer on the same user story, then you minimize the chance of ending the sprint with no fully completed user stories. However, putting many people on the same user story could lead to people stepping on each other's toes.

Another option is to just go somewhere in the middle to try to get the best of both worlds. Is that what we should do? Sometimes. Not always. Let's discuss how to determine when to use which setup.

It is generally more efficient to have more than one programmer per user story so that team members can sign up for tasks that suit their specialization. User stories generally have both a front-end and back-end component so this would allow for someone stronger in front-end to sign up for the front-end work and someone stronger in back-end to sign up for the back-end work. For user stories that are smaller than normal and easy, it would be OK to have one programmer.

Depending on how many programmers there are on a scrum team, there may or may not ever be a scenario where you want every programmer on the same user story. Good user stories are broken down to be very small so the max amount of programmers that might make sense on a user story would be around 4 where they are all [pair programming](https://amcneil36.github.io/blogs/maximizing-the-return-on-investment-of-pair-programming). This would make sense for user stories that are considered to be very difficult.

Any remaining user stories (perhaps those of medium difficulty) might be fine with having 2-3 programmers.

### What percentage of the time should the team complete every story that was pulled into the sprint?

A team won't always complete everything that is pulled into a sprint and that is OK. Maybe something outside of your team's control goes wrong like losing internet connection. Maybe another team's code that you depend on happens to break all of a sudden. Perhaps you took the correct steps when estimating how long a story would take but due to unknown unknowns, the story took longer than predicted. That is OK. So we won't complete everything every time. How often should be complete everything that was pulled into the sprint? It is good to know the answer to this question because it will help you determine your capacity at the start of your sprint. Should the team sign up for very few story points worth of work so there is a super high chance that you finish everything? Should the team be more aggressive and sign up for a bit more story points of work with a decent risk that some of the work won't finish?

A good rule of thumb is to aim to complete everything that gets pulled into the sprint 80% of the time. Whatever percentage your team is going with, make sure every developer on the team knows this number. Knowing this number will help team members determine the amount of story points worth of work that might make sense for the team to pull into the sprint. During the 20% of the time where the user stories do not get all finished in the sprint, discuss in the retro as to what may have caused the stories to not get finished and whether or not any adjustments should be made for upcoming sprints. The intention is to not put blame on others. The intention is to improve. 

### Summary

To outline the most important parts of sprint planning:
 - Use a pull system to determine who does which work
 - Have everyone sign up for one task during sprint planning and have the rest unassigned. Let team members assign tasks to themself as they finish their tasks
 - For every easy and very small user stories, it is OK to have only one programmer work on the story. For all other stories, it makes sense to have more than one programmer work on the same story so that they can sign up for tasks that suit their specializations
 - Aim to complete all user stories that were pulled into the sprint 80% of the time
 
We want our team to be getting in a team mindset. The product owner identifies the work that needs to be done but then individual team members sign up for which of those tasks they prefer. We leave most of the work un-assigned at the start of the sprint so team members can pick up work as needed. This helps combat the scenario in which some work takes longer than predicted and some work takes shorter than predicted. We understand that we won't always finish all work in the sprint and we don't aim to finish all work in the sprint every time anyway. We then take a blameless approach in the retros to discuss potential improvement actions that could help us with sprint planning (and other areas) next time.
 
 ## Sources
 1. Cohn, Mike. User Stories Applied For Agile Software Development. Addison-Wesley, 2004.  
 1. Cohn, Mike. Agile Estimation and Planning. Pearson Education, 2006.  
 1. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.
