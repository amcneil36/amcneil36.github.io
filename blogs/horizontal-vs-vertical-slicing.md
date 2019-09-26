## Horizontal vs Vertical Slicing

In order to complete a feature for a customer, code changes must be made in one or more components. Most of the time, there will be more than one components. The term 'component' just refers to a re-usable asset that is used to complete a portion of a feature. The front-end and back-end are both considered components. Within the back-end there could be multiple components such as a data retrieval component and a data insertion component. So the term 'component' could be used at different levels of granularity.

When planning out a project, there are two different approaches that teams might take to plan out the order of the work to be done. One approach is **horizontal slicing** where a team will attempt to complete all work in one component before moving on to another component. This might involve trying to do the whole back-end before starting the back-end. It might involve trying to do the whole front-end before starting the back-end. Or, it might involve completing one component in the back-end and then completing one component in the front-end followed by returning to the back-end. The second approach is **vertical slicing** where a team will code up a portion of multiple components at a time in order to produce a small amount of end to end functionality.

Suppose you are working on a feature that needs front-end and back-end code changes. Perhaps the front-end has multiple components and the back-end has multiple components. Should we do horizontal slicing or vertical slicing? Let's look at one at a time to see how it would look.

### Horizontal Slicing
As stated previously, horizontal slicing involves completing all code in one component before moving on to another component. We will start off by discussing a simple example.

#### Coding all of the back-end followed by coding all of the front-end
In this scenario, we will start off by coding up all of the back-end for the next release. When it comes to demo'ing we won't get much feedback because there is no front-end. You will be half way through the project with no working software to show in the demo. Stakeholders will become concerned that the project is not on target. Once the front-end starts coming along, you will finally start getting feedback at the demos since there will be usable functionality that the customer can see in a demo. However, we don't want to wait until half way through the project to start getting feedback at the demo. The later you get feedback, the more code that gets tossed in the event that you adjust the code to the feedback. It is also more difficult to make changes later in plans since the release date is closer.

Another issue with doing all of the back-end before all of the front-end is that the layers may not communicate together when you finally get around to integrating them. As you code up the back-end, you will need to make some assumptions about which information the front-end will be able to pass to the back-end. If these assumptions turn out to be wrong, you won't find out until after all of the back-end code has been written. This creates for more code getting tossed when there are integration issues.

#### Coding all of the front-end before all of the back-end
In this scenario, we will start off by coding up all of the front-end for the next release. When we demo our code, we should be able to get a lot of feedback in terms of how everything is coming along. One downside is that more work will appear done than what is actually done since the call to the back-end will be stubbed out. This could confuse the customer. However, this downside is manageable. After the front-end is done, we would then go on to do all of the back-end. We might not get much feedback in the last half of the project since that portion is just back-end but it's better to be getting your feedback early than late. So in terms of getting feedback in a demo, this approach is not so bad. One thing to keep in mind is that the front-end calls into the back-end but the back-end doesn't call into the front-end. So unlike the previous scenario, this time we migth have to make some minor front-end changes as the back-end is getting coded up so that the front-end is getting updated to call into the back-end.

Unfortunately, this approach suffers the same issue as the previous approach in that the layers are not being frequently integrated together. As we code up the front-end, there are some assumptions that we have to make about how the back-end will look. When we integrate this code later, we may find these assumptions were wrong. Perhaps there are some unforeseen performance issues when we finally integrate everything. As a result, we might need to change our code to improve the performance. The later we integrate, the more code that gets tossed.

### Vertical Slicing
In this scenario, your team would make code changes in a little bit of each component each sprint. The team would produce a small amount of fully integrated usable software each sprint. This is because most working software requires code changes in multiple components. The team would be able to get feedback from customers every sprint since working software is delivered frequently. If there are any issues discovered when integrating components, these issues will be discovered early since we are frequently integrating. The customer sees a small amount of usable functionality early on in the project. Each sprint, there is slightly more usable functionality.







----------------------------------------------------------------------------------
Horizontal vs vertical slicing
	• Explain difference between horizontal and vertical slicing
	• Horizontal slicing examples
		○ Do all back-end before all front-end
		- code is not usable because it is stubbed
		○ Do all front-end before all back-end
	• Vertical slicing example
		○ Can talk how some code might be stubbed out
		○ Tracer bullet development
		-code that is working and usable
		-can release when behind schedule
	• Which one should we do?
	• Stategies for going vertical
		○ User stories
