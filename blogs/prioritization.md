## Prioritization

Suppose you are on a team that is working on a product. Let's say that your team has a backlog of user stories. It is the responsibility of the product owner to determine which user stories to work on in which order. However, the product owner will need some help from the development team in order to feel more certain about how to do the prioritization. How does the product owner do this prioritization? There are a couple of factors that the product owner will take into account.

### Business value

The product owner has the domain knowledge and understands the business value that each user story gives. One thing the product owner might do is to take each user story and assign points to each story based on business value. Perhaps one user story is considered a 5 business value points and another user story is 10 business value points. This would mean that the user story that is 10 business value points is twice as valuable as the 5. The product owner could then look at each user story and prioritize based on which user stories have the highest business value points.

There are some problems with this. One problem is that some of these user stories might not actually add any value alone. User stories should be broken down very small such that you can complete multiple of them in a sprint. Each user story adds a small amount of functionality. But it can be the case that this small amount of functionality alone is not useful to the customer unless another small amount of functionality is also added. Suppose user story A cannot add business value unless user story B is also done and vice versa. Neither of these user stories individually add business value so it doesn't make sense to put business value points on these. We also want to make sure that when we are prioritzing, we do user stories A and B consecutively. We don't want to complete user story A, complete user story C, release user story C, complete user story B, then release user story A and B. We want to be working on stories for our next release at any point in time. What we can do here is place the business value points at the epic level. Perhaps there is an epic that contains user stories A and B. When putting the business value points at the epic level, we now have something that independently adds value. We can then choose when to work on the epic. Once we get around to working on the epic, we can do user stories A and B consecutively. 

Now, the product owner is able to look at the business value points of the work that needs to be done. The product owner could go off of these business value points to determine which work to work on in which order. However, going off of business value points alone is not enough information for the product owner to be able to prioritize work.

### Estimates
Suppose a user story A has twice as much business value as user story B. We should do user story A first, right? Not necessarily. What if user story A is predicted to take four times longer than user story B? We may want to do user story B first in that case. So the product owner needs to have some knowledge about how long a user story might take in order to prioritize it. Which people are most likely going to have the best guess about how long the work will take? The people doing the work. This will be the developers.

So the developers can take a look at the user stories and assign story points to the work based on how long they think the work will take. If story A is predicted to take twice as long as story B, then that means that story A is going to be twice as many story points as story B.

Now that the stories are story pointed, the product owner is a lot better equipped to prioritize the stories. One thing the product owner might try to do is come up with some formula for prioritizing user stories such as business value points divided by story points. This would be an OK approach. However, suppose you have the follow situation:
* user story A: 4 business value points, 8 story points
* user story B: 2 business value points, 4 story points
* user story C: 2 business value points, 4 story points
In this scenario, the business value points divided by the story points comes out to the same number. However, it looks like we would want to do user stories B and C before user story A since user stories B and C lead to quicker releases. Suppose user stories B and C might take one month each and user story A might take two months. Let's compare the following two scenarios:
1. We complete user story B then release it, complete user story C then release it, complete user story A then release it
1. We complete usre story A then release it, complete user story B then release it, complete user story B then release it

In both of these scenarios, we have the same amount of work done in a 4 month time period (everything released). However, in scenario 2, we already have something released after 1 month whereas in scenario 1, nothing gets released until the second month. So going off business value points divided by story points is sub-optimal. So there is still some benefit to favoring shorter user stories, even if the business value points divided by the story points are equal. Regardless of whether or not you come up with a formula, having the business value points and story point estimates help out a lot in prioritization.

### Risk
