package pacman.environnementRL;

import java.util.ArrayList;
import java.util.Arrays;

import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;
import environnement.Etat2D;

/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec
 * QLearning tabulaire
 *
 */
public class EtatPacmanMDPClassic implements Etat, Cloneable {
	private ArrayList<Integer> positionsFantomes;
	private ArrayList<Integer> positionsDots;
	protected transient StateGamePacman etat;
	protected transient int X, Y;
	protected transient MazePacman maze;

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman) {
		etat = _stategamepacman;
		maze = etat.getMaze();
		positionsDots = new ArrayList<>();
		positionsFantomes = new ArrayList<>();


		StateAgentPacman pacman = etat.getPacmanState(0);
		X = pacman.getX();
		Y = pacman.getY();

		int dx,dy;
		Boolean foundFood = false;
		int distanceLook = 1;

		while(distanceLook <= 5){
			foundFood = true;
			if(maze.isFood(X, Y+distanceLook)){
				positionsDots.add(X);
				positionsDots.add(Y+distanceLook);
			}else if(maze.isFood(X, Y-distanceLook)){
				positionsDots.add(X);
				positionsDots.add(Y-distanceLook);
			}else if(maze.isFood(X+distanceLook, Y)){
				positionsDots.add(X+distanceLook);
				positionsDots.add(Y);
			}else if(maze.isFood(X-distanceLook, Y)){
				positionsDots.add(X-distanceLook);
				positionsDots.add(Y);
			}
//			else{
//				for(int i=0;i<=distanceLook;i++){
//					dx = i;
//					dy = distanceLook-i;
//					if(maze.isFood(X+dx, Y+dy) || maze.isCapsule(X+dx, Y+dy)){ //1er quadrant
//						foodSense = "1";
//					}else if(maze.isFood(X-dx, Y+dy) || maze.isCapsule(X-dx, Y+dy)){ //2eme quadrant
//						foodSense = "2";
//					}else if(maze.isFood(X-dx, Y-dy) || maze.isCapsule(X-dx, Y-dy)){ //2eme quadrant
//						foodSense = "3";
//					}else if(maze.isFood(X+dx, Y-dy) || maze.isCapsule(X+dx, Y-dy)){ //2eme quadrant
//						foodSense = "4";
//					}
//				}
//			}
			distanceLook++;
		}

		distanceLook = 4;
		// On regarde si un fant�me se trouve autour du pacman
		for (int j = 0; j < etat.getNumberOfGhosts(); j++) {
			StateAgentPacman ghostState = etat.getGhostState(j);
			int ghostX = ghostState.getX();
			int ghostY = ghostState.getY();

			if((ghostX - X <= distanceLook || ghostX - X >= -distanceLook) && (ghostY - Y <= distanceLook || ghostY - Y >= -distanceLook)){
				positionsFantomes.add(ghostX);
				positionsFantomes.add(ghostY);
			}
		}

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




	@Override
	public boolean equals(Object o) {
		return this.hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		int result = positionsFantomes != null ? positionsFantomes.hashCode() : 0;
		result = 31 * result + (positionsDots != null ? positionsDots.hashCode() : 0);
		result = 31 * result;
		return result;
	}

	public int getDimensions(){


		return 0;
	}


}
