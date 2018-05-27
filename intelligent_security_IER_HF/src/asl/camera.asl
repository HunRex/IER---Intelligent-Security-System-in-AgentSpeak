// Agent camera in project intelligent_security_IER_HF

/* Initial beliefs and rules */

/* Initial goals */

!searchBurgler.

/* Plans */

+!searchBurgler : not see(Burg) <-
									camera_search(search);
									!searchBurgler.
									
+!searchBurgler : see(Burg) <- 
									!followBurgler.
									
+!followBurgler: see(Burg) <- 
							  ?position(burg, X, Y);
							  +position(last, X, Y);
							  .send(guard, tell, burglerat(X,Y));
							  stay(there); //do nothing for now but we want to wait 200 ms so others can work
							  !followBurgler.
							  
							  
+!followBurgler : not see(Burg) <- ?position(last, X, Y);
									followburgler(X, Y);
									!checksuccess.
									
+!checksuccess : not see(Burg) <-   
									!searchBurgler.
									
+!checksuccess :  see(Burg) <-     
									!followBurgler.
																	