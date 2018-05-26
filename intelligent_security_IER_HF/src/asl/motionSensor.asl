// Agent motionSensor in project intelligent_security_IER_HF

/* Initial beliefs and rules */

pos(something, -1, -1).

/* Initial goals */


!detect.  //elsõdleges cél a betörõ észlelése

/* Plans */


+!detect: not something(inside) <- ?pos(something, X, Y);
									detectMotion(X, Y);
									!detect.

+!detect :something(inside)  <- ?pos(something, X, Y)
								.send(guard, tell, somethingat(X,Y));
								detectMotion(X, Y);
								!detect.