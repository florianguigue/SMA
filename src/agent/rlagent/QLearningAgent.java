package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

/**
 * Renvoi 0 pour valeurs initiales de Q
 *
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
    /**
     * format de memorisation des Q valeurs: utiliser partout setQValeur car
     * cette methode notifie la vue
     */
    protected HashMap<Etat, HashMap<Action, Double>> qvaleurs;

    // AU CHOIX: vous pouvez utiliser une Map avec des Pair pour clés si vous
    // préférez
    // protected HashMap<Pair<Etat,Action>,Double> qvaleurs;

    /**
     *
     * @param alpha
     * @param gamma
     * @param Environnement
     *
     */
    public QLearningAgent(double alpha, double gamma, Environnement _env) {
        super(alpha, gamma, _env);
        qvaleurs = new HashMap<Etat, HashMap<Action, Double>>();

    }

    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e (plusieurs
     * actions sont renvoyees si valeurs identiques) renvoi liste vide si
     * aucunes actions possibles dans l'etat (par ex. etat absorbant)
     *
     */
    @Override
    public List<Action> getPolitique(Etat e) {
        // retourne action de meilleures valeurs dans _e selon Q : utiliser
        // getQValeur()
        // retourne liste vide si aucune action legale (etat terminal)
        List<Action> returnactions = new ArrayList<Action>();
        if (this.getActionsLegales(e).size() == 0) {// etat absorbant;
            // impossible de le verifier
            // via environnement
            System.out.println("aucune action legale");
            return new ArrayList<Action>();
        }

        // *** VOTRE CODE

        double bestQVal = 0.0;
        //On parcourt les actions possibles
        for(Action a : this.getActionsLegales(e)){
            //Si c'est la première action qu'on considère
            if(returnactions.size()==0){
                //Alors c'est forcément la meilleure pour l'instant
                bestQVal = this.getQValeur(e, a);
                returnactions.add(a);
            }

            if (this.getQValeur(e, a) > bestQVal){
                bestQVal = this.getQValeur(e, a);
                returnactions.clear();
                returnactions.add(a);
            }
            if (this.getQValeur(e, a) == bestQVal){
                returnactions.add(a);
            }
        }

        return returnactions;

    }

    @Override
    public double getValeur(Etat e) {
        // *** VOTRE CODE
        double output = Double.NEGATIVE_INFINITY;
        double val;

        //La valeur d'un état est la Q-valeur de la meilleure action possible depuis cet état
        for(Action a: this.getActionsLegales(e)){
            val = this.getQValeur(e, a);
            if(val > output){
                output = val;
            }
        }

        // M�j de vmin et vmax
        if(output>this.vmax)
            this.vmax = output;
        if(output<this.vmin)
            this.vmin = output;

        return output;

    }

    @Override
    public double getQValeur(Etat e, Action a) {
        // *** VOTRE CODE
        if(!this.qvaleurs.containsKey(e) || !this.qvaleurs.get(e).containsKey(a)){
            //Si on n'a jamais rencontré ce couple état/action, on renvoit 0
            return 0.0;
        }else{
            //Sinon on lit simplement la valeur depuis l'attribut qvaleurs
            return qvaleurs.get(e).get(a);
        }
    }

    @Override
    public void setQValeur(Etat e, Action a, double d) {
        // *** VOTRE CODE

        if (!this.qvaleurs.containsKey(e)) {
            this.qvaleurs.put(e, new HashMap<Action, Double>());
        }

        this.qvaleurs.get(e).put(a, d);

        // mise a jour vmax et vmin pour affichage du gradient de couleur:
        // vmax est la valeur max de V pour tout s
        // vmin est la valeur min de V pour tout s
        // ...

        this.notifyObs();

    }

    /**
     * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat
     * e,action a, etatsuivant esuivant, recompense reward> la mise a jour
     * s'effectue lorsque l'agent est notifie par l'environnement apres avoir
     * realise une action.
     *
     * @param e
     * @param a
     * @param esuivant
     * @param reward
     */
    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        if (RLAgent.DISPRL)
            System.out.println("QL mise a jour etat " + e + " action " + a + " etat' " + esuivant + " r " + reward);

        // *** VOTRE CODE

        double nouveauQ = (1-this.alpha)*this.getQValeur(e, a) + this.alpha*(reward + this.gamma*this.getValeur(esuivant));
        this.setQValeur(e, a, nouveauQ);

    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    @Override
    public void reset() {
        super.reset();
        //*** VOTRE CODE
        this.qvaleurs = new HashMap<Etat, HashMap<Action,Double>>();

        this.episodeNb =0;
        this.notifyObs();
    }

}
