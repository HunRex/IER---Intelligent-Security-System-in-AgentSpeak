// Agent dog in project intelligent_security_IER_HF

/* Initial beliefs and rules */


/* Initial goals */

!checkBurgler.

/* Plans */



 
+!checkBurgler: burgler(outside) <-
						next(step); // waits
					!checkBurgler.
						
+!checkBurgler: not burgler(outside) <- 
						.send(guard, tell, inside);
						!signalburgler.										


					
+!signalburgler : burgler(inside) <- .print("Woof Woof")
					next(step); //waits
					!signalburgler.
					
+!signalburgler :not burgler(inside) <- 
					.send(guard, tell, outside);
					!checkBurgler.
				

