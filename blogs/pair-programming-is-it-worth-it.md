# Pair Programming: is it worth it?

You walk around your office and you see that most developers are sitting by themselves, doing their work. You finally spot a group of two developers working together on the same task on the same computer. In other words, they are pair programming. You stop and think to yourself-we have two people doing work that could be done by one person; wouldn't it be more efficient if we split these people up? Let's first take a look at the benefits of pair programming to see why someone might consider doing pair programming. After that, we will look at the potential downsides and see if pair programming is worth it or not.

## Higher code quality
A study performed by Dr. Laurie Williams in the year of 2000 concluded that pair programming results in 15% fewer defects.<sup>1</sup>. She believes that this is attributed to pairs considering more design alternatives, resulting in a more simple design.

## Knowledge transfer, more understanding of the code
Pair programming has the additional benefit of knowledge transfer.<sup>2</sup> Almost every time I pair program with someone, I learn at least one cool keyboard shortcut or way of using a tool that I was not aware of. Whenever I am learning a new technology, I like to pair with someone who is already familiar with that technology. I will learn significantly faster this way than working on my own. You will also be surprised how often an expert still learns when being paired with a novice. One of the most important questions to ask at the end of the year is how much you improved in the past 365 days. If you did pair programming, you will have improved more than if you did not do pair programming. It is pretty cool to think that using pair programming would help you improve more from year to year than not pair programming.

## Easier planning
It is natural to have some tasks depend on each other when planning.<sup>3</sup> These dependencies make it harder to plan work because work has to be done in a certain order. There is always the chance of someone getting blocked in the middle of the sprint because their next task depends on something that another teammate is doing. With pair programming, we divide the amount of work in progress by two, making it less likely that someone will be blocked. Imagine you have four developers that are not pair programming. You have to have four tasks in progress at a time. It could be the case that there are four tasks remaining in the sprint but one of them depends on another task. Without being allowed to pair, the fourth developer would have to start working on a task that is not in the sprint because he or she is otherwise blocked with nothing to do. With pair programming, the team would only need two tasks in progress at a time so even if one of the four tasks is blocked, each developer can still work on tasks during the sprint. Having less work in progress also makes it easier to understand what is being worked on at any given point in time.  

## Continuous review
Imagine that you are working on a task by yourself and at the start of the task, you make an assumption about how things work. Lets say that the work you do for your task depends on this assumption. Furthermore, lets say that this assumption is wrong but you are not aware of it. After typing up 1,000 lines of code, we upload our code to be reviewed. A teammate of ours observes that we made an incorrect assumption and now this 1,000 lines of code needs to be completely re-written. This is very inefficient. We can improve our efficiency by shortening the feedback loop. We start working on our next task by ourself and this time we upload a code review after 200 lines of code. If it turns out we made an incorrect assumption early on when doing our task, there is less code that has to get tossed. 

## Less merge conflicts


## Sources
1. Williams, L. A. The Collaborative Software Process. PhD Dissertation in Department of Computer Science. Salt Lake City, UT: University of Utah, 2000. 
2. Kim, Gene, et al. The DevOps Handbook: How to Create World-Class Agility, Reliability, and Security in Technology Organizations. IT Revolution, 2016.  
3. Beck, Kent and Fowler, Martin. Planning Extreme Programming. Addison-Wesley, 2004.  
