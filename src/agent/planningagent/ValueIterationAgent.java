package agent.planningagent;

import java.util.*;

import util.HashMapUtil;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
import environnement.Action2D;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration
 * et choisit ses actions selon la politique calculee.
 *
 * @author laetitiamatignon
 */
public class ValueIterationAgent extends PlanningValueAgent {
    /**
     * discount facteur
     */
    protected double gamma;

    /**
     * fonction de valeur des etats
     */
    protected HashMap<Etat, Double> V;

    /**
     * @param gamma
     * @param mdp
     */
    public ValueIterationAgent(double gamma, MDP mdp) {
        super(mdp);
        this.gamma = gamma;
        V = new HashMap<>();
        this.mdp.getEtatsAccessibles().forEach(etat -> {
            V.put(etat, 0.0);
        });
    }


    public ValueIterationAgent(MDP mdp) {
        this(0.9, mdp);

    }

    /**
     * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
     * et notifie ses observateurs.
     * Ce n'est pas la version inplace (qui utilise nouvelle valeur de V pour mettre a jour ...)
     */
    @Override
    public void updateV() {
        //delta est utilise pour detecter la convergence de l'algorithme
        //lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
        //delta < epsilon
        this.delta = 0.0;
        Map<Etat, Double> cloneV = (Map<Etat, Double>) this.getV().clone();
        cloneV.forEach((ei, v) -> {
            try {
                final double[] result = {0.0};
                final double[] maxRes = {0.0};
                this.mdp.getActionsPossibles(ei).forEach((a) -> {
                    result[0] = 0.0;
                    try {
                        this.mdp.getEtatTransitionProba(ei, a).forEach((ea, proba) -> {
                            double recompense = this.mdp.getRecompense(ei, a, ea);
                            double oldVk = cloneV.get(ea);
                            result[0] += proba * (recompense + (oldVk * this.getGamma()));
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result[0] > maxRes[0]) {
                        maxRes[0] = result[0];
                    }
                });
                this.V.put(ei, maxRes[0]);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (this.V.get(ei) > vmax)
                vmax = this.V.get(ei);
            if (this.V.get(ei) < vmin)
                vmin = this.V.get(ei);
        });

        // mise a jour vmax et vmin pour affichage du gradient de couleur:
        //vmax est la valeur max de V pour tout s
        //vmin est la valeur min de V pour tout s
        // ...

        //******************* laisser notification a la fin de la methode
        this.notifyObs();
    }


    /**
     * renvoi l'action executee par l'agent dans l'etat e
     * Si aucune actions possibles, renvoi Action2D.NONE
     */
    @Override
    public Action getAction(Etat e) {
        List<Action> actions = this.getPolitique(e);
        if (actions.size() == 0) {
            return Action2D.NONE;//ActionGridworld.EXIT;
        }
        return actions.stream().findFirst().get();

    }

    @Override
    public double getValeur(Etat _e) {
        return this.V.get(_e);
    }

    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans etat
     * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
     */
    @Override
    public List<Action> getPolitique(Etat _e) {
        // retourne action de meilleure valeur dans _e selon V,
        // retourne liste vide si aucune action legale (etat absorbant)

        List<Action> returnactions = new ArrayList<Action>();
        final Map<Action, Double> actions = new HashMap<>();
        final double[] result = {0.0};

        if (this.mdp.estAbsorbant(_e)) {
            return returnactions;
        }
        this.mdp.getActionsPossibles(_e).forEach((a) -> {
            result[0] = 0.0;
            try {
                this.mdp.getEtatTransitionProba(_e, a).forEach((ea, val) -> {
                    double proba = val;
                    double recompense = this.mdp.getRecompense(_e, a, ea);
                    double oldVk = this.V.get(ea);
                    result[0] += proba * (recompense + (oldVk * this.getGamma()));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            actions.put(a, result[0]);
        });
        returnactions.add(actions.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey());

        return returnactions;

    }

    @Override
    public void reset() {
        super.reset();


        this.V.clear();
        for (Etat etat : this.mdp.getEtatsAccessibles()) {
            V.put(etat, 0.0);
        }
        this.notifyObs();
    }


    public HashMap<Etat, Double> getV() {
        return V;
    }

    public double getGamma() {
        return gamma;
    }

    @Override
    public void setGamma(double _g) {
        System.out.println("gamma= " + gamma);
        this.gamma = _g;
    }


}
