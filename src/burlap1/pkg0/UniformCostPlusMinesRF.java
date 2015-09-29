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
        
        int reward;
        boolean isMine = false;

        // Recupera posiciones de minas y monedas
        HashMap<String, Integer[]> coinCoordinates = this.domain.getCoinCoordinates();
        HashMap<String, Integer[]> mineCoordinates = this.domain.getMineCoordinates();
        
        // Recupera valores actuales de posicion del agente
        int x = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("x");
        int y = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("y");        
        
        // Recupera recompensas de minas y monedas. 
        int coinReward = this.domain.getCoinVal();
        int mineReward = this.domain.getMineVal();        
        
        for (String m : mineCoordinates.keySet()) {
            if (mineCoordinates.get(m)[0] == x && mineCoordinates.get(m)[1] == y) {
                System.out.println("UniformCostPlusMinesRF::reward. Mine found... " + x + "," + y + " " + mineReward);
                isMine = true;
            }
        }        
        
        if(isMine){
            reward = mineReward;
        }
        else{
            reward = -1;
        }
        
        domain.updateBudget(reward);
        return -reward;
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        HashMap<String, Integer[]> coinCoordinates = this.domain.getCoinCoordinates();
//        HashMap<String, Integer[]> mineCoordinates = this.domain.getMineCoordinates();
//
//        // Recupera valores actuales de posicion del agente
//        int x = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("x");
//        int y = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("y");
//
//        // Recupera valores de monedas y minas
//        int coinReward = this.domain.getCoinVal();
//        int mineReward = this.domain.getMineVal();
//        int noBudgetReward = this.domain.getNoBudgetVal();
//        
//        System.out.println("s: " + s.getCompleteStateDescription());
//        System.out.println("sprime: " + sprime.getCompleteStateDescription());
//        
//        boolean c1 = s.getFirstObjectOfClass("agent").getBooleanValForAttribute("c1");
//        boolean c2 = s.getFirstObjectOfClass("agent").getBooleanValForAttribute("c2");
//        
//        boolean c1p = sprime.getFirstObjectOfClass("agent").getBooleanValForAttribute("c1");
//        boolean c2p = sprime.getFirstObjectOfClass("agent").getBooleanValForAttribute("c2");
//        int budgetp = sprime.getFirstObjectOfClass("agent").getIntValForAttribute("budget");
//        
//        //System.out.println("Reward at: " + x + ", " + y);
//        
//        if( budgetp == 0 ){
//            System.out.println("UniformCostPlusMines::reward. returning ");
//            this.domain.noBudgetTransition = false;
//            domain.updateBudget(noBudgetReward, sprime);
//            return noBudgetReward;
//        }
//        
//        // Si hubo un cambio en las monedas
//        //System.out.println("Verify coins: ");
//        if((c1 ^ c1p) || (c2 ^ c2p)){
//            //System.out.println("Coin found... " + x + "," + y + " " + mineReward);
//            domain.updateBudget(coinReward, sprime);
//            // Si se han encontrado las dos monedas resetea el budget
//            if( c1p && c2p ){
//                domain.resetBudget(sprime);
//            }
//            // Regresa la recompensa de moneda
//            return coinReward;
//        }
//
//        // Verifica si es una mina
//        //System.out.println("Verify mines: ");
//        for (String m : mineCoordinates.keySet()) {
//            if (mineCoordinates.get(m)[0] == x && mineCoordinates.get(m)[1] == y) {
//                System.out.println("UniformCostPlusMinesRF::reward. Mine found... " + x + "," + y + " " + mineReward);
//                domain.updateBudget(mineReward, sprime);
//                return mineReward;
//            }
//        }
//        
//        
//        
//        domain.updateBudget(-1, sprime);
    }

}
