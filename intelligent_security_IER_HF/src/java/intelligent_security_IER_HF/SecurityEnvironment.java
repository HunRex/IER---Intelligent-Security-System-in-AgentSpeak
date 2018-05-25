package intelligent_security_IER_HF;

import static jason.asSyntax.ASSyntax.createLiteral;
import static jason.asSyntax.ASSyntax.createNumber;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Logger;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

public class SecurityEnvironment extends Environment {

	public static final int GSize = 30; // grid size
	public static final int Burg = 16; // Burgler code in grid model
	public static final int SecP = 32; // Secured place code in grid model
	public static final int CamV = 8; // camera viewpoint code in grid model
	public static final int Alar = 64; // alarm range code in grid model
	public static int Burgx = 3; // Burgler position on x koord
	public static int Burgy = 0; // Burgler position on y koord
	public static final Term step = Literal.parseLiteral("next(step)");
	public static final Term search = Literal.parseLiteral("random(search)");
	public static final Term stay = Literal.parseLiteral("stay(there)");
	public static final Term scare = Literal.parseLiteral("scare(burgler)");
	public static boolean end = false;
	public static char dir;
	static Logger logger = Logger.getLogger(SecurityEnvironment.class.getName());

	private SecModell model;
	private SecView view;
	public SecuredPlace securedPlace;
	private Thread thread;

	@Override
	public void init(final String[] args) {
		securedPlace = new SecuredPlace(GSize);
		model = new SecModell(securedPlace, new ArrayList<Camera>());
		view = new SecView(model);
		model.setView(view);
		updatePercepts();
		thread = new Thread(new BurglerController());
		thread.start(); // moving burgler on new thread

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
		// logger.info(ag + " doing: " + action); //write consol what action is done
		try {
			if (action.equals(step)) {
				// model.nextstep('d');
			} else if (action.getFunctor().equals("camera_search")) {
				final int x = (int) ((NumberTerm) action.getTerm(0)).solve();
				final int y = (int) ((NumberTerm) action.getTerm(1)).solve();
				final int d = (int) ((NumberTerm) action.getTerm(1)).solve();
				model.search(ag);
			} else if (action.getFunctor().equals("followburgler")) {
				final int x = (int) ((NumberTerm) action.getTerm(0)).solve();
				final int y = (int) ((NumberTerm) action.getTerm(1)).solve();
				model.follow(ag, x, y);
			} else if (action.getFunctor().equals("scare_burgler")) {
				final int x = (int) ((NumberTerm) action.getTerm(0)).solve();
				final int y = (int) ((NumberTerm) action.getTerm(1)).solve();
				model.scareburgler(x, y);
			} else if (action.equals(stay)) {
				// nothing
			} else if (action.equals(scare)) {
				model.scare(ag);
			} else {
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
			while (!end) {
				model.nextstep(dir);
				// view.repaint();
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
		for (final Camera c : model.cameras) {
			addPercept(c.id, Literal.parseLiteral("position(self," + c.posX + "," + c.posY + ")"));
			addPercept(c.id, Literal.parseLiteral("face(" + c.faceDir + ")"));
			addPercept(c.id, Literal.parseLiteral("turn(" + c.degree + ")"));
			if (c.inside(Burgx, Burgy)) {
				addPercept(c.id, Literal.parseLiteral("position(burg," + Burgx + "," + Burgy + ")"));

				if (!containsPercept(c.id, Literal.parseLiteral("see(burg)"))) {
					addPercept(c.id, Literal.parseLiteral("see(burg)"));
				}
			} else {
				if (containsPercept(c.id, Literal.parseLiteral("see(burg)"))) {
					removePercept(c.id, Literal.parseLiteral("see(burg)"));
				}
			}
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
		Collection<Camera> cameras;
		Collection<Alarm> alarms;
		Random random = new Random(System.currentTimeMillis());
		final int nomOfAlarms = 4;
		final int numOfCameras = 4;
		final int alarmData[] = { GSize / 2 + 3, GSize / 4, 3, GSize / 4, GSize / 2 - 3, 3, GSize * 3 / 4,
				GSize / 2 - 3, 3, GSize / 2 - 3, GSize / 4, 3 };
		final int camPos[] = { GSize / 2, GSize / 4, 3 * GSize / 4, GSize / 2, GSize / 2, 3 * GSize / 4, GSize / 4,
				GSize / 2 };

		private SecModell(final SecuredPlace securedPlace, final Collection<Camera> cameras) {
			super(GSize, GSize, 10);
			this.cameras = cameras;
			alarms = new ArrayList<>();
			this.securedPlace = securedPlace;
			for (int i = 0; i < nomOfAlarms; i++) {
				this.alarms.add(
						new Alarm("alarm" + (i + 1), alarmData[3 * i], alarmData[3 * i + 1], alarmData[3 * i + 2]));
				setAgPos(i + numOfCameras, alarmData[3 * i], alarmData[3 * i + 1]);
			}
			for (int i = 0; i < numOfCameras; i++) {
				this.cameras.add(new Camera("camera" + (i + 1), camPos[2 * i], camPos[2 * i + 1], i, 1));
				setAgPos(i, camPos[2 * i], camPos[2 * i + 1]);
			}
			add(Burg, Burgx, Burgy);
			for (int x = 0; x < GSize; x++) {
				for (int y = 0; y < GSize; y++) {
					if (securedPlace.inside(x, y)) {
						add(SecP, x, y);
					}
					for (final Camera c : this.cameras) {
						if (c.inside(x, y)) {
							add(CamV, x, y);
						}
					}
					for (final Alarm a : this.alarms) {
						if (a.inside(x, y)) {
							add(Alar, x, y);
						}
					}
				}
			}
		}

		public void scare(final String ag) {
			// TODO Auto-generated method stub
			for (final Alarm a : alarms) {
				if (a.id.equals(ag)) {
					if (a.inside(Burgx, Burgy)) {
						logger.info("Burgler have been caught");
						thread.stop();
					}
				}
			}
		}

		public void scareburgler(final int x, final int y) {
			// TODO Auto-generated method stub
			for (final Alarm a : alarms) {
				if (a.inside(x, y)) {
					addPercept("guard", Literal.parseLiteral("scare(possible)"));
					addPercept("guard", Literal.parseLiteral("alarm(" + a.id + ")"));
					return;
				}
			}
		}

		public void follow(final String ag, final int x, final int y) {
			// TODO Auto-generated method stub
			for (final Camera c : cameras) {
				if (c.id.equals(ag)) {
					followSpin(c, x, y);
				}
			}
		}

		public void search(final String Ag) {
			// TODO Auto-generated method stub
			for (final Camera c : cameras) {
				if (c.id.equals(Ag)) {
					randSpin(c);
				}
			}
		}

		public void followSpin(final Camera cam, final int bx, final int by) {
			final int xdir = bx - cam.posX;
			final int ydir = by - cam.posY;
			switch (cam.degree) {
			case 0:
				if (xdir > 0)
					spin(cam, 1);
				else if (xdir < 0)
					spin(cam, 3);
				else
					spin(cam, 2);
				break;
			case 1:
				if (ydir > 0)
					spin(cam, 2);
				else if (ydir < 0)
					spin(cam, 0);
				else
					spin(cam, 3);
				break;
			case 2:
				if (xdir > 0)
					spin(cam, 1);
				else if (xdir < 0)
					spin(cam, 3);
				else
					spin(cam, 0);
				break;
			case 3:
				if (ydir > 0)
					spin(cam, 2);
				else if (ydir < 0)
					spin(cam, 0);
				else
					spin(cam, 1);
				break;
			}
		}

		public void randSpin(final Camera cam) {

			switch (random.nextInt() % 3) {
			case 0:
				if (cam.degree == 0)
					spin(cam, 3);
				else
					spin(cam, cam.degree - 1);
				break;
			case 1:
				if (cam.degree == 3)
					spin(cam, 0);
				else
					spin(cam, cam.degree + 1);
				break;
			case 2: // not move
				break;
			}
		}

		public void spin(final Camera cam, final int degreeNew) {
			for (int x = 0; x < GSize; x++) {
				for (int y = 0; y < GSize; y++) {
					if (cam.inside(x, y)) {
						remove(CamV, x, y);
					}
				}
			}
			cam.degree = degreeNew;
			for (int x = 0; x < GSize; x++) {
				for (int y = 0; y < GSize; y++) {
					if (cam.inside(x, y)) {
						add(CamV, x, y);
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
			super(modell, "Security World", 800);
			defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
			setVisible(true);
			repaint();
		}

		@Override
		public void draw(final Graphics g, final int x, final int y, final int object) {
			g.setFont(defaultFont);
			switch (object) {
			case SecurityEnvironment.Burg:
				drawBurg(g, x, y);
				break;
			case SecurityEnvironment.SecP:
				drawSecP(g, x, y);
				break;
			case SecurityEnvironment.CamV:
				drawCamV(g, x, y);
				break;
			case SecurityEnvironment.Alar:
				drawAlar(g, x, y);
				break;
			}
		}

		public void drawSecP(final Graphics g, final int x, final int y) {
			g.setColor(Color.GRAY);
			g.drawString("P", convertCoordinateX(x), convertCoordinateY(y));
			// drawString(g, x , y, defaultFont, "P");
			// super.drawObstacle(g, x, y);
		}

		public void drawCamV(final Graphics g, final int x, final int y) {
			g.setColor(Color.blue);
			g.drawString("x", convertCoordinateX(x) + 5, convertCoordinateY(y) + 5);
		}

		public void drawAlar(final Graphics g, final int x, final int y) {
			g.setColor(Color.orange);
			g.drawString("a", convertCoordinateX(x) + 10, convertCoordinateY(y));
		}

		public void drawBurg(final Graphics g, final int x, final int y) {
			super.drawAgent(g, x, y, Color.RED, -1);
			g.setColor(Color.BLACK);
			drawString(g, x, y, defaultFont, "B");
		}

		public int convertCoordinateX(final int i) { // left coord of grid slot
			return 800 / GSize * i;
		}

		public int convertCoordinateY(final int i) { // top coord of grid slot
			return (750 / GSize * i) + 15;
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

	public class Camera {
		int posX, posY;
		int faceDir, degree, distance;
		String id;

		public Camera() {
			posX = 0;
			posY = 0;
			faceDir = 0;
			degree = 0;
			id = "";
			distance = 0;
		}

		public Camera(final String cid, final int x, final int y, final int fdir, final int cdegree) {
			posX = x;
			posY = y;
			faceDir = fdir;
			degree = cdegree;
			id = cid;
			distance = 6; // how far the camera looks
		}

		public boolean inside(final int X, final int Y) {
			if (posX == X && posY == Y)
				return true;
			int dist = 0;
			switch (degree) {
			case 0: // up
				dist = posY - Y;
				return X >= posX - dist && X <= posX + dist && Y < posY && Y > posY - distance;
			case 1: // right
				dist = X - posX;
				return Y >= posY - dist && Y <= posY + dist && X > posX && X < posX + distance;
			case 2: // down
				dist = Y - posY;
				return X >= posX - dist && X <= posX + dist && Y > posY && Y < posY + distance;
			case 3:// left
				dist = posX - X;
				return Y >= posY - dist && Y <= posY + dist && X < posX && X > posX - distance;
			default:
				return false;
			}
		}

	}

	public class Alarm {
		String id;
		int posX, posY, range;

		public Alarm(final String aid, final int xpos, final int ypos, final int arange) {
			id = aid;
			posX = xpos;
			posY = ypos;
			range = arange;
		}

		public boolean inside(final int X, final int Y) {
			return Math.abs(X - posX) < range && Math.abs(Y - posY) < range;
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
