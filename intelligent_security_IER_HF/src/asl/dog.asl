// Agent dog in project intelligent_security_IER_HF

/* Initial beliefs and rules */
 //burgler(outside).

/* Initial goals */

!checkBurgler.

/* Plans */



 
+!checkBurgler: burgler(outside) <-
						next(step);
					!checkBurgler.
						
+!checkBurgler: not burgler(outside) <- //.print("bent van");
						.send(guard, tell, inside);
						!signalburgler.										
/*				
@lg[atomic]
+burgler(inside) : true
   <- !signalburgler.
   */

					
+!signalburgler : burgler(inside) <- 
					next(step);
					!signalburgler.
					
+!signalburgler :not burgler(inside) <-  //.print("nincs bent");
					.send(guard, tell, outside);
					!checkBurgler.
				

//+hello[source(A)] <- .print("Woooof,( I received 'hello' from )",A); 
//.send(guard, tell, hello).
