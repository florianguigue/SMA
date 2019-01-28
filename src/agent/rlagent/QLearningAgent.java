package agent.rlagent;

import java.util.*;

import environnement.Action2D;
import javafx.event.ActionEvent;
import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
	/**
	 *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
	 */
	protected HashMap<Etat,HashMap<Action,Double>> qvaleurs;

	//AU CHOIX: vous pouvez utiliser une Map avec des Pair pour clés si vous préférez
	//protected HashMap<Pair<Etat,Action>,Double> qvaleurs;


	/**
	 *
	 * @param alpha
	 * @param gamma
	 * @param Environnement

	 */
	public QLearningAgent(double alpha, double gamma,
						  Environnement _env) {
		super(alpha, gamma,_env);
		qvaleurs = new HashMap<Etat,HashMap<Action,Double>>();
	}




	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  (plusieurs actions sont renvoyees si valeurs identiques)
	 *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)

	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		// retourne action de meilleures valeurs dans _e selon Q : utiliser getQValeur()
		// retourne liste vide si aucune action legale (etat terminal)
		List<Action> returnactions = new ArrayList<Action>();
		if (this.getActionsLegales(e).size() == 0){//etat  absorbant; impossible de le verifier via environnement
			out.println("aucune action legale");
			return new ArrayList<Action>();

		}

		if(!qvaleurs.containsKey(e)) {
			return this.env.getActionsPossibles(e);
		}

		Double beneficeMax = qvaleurs.get(e).values().stream().max(Double::compareTo).orElse(0d);
		return qvaleurs.get(e).entrySet().stream()
				.filter(actionDoubleEntry -> actionDoubleEntry.getValue().equals(beneficeMax))
				.map(Map.Entry::getKey)
				.collect(toList());
	}

	@Override
	public double getValeur(Etat e) {
		if(!qvaleurs.containsKey(e)){
			return 0;
		}
		return qvaleurs.get(e).values().stream().max(Double::compareTo).orElse(0d);

	}

	@Override
	public double getQValeur(Etat e, Action a) {
		if(!qvaleurs.containsKey(e) || !qvaleurs.get(e).containsKey(a)){
			return 0;
		}
		return qvaleurs.get(e).get(a);
	}



	@Override
	public void setQValeur(Etat e, Action a, double d) {
		qvaleurs.get(e).put(a, d);
		// mise a jour vmax et vmin pour affichage du gradient de couleur:
		//vmax est la valeur max de V pour tout s
		//vmin est la valeur min de V pour tout s
		// ...
		out.println("Pour l'etat : " + e.toString() + " et l'action " + a.toString() + " , valeur = " + d);
		this.notifyObs();

	}


	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {

		if (RLAgent.DISPRL)
			out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);

		if(!qvaleurs.containsKey(e))  {
			HashMap<Action, Double> mapE = new HashMap<>();
			this.env.getActionsPossibles(e).forEach(action -> mapE.put(action, 0d));
			qvaleurs.put(e, mapE);
		}
		if(!qvaleurs.containsKey(esuivant))  {
			HashMap<Action, Double> mapS = new HashMap<>();
			this.env.getActionsPossibles(esuivant).forEach(action -> mapS.put(action, 0d));
			qvaleurs.put(esuivant, mapS);
		}
		HashMap<Action, Double> actionsSuivantes = qvaleurs.get(esuivant);
		Double maxActionSuivanteValue = actionsSuivantes.values().stream().max(Double::compareTo).orElse(0d);
//		Double newQ = (1 - this.alpha)* qvaleurs.get(e).get(a).doubleValue() + this.alpha * (reward + getGamma() * maxActionSuivanteValue);
		Double newQ = (1 - this.alpha)* this.getQValeur(e,a) + this.alpha * (reward + getGamma() * maxActionSuivanteValue);

		setQValeur(e, a, newQ);
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		this.qvaleurs = new HashMap<Etat,HashMap<Action,Double>>();

		this.episodeNb =0;
		this.notifyObs();
	}
}
