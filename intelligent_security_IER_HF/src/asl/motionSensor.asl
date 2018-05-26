// Agent motionSensor in project intelligent_security_IER_HF

/* Initial beliefs and rules */

position(Self, X,Y).

/* Initial goals */


!detectRobber.  //elsõdleges cél a betörõ észlelése

/* Plans */

+!start : true <- .print("hello world, I am a motion sensor.").
+!detectRobber : true <- .send(camera1, tell, pos(1,1)). // ha észleli a betörõt, elküldi a kameráknak a pozícióját