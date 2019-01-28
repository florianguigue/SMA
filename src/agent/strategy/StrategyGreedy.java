package agent.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 * @author lmatignon
 *
 */
public class StrategyGreedy extends StrategyExploration{
	/**
	 * parametre pour probabilite d'exploration
	 */
	protected double epsilon;
	private Random rand=new Random();
	
	
	
	public StrategyGreedy(RLAgent agent,double epsilon) {
		super(agent);
		this.epsilon = epsilon;
	}

	@Override
	public Action getAction(Etat _e) {//renvoi null si _e absorbant
		double d =rand.nextDouble();
		if (this.agent.getActionsLegales(_e).isEmpty()){
			return null;
		}

		Action action = null;
		if (d < epsilon || this.agent.getPolitique(_e).get(0) == null) {
			Integer random = rand.nextInt(this.agent.getActionsLegales(_e).size());
			action = this.agent.getActionsLegales(_e).get(random);
		} else {
			return this.agent.getPolitique(_e).get(0);
		}
		//VOTRE CODE ICI
		
		return action;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		System.out.println("epsilon:"+epsilon);
	}

/*	@Override
	public void setAction(Action _a) {
		// TODO Auto-generated method stub
		
	}*/

}
