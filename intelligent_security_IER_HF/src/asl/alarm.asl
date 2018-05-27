// Agent alarm in project intelligent_security_IER_HF

/* Initial beliefs and rules */

/* Initial goals */

/* Plans */


+scare [source(A)]: true<- .print("BEEEEP");
				scare(burgler);
				-scare  [source(A)].