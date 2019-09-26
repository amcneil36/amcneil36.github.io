## Horizontal vs Vertical Slicing

In order to complete a feature for a customer, code changes must be made in one or more components. Most of the time, there will be more than one components. The term 'component' just refers to a re-usable asset that is used to complete a portion of a feature. The front-end and back-end are both considered components. Within the back-end there could be multiple components such as a data retrieval component and a data insertion component. So the term 'component' could be used at different levels of granularity.

When planning out a project, there are two different approaches that teams might take to plan out the order of the work to be done. One approach is **horizontal slicing** where a team will attempt to complete all work in one component before moving on to another component. This might involve trying to do the whole back-end before starting the back-end. It might involve trying to do the whole front-end before starting the back-end. Or, it might involve completing one component in the back-end and then completing one component in the front-end followed by returning to the back-end. The second approach is **vertical slicing** where a team will code up a portion of multiple components at a time in order to produce a small amount of end to end functionality.

Suppose you are working on a feature that needs front-end and back-end code changes. Perhaps the front-end has multiple components and the back-end has multiple components. Should we do horizontal slicing or vertical slicing? Let's look at one at a time to see how it would look.

### Horizontal Slicing
As stated previously, horizontal slicing involves completing all code in one component before moving on to another component. We will start off by discussing a simple example.

#### Coding all of the back-end followed by coding all of the front-end
In this scenario, we will start off by coding up all of the back-end for the next release. When it comes to demo'ing for feedback, we won't have much of anything to show at the demo because there is no front-end. We might end up showing some back-end tests that run








----------------------------------------------------------------------------------
Horizontal vs vertical slicing
	• Explain difference between horizontal and vertical slicing
	• Horizontal slicing examples
		○ Do all back-end before all front-end
		○ Do all front-end before all back-end
	• Vertical slicing example
		○ Can talk how some code might be stubbed out
		○ Tracer bullet development
	• Which one should we do?
	• Stategies for going vertical
		○ User stories
