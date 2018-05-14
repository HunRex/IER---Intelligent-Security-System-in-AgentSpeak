// Agent dog in project intelligent_security_IER_HF

/* Initial beliefs and rules */


/* Initial goals */

!signalRobber.

/* Plans */

+!signalRobber: true <- .print("start: ").

+name(N): N<2 <- .print("Silence :", N).
+name(N): N>=2 <- .print("Woof :", N).

//+hello[source(A)] <- .print("Woooof,( I received 'hello' from )",A); 
//.send(guard, tell, hello).
