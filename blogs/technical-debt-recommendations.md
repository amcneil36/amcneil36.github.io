## Technical Debt Recommendations
Technical debt is any shortcut that a team chooses to do now that will impede future development if left undone.<sup>1</sup> Some people prefer to tackle technical debt as they work on stories, only eliminating technical debt that will help with their story. Others will take a different approach where they tackle technical debt by scheduling periods of time where the only focus is to tackle technical debt. Dealing with technical debt is a conversation that needs to be had between software engineers and business people. I'll give my recommendations for how to go about tackling technical debt as well as how software engineers and business people can communicate together to come up with a plan for tackling technical debt.

## Tackling technical debt as you work on stories
In this approach, we do not schedule a task for eliminating technical debt. We work on our stories just like normal but we eliminate technical debt whenever it impedes our task. Suppose there is a poorly named variable in the code called 'aiekdj.' Let's even say that we are aware that this variable is in the code somewhere. If our user story we are working on has no affiliation with this variable, then we will complete our user story without having changed the variable name. If the user story we are working on depends on this variable somehow, we will change the variable name to be more descriptive. 

## Sources
1. Sterling, Christ and Barton, Brent. Managing Software Debt. Addison-Wesley, 2011.
