// Agent guard in project intelligent_security_IER_HF

/* Initial beliefs and rules */

/* Initial goals */

//!start.
//!catchRobber.

/* Plans */

//+!start : true <- .send(dog , tell, hello).
+burglerat(X, Y)[source(A)] : not burgler(inside) <- .print("I see Ya(",X, ":", Y,") from ", A);
													 scare_burgler(X, Y);
													 !scareBurgler.

+!scareBurgler: scare(possible) <- ?alarm(A);
								  -scare(possible);
								  .send(A, tell, scare).
								  
+!scareBurgler: not scare(possible) <- .print("nem volt mar ott").
								  

+inside : true <- +burgler(inside).

+outside : true <- -burgler(inside).