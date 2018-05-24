package intelligent_security_IER_HF;

import static jason.asSyntax.ASSyntax.createLiteral;
import static jason.asSyntax.ASSyntax.createNumber;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

public class SecurityEnvironment extends Environment {

	public static final int GSize = 30; // grid size
	public static final int Burg = 16; // Burgler code in grid model
	public static final int SecP = 32; // Secured place code in grid model
	public static int Burgx = 3; // Burgler position on x koord
	public static int Burgy = 0; // Burgler position on y koord
	public static final Term step = Literal.parseLiteral("next(step)");
	public static boolean end = false;
	public static char dir;
	static Logger logger = Logger.getLogger(SecurityEnvironment.class.getName());

	private SecModell model;
	private SecView view;
	public SecuredPlace securedPlace;

	@Override
	public void init(final String[] args) {
		securedPlace = new SecuredPlace(GSize);
		model = new SecModell(securedPlace);
		view = new SecView(model);
		model.setView(view);
		updatePercepts();
		new Thread(new BurglerController()).start();

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
			switch (ke.getID()) {
			case KeyEvent.KEY_PRESSED:
				if (ke.getKeyCode() == KeyEvent.VK_W) {
					dir = 'w';
				} else if (ke.getKeyCode() == KeyEvent.VK_A) {
					dir = 'a';
				} else if (ke.getKeyCode() == KeyEvent.VK_S) {
					dir = 's';
				} else if (ke.getKeyCode() == KeyEvent.VK_D) {
					dir = 'd';
				} else
					dir = 'q';
				break;

			}
			return false;
		});
	}

	@Override
	public boolean executeAction(final String ag, final Structure action) {
		logger.info(ag + " doing: " + action);
		try {
			if (action.equals(step)) {
				// model.nextstep('d');
			} // else if (action.getFunctor().equals("move_towards")) {
				// final int x = (int) ((NumberTerm) action.getTerm(0)).solve();
			else {
				return true;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		updatePercepts();

		try {
			Thread.sleep(200);
		} catch (final Exception e) {
		}
		informAgsEnvironmentChanged();
		return true;
	}

	public class BurglerController implements Runnable {

		@Override
		public void run() {
			System.out.println("Hello from a thread!");
			while (!end) {
				model.nextstep(dir);
				view.repaint();
				try {
					Thread.sleep(200);
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/** creates the agents perception based on the MarsModel */
	void updatePercepts() {
		clearPercepts();
		if (securedPlace.inside(Burgx, Burgy)) {
			removePercept("dog", Literal.parseLiteral("burgler(outside)"));
			addPercept("dog", Literal.parseLiteral("burgler(inside)"));
		} else {
			removePercept("dog", Literal.parseLiteral("burgler(inside)"));
			addPercept("dog", Literal.parseLiteral("burgler(outside)"));
		}
		informAgsEnvironmentChanged();

		// final Location r1Loc = model.getAgPos(0);

		// final Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y
		// + ")");
		// addPercept(pos1);

		// if (model.hasObject(GARB, r1Loc)) {
		// addPercept(g1);
		// }
	}

	class SecModell extends GridWorldModel {
		SecuredPlace securedPlace;

		private SecModell(final SecuredPlace securedPlace) {
			super(GSize, GSize, 2);
			this.securedPlace = securedPlace;
			add(Burg, Burgx, Burgy);
			for (int x = 0; x < GSize; x++) {
				for (int y = 0; y < GSize; y++) {
					if (securedPlace.inside(x, y)) {
						add(SecP, x, y);
					}
				}
			}
		}

		public void nextstep(final char dir) {
			// TODO Auto-generated method stub
			if (model.hasObject(Burg, Burgx, Burgy)) {
				remove(Burg, Burgx, Burgy);
				switch (dir) {
				case 'a':
					if (Burgx == 0) {
						Burgx = GSize - 1;
					} else
						Burgx--;
					break;
				case 'w':
					if (Burgy == 0) {
						Burgy = GSize - 1;
					} else
						Burgy--;
					break;
				case 's':
					if (Burgy == GSize - 1) {
						Burgy = 0;
					} else
						Burgy++;
					break;
				case 'd':
					if (Burgx == GSize - 1) {
						Burgx = 0;
					} else
						Burgx++;
					break;
				}
				add(Burg, Burgx, Burgy);
			}
		}
	}

	class SecView extends GridWorldView {
		private SecView(final SecModell modell) {
			super(modell, "Mars World", 800);
			defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
			setVisible(true);
			repaint();
		}

		@Override
		public void draw(final Graphics g, final int x, final int y, final int object) {
			switch (object) {
			case SecurityEnvironment.Burg:
				drawBurg(g, x, y);
				break;
			case SecurityEnvironment.SecP:
				if (!(x == Burgx && y == Burgy))
					drawSecP(g, x, y);
				break;
			}
		}

		public void drawSecP(final Graphics g, final int x, final int y) {
			g.setColor(Color.GRAY);
			super.drawObstacle(g, x, y);
		}

		public void drawBurg(final Graphics g, final int x, final int y) {
			super.drawAgent(g, x, y, Color.RED, -1);
			g.setColor(Color.BLACK);
			drawString(g, x, y, defaultFont, "B");
		}
	}

	public class SecuredPlace {
		int start;
		int size;

		SecuredPlace(final int gridsize) {
			start = gridsize / 4;
			size = gridsize / 2;
		}

		public boolean inside(final int x, final int y) {
			return x > start && x < start + size && y > start && y < start + size;
		}
	}

	public SecurityEnvironment() {
		final Literal v = createLiteral("name", createNumber(-2));
		System.out.println(v);
		System.out.println("variable: " + Literal.parseLiteral("x(10)"));
		addPercept("dog", v);
	}

	public void setup() {
	}

}
