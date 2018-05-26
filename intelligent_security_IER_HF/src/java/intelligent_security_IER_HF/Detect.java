// Internal action code for project intelligent_security_IER_HF

package intelligent_security_IER_HF;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

@SuppressWarnings("unused")
public class Detect extends DefaultInternalAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'intelligent_security_IER_HF.Detect'");
        if (true) { // just to show how to throw another kind of exception
        	
        }

        // everything ok, so returns true
        return true;
    }
}
