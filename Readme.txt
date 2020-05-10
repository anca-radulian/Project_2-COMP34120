Our approach and details about the implementation and results can be found in our group's journal.

Leader classes:

The archive contains the following leaders to be run:
CaesarLeader.class which is implemented using a linear regression
ForgettingFactor.class which is an extension of CaesarLeader containing the modified window approach (ForgetFactor_Results folder contains the results for this implementation).
PolynomialLeader.class which is implemented using polynomial regression and a moving window approach (Polynomial_Results folder contains the results for this implementation). Inside the PolynomialLeader we used Java Matrix Package (JAMA) and PolynomialRegression class to obtain the regression coefficients.

In terms of total Pay-off, with MK1 and MK2, CaesarLeader implementation obtains the best results out of the three implementations:
With MK1 as follower, it obtains 17.5571842
With MK2 as follower, it obtains 16.9564495

The PolynomialLeader obtains the best results out of the three implementations with MK3:
With MK3 as follower, it obtains 19.48833465

The results obtained using the Forgetting Factor Leader are smaller than the ones from CaesarLeader and PolynomialLeader.

The details regarding the approach used for each Leader are explained further in the Group Journal on Blackboard.

How to run:

To enable RMI registration run:
rmiregistry &

To run the GUI of the platform run:
java -classpath poi-3.7-20101029.jar: -Djava.rmi.server.hostname=127.0.0.1 comp34120.ex2.Main &

To run Caesar Leader:
java -Djava.rmi.server.hostname=127.0.0.1 CaesarLeader &

To run ForgettingFactor:
java -Djava.rmi.server.hostname=127.0.0.1 ForgettingFactor &

To run Polynomial Leader:
java -classpath jama-1.0.3.jar: -Djava.rmi.server.hostname=127.0.0.1 PolynomialLeader &

If possible, we would prefer to run CaesarLeader with MK1 and MK2, and PolynomialLeader with MK3. But in case you only run one leader, then run CaesarLeader with all three followers.
