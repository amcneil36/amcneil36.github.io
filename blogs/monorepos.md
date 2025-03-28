## Monorepos

### Motivation for monorepos
It is often the case that each company will have code for some feature that can be re-usable for other features. It could be foundational code that hosts the servers that the code runs on. It could be common code that does privacy checks against a logged in user. This common code often gets extracted out into its own repository and is imported by code in other repositories. 
  
Now, imagine that you are working on a new feature in Repository A and you have common code in Repository B. Suppose that you need a slight code change in Repository B in order for you to work on the feature in Repository A. So, you go to Repository B and do a code change in it. As you are doing the code change in Repository B, you aren't able to test it with Repository A. The code in Repository A depends on the code in Repository B but because they are separate repositories, you need the Repository B code change released before you can reference it in Repository B. So you commit the code and release the code from Repository B. You then start working on Repository A and call into your new Repository B code but you find out that there was an edge-case that you missed in Repository B. 
  
Do you see the bottleneck here? You first got slowed down while writing code in Repository B because it wasn't able to be tested in Repository A prior to being released so you felt a little blind as you were coding it up. You then got slowed down more because you had to wait for Repository B to be released after you queued up the code to be committed. What is the solution to this issue? 


### What is a monorepo?  
Some companies do what is called a monorepository or monorepo. A monorepo is basically when you dump all of your code in the same repository. This way, when you update the common code, your other code automatically gets the code change without you having to wait for anything to be released. You can then fully test your code together to validate that it works instead of having to blindly commit something that you couldn't test end to end.

Note that a monorepo doesn't mean that there is only one file or folder in the repo. There will be tons of files and folders for code organization.

### Why don't all companies use monorepos?  
Monorepos are a challenging problem with a lot of complexity. Imagine a situation where all code is in the same repository and all code is built out every time you want to test a code change and every time you want to release the code. This could result in the build process going from minutes individual repos to hours in a monorepo. Also imagine that multiple employees are attempting to merge code to master every minute but it takes an hour to build out all of the code and run all of the tests. How do we get all of the code safely merged to master when people are attempting to merge faster than how long it takes to do the merge? The monorepo has to have a way to allow multiple people to queue up a merge to master at the same time and to have their commits be tested. This is currently the challenge that infra teams solve.
