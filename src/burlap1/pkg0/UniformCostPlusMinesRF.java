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
        HashMap<String, Integer[]> mineCoordinates = this.domain.getMineCoordinates();

        // Recupera valores actuales de posicion del agente
        int x = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("x");
        int y = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("y");

        // Recupera valores de monedas y minas
        int coinReward = this.domain.getCoinVal();
        int mineReward = this.domain.getMineVal();
        int noBudgetReward = this.domain.getNoBudgetVal();
        
        System.out.println("s: " + s.getCompleteStateDescription());
        System.out.println("sprime: " + sprime.getCompleteStateDescription());
        
        boolean c1 = s.getFirstObjectOfClass("agent").getBooleanValForAttribute("c1");
        boolean c2 = s.getFirstObjectOfClass("agent").getBooleanValForAttribute("c2");
        int budgs = s.getFirstObjectOfClass("agent").getIntValForAttribute("budget");
        
        boolean c1p = sprime.getFirstObjectOfClass("agent").getBooleanValForAttribute("c1");
        boolean c2p = sprime.getFirstObjectOfClass("agent").getBooleanValForAttribute("c2");
        int budgsp = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("budget");
        
        //System.out.println(c1 + " " + c2 + " " + c1p + " " + c2p);

        //System.out.println("Reward at: " + x + ", " + y);
        
        //System.out.println("Verify coins: ");
        
        System.out.println("Estado final: " + budgs + " " + budgsp);
        if( budgsp == 0 && budgs == 1 ){
            System.out.println("AAA");
            System.exit(0);
            return noBudgetReward;
        }
        
        // Si hubo un cambio en las monedas
        if((c1 ^ c1p) || (c2 ^ c2p)){
            System.out.println("Coin found... " + x + "," + y + " " + mineReward);
            domain.updateBudget(coinReward, sprime);
            // Si se han encontrado las dos monedas resetea el budget
            if( c1p && c2p ){
                domain.resetBudget(sprime);
            }
            // Regresa la recompensa de moneda
            return coinReward;
        }

        // Verifica si es una mina
        //System.out.println("Verify mines: ");
        for (String m : mineCoordinates.keySet()) {
            if (mineCoordinates.get(m)[0] == x && mineCoordinates.get(m)[1] == y) {
                System.out.println("Mine found... " + x + "," + y + " " + mineReward);
                domain.updateBudget(mineReward, sprime);
                return mineReward;
            }
        }
        domain.updateBudget(-1, sprime);
        return -1;
    }

}
