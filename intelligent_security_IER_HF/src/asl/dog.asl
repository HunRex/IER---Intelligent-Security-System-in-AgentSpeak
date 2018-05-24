// Agent dog in project intelligent_security_IER_HF

/* Initial beliefs and rules */
 //burgler(outside).

/* Initial goals */

!checkBurgler.

/* Plans */



 
+!checkBurgler: burgler(outside) <- .print("nincs bent")
						next(step);
						!checkBurgler.	
						
+!checkBurgler: not burgler(outside) <- .print("bent van rossz")
						!signalburgler.										
				
@lg[atomic]
+burgler(inside) : true
   <- !signalburgler.
   

					
+!signalburgler : burgler(inside) <- .print("bent van")
					next(step);
					!signalburgler.
					
+!signalburgler :not burgler(inside) <- 
					!checkBurgler.
				

//+hello[source(A)] <- .print("Woooof,( I received 'hello' from )",A); 
//.send(guard, tell, hello).
