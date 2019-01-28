package pacman.environnementRL;

import environnement.Etat;
import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;

import java.util.ArrayList;

/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire
 */
public class EtatPacmanMDPClassic implements Etat, Cloneable {
	private ArrayList<Position> positionsFantomes;
	private ArrayList<Position> positionsDots;
	private Position positionPacman;
	private MazePacman mazePacman;

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman) {
		mazePacman = _stategamepacman.getMaze();
		positionsDots = new ArrayList<>();
		positionsFantomes = new ArrayList<>();

		// On cherche le pacman dans le labyrinthe
		for (int i = 0; i < _stategamepacman.getNumberOfPacmans(); i++) {
			StateAgentPacman pacmanState = _stategamepacman.getPacmanState(i);
			positionPacman = new Position(pacmanState.getX(), pacmanState.getY());
		}

		// On a un seul Pacman dans le jeu
		int pacX = positionPacman.x;
		int pacY = positionPacman.y;

		// On cherche un dot autour du pacman
		int p = 1;
		while (p < 4) {
			// Haut, Bas, Gauche, Droite
			if (checkMazeLimits(pacX + p, 'x'))
				checkDotsPosition(pacX + p, pacY, new Position(pacX + p, pacY));
			if (checkMazeLimits(pacX - p, 'x'))
				checkDotsPosition(pacX - 1, pacY, new Position(pacX - p, pacY));
			if (checkMazeLimits(pacY + p, 'y'))
				checkDotsPosition(pacX, pacY + p, new Position(pacX, pacY + p));
			if (checkMazeLimits(pacY - p, 'y'))
				checkDotsPosition(pacX, pacY - p, new Position(pacX, pacY - p));

			// Diagonales
			if (checkDiagonalMazeLimits(pacX + p, pacY + p))
				checkDotsPosition(pacX + p, pacY + p, new Position(pacX + p, pacY + p));
			if (checkDiagonalMazeLimits(pacX + p, pacY - p))
				checkDotsPosition(pacX + p, pacY - p, new Position(pacX + p, pacY - p));
			if (checkDiagonalMazeLimits(pacX - p, pacY + p))
				checkDotsPosition(pacX - p, pacY + p, new Position(pacX - p, pacY + p));
			if (checkDiagonalMazeLimits(pacX - p, pacY - p))
				checkDotsPosition(pacX - p, pacY - p, new Position(pacX - p, pacY - p));

			p++;
		}

		// On regarde si un fantôme se trouve autour du pacman
		for (int j = 0; j < _stategamepacman.getNumberOfGhosts(); j++) {
			StateAgentPacman ghostState = _stategamepacman.getGhostState(j);
			int gostX = ghostState.getLastX();
			int gostY = ghostState.getLastY();

			if (checkMazeLimits(pacX + 1, 'x'))
				addGhostToList(pacX + 1, pacY, gostX, gostY);
			if (checkMazeLimits(pacX - 1, 'x'))
				addGhostToList(pacX - 1, pacY, gostX, gostY);
			if (checkMazeLimits(pacY + 1, 'y'))
				addGhostToList(pacX, pacY + 1, gostX, gostY);
			if (checkMazeLimits(pacY - 1, 'y'))
				addGhostToList(pacX, pacY - 1, gostX, gostY);

			// Diagonales
			if (checkDiagonalMazeLimits(pacX + 1, pacY + 1))
				addGhostToList(pacX + 1, pacY + 1, gostX, gostY);
			if (checkDiagonalMazeLimits(pacX + 1, pacY - 1))
				addGhostToList(pacX + 1, pacY - 1, gostX, gostY);
			if (checkDiagonalMazeLimits(pacX - 1, pacY + 1))
				addGhostToList(pacX - 1, pacY + 1, gostX, gostY);
			if (checkDiagonalMazeLimits(pacX - 1, pacY - 1))
				addGhostToList(pacX - 1, pacY - 1, gostX, gostY);
		}

	}

	private boolean checkMazeLimits(int pos, char axis) {
		if (axis == 'x') {
			return pos < mazePacman.getSizeX() && pos > 0;
		}
		if (axis == 'y') {
			return pos < mazePacman.getSizeY() && pos > 0;
		}
		return false;
	}

	private boolean checkDiagonalMazeLimits(int posX, int posY) {
		return posX < mazePacman.getSizeX() && posX > 0 && posY < mazePacman.getSizeY() && posY > 0;
	}

	private void checkDotsPosition(int pacmanPosX, int y, Position e) {
		if (mazePacman.isFood(pacmanPosX, y)) {
			positionsDots.add(e);
		}
	}

	private void addGhostToList(int pacX, int pacY, int gostX, int gostY) {
		positionsFantomes.add(new Position(gostX, gostY));
	}

	@Override
	public String toString() {

		return "";
	}


	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la
			// methode super.clone()
			clone = (EtatPacmanMDPClassic) super.clone();
		} catch (CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}


		// on renvoie le clone
		return clone;
	}


	private class Position {
		private int x;
		private int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Position position = (Position) o;

			if (x != position.x) return false;
			return y == position.y;

		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtatPacmanMDPClassic that = (EtatPacmanMDPClassic) o;

		if (positionsFantomes != null ? !positionsFantomes.equals(that.positionsFantomes) : that.positionsFantomes != null)
			return false;
		if (positionsDots != null ? !positionsDots.equals(that.positionsDots) : that.positionsDots != null)
			return false;
		return positionPacman != null ? positionPacman.equals(that.positionPacman) : that.positionPacman == null;

	}

	@Override
	public int hashCode() {
		int result = positionsFantomes != null ? positionsFantomes.hashCode() : 0;
		result = 31 * result + (positionsDots != null ? positionsDots.hashCode() : 0);
		result = 31 * result + (positionPacman != null ? positionPacman.hashCode() : 0);
		return result;
	}

	public int getDimensions() {
		int nb = 0;
		for (int i = 0; i < mazePacman.getSizeX(); i++) {
			for (int j = 0; j < mazePacman.getSizeY(); j++) {
				nb = mazePacman.isWall(i, j) ? nb : nb + 1;
			}
		}

        /*if(positionsDots.size() > 1) {
            nb = positionsDots.size()*nb;
        }

        if(positionsFantomes.size() > 1) {
            nb = positionsFantomes.size()*nb;
        }*/
		return nb;
	}
}
