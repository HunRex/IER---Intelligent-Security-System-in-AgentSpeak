// Agent camera in project intelligent_security_IER_HF

/* Initial beliefs and rules */
face(Dir).
turn(Degree).
position(Self, X,Y).

/* Initial goals */

!searchBurgler.

/* Plans */

+!searchBurgler : not see(Burg) <- .print("keresem a betörõt");
									?face(Dir);
									?turn(Degree);
									?position(self, X, Y);
									camera_search(X, Y, Dir, Degree);
									!searchBurgler.
