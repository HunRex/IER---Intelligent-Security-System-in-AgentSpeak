
import static jason.asSyntax.ASSyntax.createLiteral;
import static jason.asSyntax.ASSyntax.createNumber;

import jason.asSyntax.Literal;
import jason.environment.Environment;

public class SecurityEnvironment extends Environment {

	public SecurityEnvironment() {
		final Literal v = createLiteral("name", createNumber(-2));
		System.out.println(v);
		System.out.println("variable: " + Literal.parseLiteral("x(10)"));
		addPercept("dog", v);
	}

	public void setup() {
	}

}
