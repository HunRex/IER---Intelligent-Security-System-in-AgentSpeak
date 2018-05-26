// Agent guard in project intelligent_security_IER_HF
	
	/* Initial beliefs and rules */
	
	position(Self, X,Y).
	
	
	/* Initial goals */
	
	//!start.
	//!catchRobber.
	
	/* Plans */
	
	//+!start : true <- .send(dog , tell, hello).
	+burglerat(X, Y)[source(A)] : not burgler(inside) <- .print("I see Ya(",X, ":", Y,") from ", A);
														 scare_burgler(X, Y);
														 !scareBurgler.
														 
	+burglerat(X, Y)[source(A)] : burgler(inside) <- .print("I see Ya(",X, ":", Y,") from ", A);
														 catch_burgler(X, Y);
														 +pos(burg, X, Y);
														 !catchBurgler(0).													 
														 
	
	+!catchBurgler(C): burgler(inside) & (C < 4) <- ?pos(burgler, X, Y);
										catch_burgler(X, Y);
										.print("Elkapom!");
										.print(C);
										!catchBurgler(C + 1).
									
	+!catchBurgler(C): burgler(inside) & (C >= 4) <- stay(there).
	
	
	+!scareBurgler: scare(possible) <- ?alarm(A);
									  -scare(possible);
									  .send(A, tell, scare).
									  
	+!scareBurgler: not scare(possible) <- .print("nem volt mar ott").
									  
	
	+inside : true <- +burgler(inside).
	
	+outside : true <- -burgler(inside).

