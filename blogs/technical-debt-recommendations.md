## Technical Debt Recommendations
Technical debt is any shortcut that a team chooses to do now that will impede future development if left undone.<sup>1</sup> Some people prefer to tackle technical debt as they work on stories, only eliminating technical debt that will help with their story. Others will take a different approach where they tackle technical debt by scheduling periods of time where the only focus is to tackle technical debt. Dealing with technical debt is a conversation that needs to be had between software engineers and business people. I'll give my recommendations for how to go about tackling technical debt as well as how software engineers and business people can communicate together to come up with a plan for tackling technical debt.

## Tackling technical debt as you work on stories
In this approach, we do not schedule a task for eliminating technical debt. We work on our stories just like normal but we eliminate technical debt whenever it impedes our task. Suppose there is a poorly named variable in the code called 'aiekdj.' Let's even say that we are aware that this variable is in the code somewhere. If our user story we are working on has no affiliation with this variable, then we will complete our user story without having changed the variable name. If the user story we are working on depends on this variable somehow, we will change the variable name to be more descriptive. 

This approach should be your default way of eliminating technical debt. With this approach, you immediately benefit every time you eliminate technical debt. The problem with scheduling time periods for technical debt tasks where all you do is eliminate technical debt is that you don't get any benefit until you go on to do a task that benefits from having that technical debt removed. If you eliminate some technical debt but never end up doing a task that benefits from it, then you have wasted your time. Thus, scheduling technical debt tasks poses significant risk that removing the technical debt will never go on to pay for itself. Tackling technical debt as you work on stories still has the chance of not being not worth the investment but the chance of that is much lower.

## Scheduling technical debt tasks
In this approach, we schedule tasks for eliminating technical debt. The only purpose of the task is to eliminate technical debt. No immediate business value is achieved. The goal is that we will eventually go on to tasks at a later point in time that will benefit from having eliminated this technical debt. 

From the previous section, I hinted that tackling technical debt while you work on stories is the better approach. In most technical debt scenarios, you can remove a little bit of tech debt by doing a little bit of work which is how the tackling tech debt as you work on stories approach works best. However, in a few scenarios, the tech debt is set up in such a way that you cannot remove a little bit of tech debt by doing a little bit of work. The only way to even remove any tech debt might be to remove a lot of the tech debt by doing a lot of work. Perhaps you have to uplift your project to a different framework or technology and the framework or technology is set up in such a way where it either all goes through or none of it goes through. Instead of it being possible to just uplift a few files at a time and the project works with only a few files uplifted, maybe the framework or technology requires all files to be uplifted for the project to build. This would be an example where scheduling a technical debt task is the better of the two approaches. 

## Doing an approach that reduces technical debt is not always the wrong idea
I used to think that doing something that introduces technical debt was always the wrong approach. It felt obvious to me that our team would complete the most work in the long term by never introducing any tech debt. I was of the opinion that planning in such a way that we complete the most amount of work in the long term was the way to go. I eventually found out this wasn't quite the right idea.

When thinking about doing something that will add technical debt, we need to answer the following question:

>Should we take some shortcuts that will get our feature to market earlier but will slow down some future releases? 

In most scenarios, the answer is no. Sometimes the answer will be yes.

Suppose that your company is working on a feature and a competitor of yours is working on a similar feature. Your stakeholders consider it critical that your feature makes it to market before your competitor's feature. You could find yourself in a situation where the increase in revenue generated from taking shortcuts to deliver early more than offsets the additional cost that is incurred from introducing tech debt.

Another scenario where introducing tech debt might make sense is when you are behind schedule on a project with a strict deadline set by the government where missing the deadline results in your clients getting fined a large sum. Taking a shortcut that introduces technical debt is not supposed to be the default response to being behind schedule. That is where reducing scope comes into play. 

## Sources
1. Sterling, Christ and Barton, Brent. Managing Software Debt. Addison-Wesley, 2011.
