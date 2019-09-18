## How much upfront design should be done before coding?
I have seen teams with a large amount of variance in the amount of design they do upfront before coding. Let's talk about a couple different options and then discuss which one makes the most sense.  Some teams will do no design before coding. They just jump into coding, get something working, and then refactor. Others will do what is referred to more as "just enough design upfront" where they might do just enough high-level design before coding. After they get something working, they would refactor. To some degree, this is a question of spending more time doing design and less time doing refactoring versus spending less time doing design and more time doing refactoring. Lets take a look at these options individually.

### Big upfront design before coding
Some teams will do big design up front where they try to do all of the high-level and low-level design before coding. In this situation, they are trying to 'get it right' the first time in order to avoid rework. Many teams that take this approach will "freeze" the design once it is approved. This means that once the design is approved, the person doing the coding has to code up precisely to what the design indicates and has no freedom to code up the task in a slightly different way. The only exception would be if the person who codes it up happens to see that it won't be possible to get the code working with that design. At that point, they might do some more design and have it reviewed by teammates prior to continuing the code. 

Other teams that do big upfront design prior to coding will not freeze the design. They would still do high and low level design prior to coding but they will have the freedom to refactor the code to as they go such that the code might not eventually go on to completely use the low level design that was originally planned.

### No upfront design before coding





The problem with freezing the interface is that it puts a lot of pressure on getting the design right the first time.<sup>a</sup> If the design we come up with is sub-optimal, we are stuck with it because the design is frozen. As a result, this typically 


## Sources
a. Fowler, Martin and Beck, Kent. Refactoring: Improving the Design of Existing Code. Addison-Wesley, 1999.
