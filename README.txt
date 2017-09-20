JAVA language was used to code the decision tree and obtain the pre-pruned and post-prune results.

To Compile form command line
	javac *.java

To run from command line(make sure that all the datasets and the class files are in the same folder or give the exact path of the data sets)(give four arguments training_set.csv, validation_set.csv, test_set.csv, Pruning Factor )
	java DecisionTree training_set.csv validation_set.csv test_set.csv 0.2

** Assumptions **
--If a noisy data is encountered then the class classification in the tree has no value i.e it mean a don't care value(?).
--we have only considered the internal nodes(excluding leaf nodes) for selecting the total number of prune nodes.

I have accomplished in creating Decision tree and pruned it for better results.

Observations:
- We have observed that with the help of training data, we can built a decision tree which would work with 100% accuracy on training data and would be good enough for the test data. 
- If we encounter noisy data in the training data the accuracy of the decision tree decreases. 
- We have observed that if the decision tree is pruned its accuracy increases or decreases depending on the pruning factor and nodes pruned.  
- It is better to prune nodes which are close to the root nodes and to avoid pruning root nodes. 
- While pruning it is important to keep in mind to never prune the leaf nodes.
