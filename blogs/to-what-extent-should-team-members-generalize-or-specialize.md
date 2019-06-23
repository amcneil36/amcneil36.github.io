# To what extent should team members generalize or specialize?

Lets imagine we have a scrum team containing some software engineers, a business analyst, and a tester. Maybe the team contains a few other roles too or maybe it doesn't. The answer to this question is going to be the same in either case so do not worry if this example mirrors the set up on your team.

Lets discuss three different options that might represent a team members skill set.

**Generalist:** this would be a team member that is equally good in many areas.

**Specialist:** this would be a team member that is extremely strong in one or two areas and does not do any work outside of their specialization.

**Generalizing-Specialist:** this would be a team member that has in-depth skills in one or two areas and broad skills in other areas. This person would attempt to primarily work in their specializtion but would be more than willing to work in an area outside of their specialization if needed. These people are sometimes called T-shaped people.

## Generalist
### pros
It is very easy to do sprint planning because you can assign the tasks to just about anyone. Secondly, if one area of the project is having some issues and needs extra attention, anyone would be able to step up and do a reasonable job in helping out in that area. Some sprints might require slightly different skill sets than others and for a team of generlists, that's not a problem. Someone could also be out of office and that would be less likely to cause any issues for a team of generalists.

### cons
People need to spend a lot of time doing spikes since there are many areas they need to be adequate in. Onboarding new people will take quite a bit longer. Lastly, people only work in areas they are medium at and don't work in any area they are truly great at.

## Specialist
## pros
Team members are only working in areas that they are specialized in so they don't need near as much time to be onboarded. After getting onboarded, team members also don't need near as much time doing spikes and other types of studying. Team members would also be more likely to understand best practices in the area(s) they are working in since they only work in areas they are specialized in.

## cons
This setup makes it difficult to remove bottlenecks. If something is going wrong in a particular area, we might want to have more than one person help out in that area. If people are all specialists, we may not be able to have more than one person to help out so having specialists makes bottlenecks take longer to remove. Secondly, some sprints might contain works that require a little bit different skill sets than other sprints. This means that in some sprints, our specialist set up aligns well with the work that needs to be done but in other sprints the specialist set up does not align well with the work that needs to be done. Lastly, when a specialist is out of office, it might be hard for someone else to cover their area.

## Generalizing-Specialist
## pros
A majority of the work being done by a team member is in an area that they are specialized in. This set up also still has the ability for multiple people to step up and remove bottlenecks since they contain broad skills in many areas.

## cons
Team members will sometimes work in areas that they have broad skills in which might be a scenario where best practices are followed quite as well in comparison to having people only work in areas they are specialized in. Secondly, it could also be the case that some sprints result in requiring skills that align well with the generalizing-specialist set up and other sprints require skills that do not align well with the generalizing-specialist setup.

## So which option is the best?
I'm going to go ahead and rule out the option of there being a team of generalists since that is the most obvious one to rule out. If there is no control over who works on what, then having a team of generalists makes sense. But because people can choose what they work on, it makes sense to allow specialization and to have some sort of preference towards having people work in their specialization. The questions is, should they specialize in just one or two areas and only work in those areas? Or should they have broad skills in many areas (in addition to specializing in a few areas) and attempt to work in a wider variety of areas?

There is one concept that is incredibly important that is going to ultimately decide which way to go between specialists and generalizing-specialists. Certain types of tasks are significantly more important at certain points in time and significantly less important at other points in time. Fleshing out requirements is only important right before work is about to begin by others to introduce the functionality to satisfy the requirements. Attempting to flesh out requirements far in advance represents waste. Thus we would want whoever is writing requirements to be able to work in other areas during timeslots where it is not appropriate to be writing requirements.

What happens when all software engineers are specialized and only work in their expertise? You might have front-end developers who only do front-end and back-end developers that only do back-end. It will almost certainly be the case that the front-end code for a release is finished way before the back-end code or vice-versa. This is no good as we can't release until both front-end and back-end are done. However, he concept explained above of having specific timeslots where certain types of tasks are significantly more important than others still applies. Even if the front-end and back-end layer somehow are completed at the same time, we still don't want one layer getting way ahead of the other at any point of time. Scrum asks that we produce a potentially shippable product increment at the end of each sprint which typically requires changes in front-end and back-end. What if the back-end is struggling and possibly preventing us from producing a potentially shippable product increment? We would expect some front-end developers to be able to help out in the back-end so we can still produce our potentially shippable product increment. 

---------------------------
note this applies to all teammates
at some times, certain areas of the project are more important (we need back-end help)
if your a front-end specialist, you would end up continuing with front-end tasks and get ahead of the back-end code
whoever is writing requiremetns needs to be able to do other stuff because getting ahead on requirements is no good
same for the tester-trying to write test cases for stuff that is not near ready to be started is waste
make decisions at the last responsible moment
what about having a specialist that works on multiple teams?
dba specialization
group work to fit the teams instead of grouping teams to fit the work
