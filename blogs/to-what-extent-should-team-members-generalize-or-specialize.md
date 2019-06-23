# To what extent should team members generalize or specialize?

Lets imagine we have a scrum team containing some software engineers, a business analyst, and a tester. Maybe the team contains a few other roles too or maybe it doesn't. The answer to this question is going to be the same in either case so do not worry if this example mirrors the set up on your team.

Lets discuss three different options that might represent a team member's skill set.

**Generalist:** this would be a team member that is equally good in many areas.

**Specialist:** this would be a team member that is extremely strong in one or two areas and does not do any work outside of their specialization.

**Generalizing-Specialist:** this would be a team member that has in-depth skills in one or two areas and broad skills in other areas. This person would attempt to primarily work in their specializtion but would be more than willing to work in an area outside of their specialization if needed. These people are sometimes called T-shaped people.

## Generalist
### pros
It is very easy to do sprint planning because you can assign the tasks to just about anyone. Secondly, if one area of the project is having some issues and needs extra attention, anyone would be able to step up and do a reasonable job in helping out in that area. Some sprints might require slightly different skill sets than others and for a team of generlists, that's not a problem. Someone could also be out of office and that would be less likely to cause any issues for a team of generalists.

### cons
People need to spend a lot of time doing spikes since there are many areas they need to be adequate in. Onboarding new people will take quite a bit longer. Lastly, people only work in areas they are medium at and don't work in any area they are truly great at.<sup>1</sup>

## Specialist
## pros
Team members are only working in areas that they are specialized in so they don't need near as much time to be onboarded. After getting onboarded, team members also don't need near as much time doing spikes and other types of studying. Team members would also be more likely to understand best practices in the area(s) they are working in since they only work in areas they are specialized in.

## cons
This setup makes it difficult to remove bottlenecks.<sup>2</sup> If something is going wrong in a particular area, we might want to have more than one person help out in that area. If people are all specialists, we may not be able to have more than one person to help out so having specialists makes bottlenecks take longer to remove. Secondly, some sprints might contain works that require a little bit different skill sets than other sprints. This means that in some sprints, our specialist set up aligns well with the work that needs to be done but in other sprints the specialist set up does not align well with the work that needs to be done.<sup>3</sup> Lastly, when a specialist is out of office, it might be hard for someone else to cover their area.

## Generalizing-Specialist
## pros
A majority of the work being done by a team member is in an area that they are specialized in. This set up also still has the ability for multiple people to step up and remove bottlenecks since they contain broad skills in many areas.

## cons
Team members will sometimes work in areas that they have broad skills in which might be a scenario where best practices are followed quite as well in comparison to having people only work in areas they are specialized in.

## So which option is the best?
### Ruling out the Generalists
I'm going to go ahead and rule out the option of there being a team of generalists since that is the most obvious one to rule out. If there is no control over who works on what, then having a team of generalists makes sense. But because people can choose what they work on, it makes sense to allow specialization and to have some sort of preference towards having people work in their specialization.<sup>1</sup> The questions is, should they specialize in just one or two areas and only work in those areas? Or are we going to want people to be able to take on a wider variety of roles such that they need to be a generalizig-specialist?

### Specialist or Generalizing-Specialist for Non-Software Engineers?
There are two important concepts that will be used to determine the answer to this question. One concept is the fact that some people may not be able to be a specialist for 40 hours per week on a team because there would not be enough work if they are only working in the area they are specialized in.<sup>4,5</sup> Secondly, certain types of tasks are significantly more important at certain points in time and significantly less important at other points in time. Fleshing out requirements is only needed right before work is about to begin by others to introduce the functionality to satisfy the requirements. We want to make decisions at the last responsible moment so that we have the most information when making that decision.<sup>6</sup> When you are a specialist and only write requirements, you might end up having written some of the requirements way too early in advance as a result of not having anything else to do. Thus we would want whoever is writing requirements to be able to work in other areas during timeslots where it is not appropriate to be writing requirements. Whoever writes requirements may be able to assist in other areas too such as testing or documentation. Someone who is specialized in UX design might also need to be able to help out in other areas such as documentation or testing for the same reasons.<sup>4</sup>. So for non-software engineers we clearly want generalizing-specialists.<sup>2,3,4,5,7,8</sup> For software engineers, the answer is not immediately obvious because there will be multiple people in that role which could promote specialization a bit more. Let's see though.

### Specialist or Generalizing-Specialist for software engineers?
Before we begin, lets assume your team is a [feature team](https://amcneil36.github.io/blogs/feature-teams-vs-component-teams-which-one-is-better?fbclid=IwAR2fLNiQSMdLTEkPeaXJBKoeB3mw05ifEtBWJyGiJgTXJmKGmPOALOB_rwo) that does front-end and back-end since that is the type of team you should be on. What happens when all software engineers are specialized and only work in their expertise? You might have front-end developers who only do front-end and back-end developers that only do back-end. It will almost certainly be the case that the front-end code for a release is finished way before the back-end code or vice-versa. This is no good as we can't release until both front-end and back-end are done. Even if the front-end and back-end layer somehow are completed at the same time, we still don't want one layer getting way ahead of the other at any point of time in the middle of working on the project either. When one layer gets way ahead of the other layer, there becomes a lot of uncertainty about how one layer will communicate with the other layer. The layers are no longer being fully integrated together. They are only being partially integrated because one layer isn't keeping up with the other layer. This is no good. If front-end starts to get way ahead of back-end, then we need someone working on the front-end to be able to help with the back-end.

Lastly, scrum asks that we produce a potentially shippable product increment at the end of each sprint.<sup>9</sup> This generally requires changes in both front-end and back-end and has the layers fully integrated by the end of the sprint.<sup>10</sup> This produces some sort of fully complete functionality that can be demo'd at the end of the sprint. Having a team of only specialists decreases the chances of producing a potentially shippable product increment at the end of the sprint because if one struggling area is needing help to produce the potentially shippable product increment, no one is able to step up and help. So for software engineers, we want generalizing-specialists as well.<sup>2,3,4,5,7,8,11</sup>

### But what about having a specialist that is allocated to multiple teams?
Part of the argument in favor of generalizing-specialists as opposed to specialists is that someone may have less than 40 hours of work to do for a week if they are only allowed to work in their specialty. This is one reason why the UX designer in the example above was recommended to be a generalizing-specialist as opposed to a specialist. But what if we let the UX designer be a specialist and have this UX designer allocated to multiple teams so that this UX designer could be fully utilized for UX design? Kenneth Rubin and Sriram Narayan say that this route is worse than having someone be fully allocated to a team as a generalizing-specialist.<sup>4,5</sup> Having someone allocated to multiple teams will decrease quality due to context switching and create bottle necks. For example, what if both teams need the UX designer at the same time?<sup>4,5</sup>

### Honorable mention
I'm going to assign an option to second place and it is not going to be a team of all specialists or a team of all generalists. It will actually be a new option. This is a team of generalists and specialists. This allows for some of the team to prioritize working in areas they are good at while still having others who can step up and remove bottle necks to help produce a potentially shippable product increment. There are two main reasons I prefer the generalizing-specialists route over the honorable mention idea. One reason is that having an understanding of other areas can help you in your own area. Another is that I feel the people who are strictly specialists end up with a skill set that becomes stagnant and difficult to change later down the road if demand or personal interests change.

### Conclusion
I would consider having a team of generalizing-specialists being the best route while having a team containing both specialists and generalists being at a close second. However, going with the generalist plus specialist route is better if that would suit the passion of team members more since having passionate teammates will improve performance. I consider all other options significantly worse.

## Sources
1. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.  
2. Craig Larman and Bas Vodde. Practices for Scaling Lean & Agile Development: Large, Multisite, and Offshore Product Development with Large-Scale Scrum. Addison-Wesley, 2012. 
3. Craig Larman and BasVodde. Scaling Lean & Agile Development: Thinking and Organizational Tools for Large-Scale Scrum. Addison-Wesley, 2008.  
4. Narayan, Sriram. Agile IT Organization Design. Pearson Education, 2015.  
5. Rubin, Kenneth. Essential Scrum: A Practical Guide To The Most Popular Agile Process. Addison-Wesley, 2013.
6. Shore, James and Warden, Shane. The Art of Agile Development. O'Reilly Media, 2008.  
7. Gregory, Janet and Crispin, Lisa. More Agile Testing. Addison-Wesley, 2014.  
8. Kim, Gene, et al. The DevOps Handbook: How to Create World-Class Agility, Reliability, and Security in Technology Organizations. IT Revolution, 2016.  
9. Cohn, Mike. Succeeding With Agile: Software Development Using Scrum. Addison-Wesley, 2013.  
10. Craig Larman and Bas Vodde. Practices for Scaling Lean & Agile Development: Large, Multisite, and Offshore Product Development with Large-Scale Scrum. Addison-Wesley, 2012.  
11. Appelo, Jurgen. Management 3.0: Leading Agile Developers, Developing Agile Leaders. Addison-Wesley, 2011  
