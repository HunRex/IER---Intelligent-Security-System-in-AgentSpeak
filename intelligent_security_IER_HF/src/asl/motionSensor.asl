// Agent motionSensor in project intelligent_security_IER_HF

/* Initial beliefs and rules */

pos(something, -1, -1).

/* Initial goals */


!detect.  //els�dleges c�l a bet�r� �szlel�se

/* Plans */


+!detect: not something(inside) <- ?pos(something, X, Y);
									detectMotion(X, Y);
									!detect.

+!detect :something(inside)  <- ?pos(something, X, Y)
								.send(guard, tell, somethingat(X,Y));
								.print("Something is moving here!");
								detectMotion(X, Y);
								!detect.