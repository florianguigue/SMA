package agent.rlapproxagent;

import java.util.ArrayList;
import java.util.List;

import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

/**
 * Agent qui apprend avec QLearning en utilisant approximation de la Q-valeur :
 * approximation lineaire de fonctions caracteristiques
 *
 * @author laetitiamatignon
 *
 */
public class QLApproxAgent extends QLearningAgent {

	private FeatureFunction featureFunction;
	private double[] poids;
	private int nbFeatures;

	public QLApproxAgent(double alpha, double gamma, Environnement _env, FeatureFunction _featurefunction) {
		super(alpha, gamma, _env);
		// *** VOTRE CODE
		featureFunction = _featurefunction;
		nbFeatures = featureFunction.getFeatureNb();
		poids = new double[nbFeatures];
		for (int i = 0; i < nbFeatures; i++) {
			poids[i] = 0;
		}
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		// *** VOTRE CODE
		double qVal = 0;
		// On r�cup�re les features en dehors de la boucle
		double[] features = featureFunction.getFeatures(e, a);
		for (int i = 0; i < nbFeatures; i++) {
			// Somme cumulative des theta_i * phi_i(e,a)
			qVal += poids[i] * features[i];
		}

		return qVal;

	}

	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL) {
			System.out.println("QL: mise a jour poids pour etat \n" + e + " action " + a + " etat' \n" + esuivant
					+ " r " + reward);
		}
		// inutile de verifier si e etat absorbant car dans runEpisode et
		// threadepisode
		// arrete episode lq etat courant absorbant

		// *** VOTRE CODE

		// On r�cup�re la valeur de esuivant (max( Q(s',b) ) dans la formule
		double valESuivant = this.getValeur(esuivant);
		// On r�cup�re les features en dehors de la boucle
		double[] features = featureFunction.getFeatures(e, a);
		for (int k = 0; k < nbFeatures; k++) {
			// On applique la formule de mise � jour des poids
			poids[k] += this.alpha * (reward + this.gamma * valESuivant - this.getQValeur(e, a)) * features[k];
		}
	}

	@Override
	public void reset() {
		super.reset();
		this.qvaleurs.clear();

		// *** VOTRE CODE
		for(int i=0;i<nbFeatures;i++){
			poids[i]=0;
		}

		this.episodeNb = 0;
		this.notifyObs();
	}

}
