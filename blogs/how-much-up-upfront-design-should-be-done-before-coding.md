## How much upfront design should be done before coding?
Suppose we are ready to start working on a new user story. How much upfront design should we do before starting to code up this user story? Let's talk about a couple different options and then discuss which one makes the most sense.

### Some options for how much design to do before codingn
#### Big upfront design before coding
Some teams will do big design up front where they try to do all of the high-level and low-level design before coding. In this situation, they are trying to 'get it right' the first time in order to avoid rework. Many teams that take this approach will "freeze" the design once it is approved. This means that once the design is approved, the person doing the coding has to code up precisely to what the design indicates and has no freedom to code up the task in a slightly different way. The only exception would be if the person who codes it up happens to see that it won't be possible to get the code working with that design. At that point, they might do some more design and have it reviewed by teammates prior to continuing the code. 

Other teams that do big upfront design prior to coding will not freeze the design. They would still do high and low level design prior to coding but they will have the freedom to refactor the code to as they go such that the code might not eventually go on to completely use the low level design that was originally planned.

#### No upfront design before coding
Some teams will do no design before coding. They just jump into coding, get something working, and then refactor. Depending on how much time is spent on refactoring, this approach doesn't necessarily result in a worse design.

#### "Just Enough" upfront design before coding
Others will do what is referred to as "just enough design upfront." In this scenario, someone would do only enough design to feel comfortable about the problem they are trying to solve and then they would start coding. After they get something working, they can refactor. There could be various interpretations of what "just enough" design means but it generally entails to doing only high level design before coding. 

### Which option should we go with?
#### The problem with freezing the design
The problem with freezing the design is that it puts a lot of pressure on getting the design right the first time.<sup>a</sup> If the design we come up with is sub-optimal, we are stuck with it because the design is frozen. This generally results in overly complex code with unused functionality.<sup>b</sup> As a result, we should not be freezing the design.

#### Trying to do big upfront design before coding is difficult
Trying to do big upfront design before coding is very hard to do. We cannot create a complete design upfront by simply working longer and harder.<sup>c</sup> When brainstoroming the low-level design, our thinking is full of many holes.<sup>a</sup> We might think that things behave a certain way based on our understanding of their documentation but we find out that they behave differently after getting feedback from an integration test. As a result, it turns out that as we code up a problem and get feedback from our tests, we learn more about the problem we are trying to solve. So we will need to let some of our design emerge after the coding has already started.<sup>c</sup> 

#### Doing no upfront design before coding will result in too much time spent doing rework
It is correct to understand that we do not need to do all of the upfront design before coding since some of our design can only emerge after coding has started. However, trying to do no design upfront design before coding will cause there to be so much time spent doing rework. Blindly jumping in and coding will often times make it take longer to get something working since we may not have even thought enough about the problem. The time spent refactoring will also be quite big as a result of this.

#### And the winner is...
When it comes to how much upfront design we should do before coding, the answer is "just enough."<sup>a, b, c, d,  This approach has the correct balance between doing enough design to understand the problem and not needing to spend too much time on rework. This balance results in the fastest velocity. I'd also argue that this results in the best design as well since teams doing big upfront design prior to coding tens to be more hesitant to refactor since they have already spent so much time doing the design. It could be entirely possible that doing big design up front was the best approach to take in the past.<sup>d</sup> With automated testing frameworks not being widely available at the early stages of software development, refactoring was less feasilble. This made it more important to try to get the design right the first time. But in today's world, we should take the approach of doing just enough design before coding and let the low level design emerge via refactoring.

## Sources
a. Fowler, Martin and Beck, Kent. Refactoring: Improving the Design of Existing Code. Addison-Wesley, 1999.  
b. Sterling, Christ and Barton, Brent. Managing Software Debt. Addison-Wesley, 2011.  
c. Rubin, Kenneth. Essential Scrum: A Practical Guide To The Most Popular Agile Process. Addison-Wesley, 2013.  
d. Cohn, Mike. Succeeding With Agile: Software Development Using Scrum. Addison-Wesley, 2013.  
e. Beck, Kent. Test Driven Development By Example. 2002  
f. Bain, Scott. Emergent Design: The Evolutionary Nature of Professional Software Development. Addison-Wesley,  2008.  
g. Beck, Kent. Extreme Programming Explained, Embrace Change . Addison-Wesley, 2012.  
h. Agile IT Organization Design, For Digital Transformation and Continuous Delivery – Sriram Narayan  
i. Accelerate, The Science of Lean Software and DevOps: Building and Scaling High Performing Technology Organizations – Forsgren PhD, Jez Humble, Gene Kim  
j. Cohn, Mike. User Stories Applied For Agile Sowftware Development. Addison-Wesley, 2004.

