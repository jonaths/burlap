package burlap1.pkg0;

import burlap.MineWorldDomain;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import java.util.HashMap;

/**
 * Defines a reward function that always returns -1.
 *
 * @author James MacGlashan
 *
 */
public class UniformCostPlusMinesRF implements RewardFunction {

    MineWorldDomain domain;

    UniformCostPlusMinesRF(MineWorldDomain domain) {
        this.domain = domain;
    }

    @Override
    public double reward(State s, GroundedAction a, State sprime) {
        HashMap<String, Integer[]> coinCoordinates = this.domain.getCoinCoordinates();
        HashMap<String, Integer[]> mineCoordinates = this.domain.getCoinCoordinates();

        // Recupera valores actuales de posicion del agente
        int x = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("x");
        int y = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("y");

        // Recupera valores de monedas y minas
        int coinReward = this.domain.getCoinVal();
        int mineReward = this.domain.getMineVal();

        System.out.println("A");
        
        // Verifica si es una moneda
        for (String m : coinCoordinates.keySet()) {
            if (coinCoordinates.get(m)[0] == x && coinCoordinates.get(m)[1] == y) {
                System.out.println("Coin: " + x + "," + y + " " + coinReward);
                return coinReward;
            }
        }

        System.out.println("B");
        
        // Verifica si es una mina
        for (String m : mineCoordinates.keySet()) {
            if (mineCoordinates.get(m)[0] == x && mineCoordinates.get(m)[1] == y) {
                System.out.println("Mine: " + x + "," + y + " " + mineReward);
                return mineReward;
            }
        }
        
        System.out.println("C");

        return -1;
    }

}
