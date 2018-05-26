// Agent guard in project intelligent_security_IER_HF
	
	/* Initial beliefs and rules */
	
	+position(Self, X,Y).
	
	
	/* Initial goals */
	
	//!start.
	//!catchRobber.
	
	/* Plans */
	
	//+!start : true <- .send(dog , tell, hello).
	+burglerat(X, Y)[source(A)] : not burgler(inside) <- .print("I see Ya(",X, ":", Y,") from ", A);
														 scare_burgler(X, Y);
														 !scareBurgler.
														 
	+burglerat(X, Y)[source(A)] : burgler(inside) & not burgler_caught <- .print("I see Ya(",X, ":", Y,") from ", A);
														 catch_burgler(X, Y);
														 -burglerat(X, Y)[source(A)];.											 
														 
	+burglerat(X, Y)[source(A)] : burgler(inside) & burgler_caught <- 
														.print("Burgler caught");.										 
														 
	
	+somethingat(X, Y)[source(A)] : not burgler(inside) <- .print("I see Ya(",X, ":", Y,") from ", A);
														 scare_burgler(X, Y);
														 !scareBurgler.
														 
	+somethingat(X, Y)[source(A)] : burgler(inside) & not burgler_caught <- .print("I see Ya(",X, ":", Y,") from ", A);
														 catch_burgler(X, Y);
														 .print("I see Ya(",X, ":", Y,") from ", A);
														 -somethingat(X, Y)[source(A)].
														 
 	+somethingat(X, Y)[source(A)] : burgler(inside) & burgler_caught <- 
														.print("Burgler caught");.		
														 
	+!scareBurgler: scare(possible) <- ?alarm(A);
									  -scare(possible);
									  .send(A, tell, scare).
									  
	+!scareBurgler: not scare(possible) <- .print("nem volt mar ott").
									  
	
	+inside[source(A)] : true <- +burgler(inside);
						-outside[source(A)].
	
	+outside[source(A)] : true <- -burgler(inside);
						-inside[source(A)].

