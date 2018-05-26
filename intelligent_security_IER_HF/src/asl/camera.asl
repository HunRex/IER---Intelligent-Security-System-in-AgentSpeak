// Agent camera in project intelligent_security_IER_HF

/* Initial beliefs and rules */
face(Dir).
turn(Degree).
position(Self, X,Y).

/* Initial goals */

!searchBurgler.

/* Plans */

+!searchBurgler : not see(Burg) <-// .print("keresem a betörõt");
									?face(Dir);
									?turn(Degree);
									?position(self, X, Y);
									camera_search(X, Y, Dir, Degree);
									!searchBurgler.
									
+!searchBurgler : see(Burg) <- 
									!followBurgler.
									
+!followBurgler: see(Burg) <- //.print("I see YOU");
							  ?position(burg, X, Y);
							  .send(guard, tell, burglerat(X,Y));
							  stay(there); //do nothing for now but we want to wait 200 ms so others can work
							  !followBurgler.
							  
							  
+!followBurgler : not see(Burg) <- ?position(burg, X, Y);
									followburgler(X, Y);
									!checksuccess.
									
+!checksuccess : not see(Burg) <-   //.print("nem sikerult kovetnem");
									!searchBurgler.
									
+!checksuccess :  see(Burg) <-     // .print("sikerült követnem");
									.send(guard, tell, burglerat(X,Y));
									!followBurgler.
																	