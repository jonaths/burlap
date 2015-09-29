/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burlap;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ACTIONEAST;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ACTIONNORTH;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ACTIONSOUTH;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ACTIONWEST;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ATTLOCTYPE;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ATTX;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.ATTY;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.CLASSAGENT;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.CLASSLOCATION;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.PFATLOCATION;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.PFWALLEAST;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.PFWALLNORTH;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.PFWALLSOUTH;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.PFWALLWEST;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.SADomain;
import java.util.HashMap;

/**
 *
 * @author jonathan
 */
public class MineWorldDomain extends GridWorldDomain {

    /**
     * Constant for the name of coin 1
     */
    public static final String C1 = "c1";

    /**
     * Constant for the name of the coin 2
     */
    public static final String C2 = "c2";
    
    /**
     * Constant for the name of the budget status
     */
    public static final String BUDGET = "budget";    
    
    /**
	 * Constant for the name of the at location propositional function
     */
    public static final String  PFHASALLCOINSORNOBUDGET = "hasAllCoinsOrNoBudget";
    
    public HashMap<String, Integer[]> coins;
    public HashMap<String, Integer[]> mines;
    public int coinVal;
    public int mineVal;
    public int noBudgetVal;
    public boolean noBudgetTransition;

    public int budget;
    public int initBudget;

    public MineWorldDomain(int width, int height) {
        super(width, height);
        this.initBudget = 20;
        this.noBudgetTransition = false;
        
    }

    @Override
    public Domain generateDomain() {

        Domain domain = new SADomain();
        


        //Creates a new Attribute object
        Attribute xatt = new Attribute(domain, ATTX, Attribute.AttributeType.INT);
        xatt.setLims(0, this.width - 1);

        Attribute yatt = new Attribute(domain, ATTY, Attribute.AttributeType.INT);
        yatt.setLims(0., this.height - 1);
        
        Attribute bdtatt = new Attribute(domain, BUDGET, Attribute.AttributeType.INT);
        bdtatt.setLims(0., 3);        

        // Create coins attribute
        Attribute c1 = new Attribute(domain, C1, Attribute.AttributeType.BOOLEAN);
        Attribute c2 = new Attribute(domain, C2, Attribute.AttributeType.BOOLEAN);

        Attribute ltatt = new Attribute(domain, ATTLOCTYPE, Attribute.AttributeType.DISC);
        ltatt.setDiscValuesForRange(0, numLocationTypes - 1, 1);

        ObjectClass agentClass = new ObjectClass(domain, CLASSAGENT);
        agentClass.addAttribute(xatt);
        agentClass.addAttribute(yatt);

        // Add coins attribute
        agentClass.addAttribute(c1);
        agentClass.addAttribute(c2);
        
        // Add budget attribute
        agentClass.addAttribute(bdtatt);

        ObjectClass locationClass = new ObjectClass(domain, CLASSLOCATION);
        locationClass.addAttribute(xatt);
        locationClass.addAttribute(yatt);

        locationClass.addAttribute(ltatt);

        int[][] cmap = this.getMap();

        new MovementAction(ACTIONNORTH, domain, this.transitionDynamics[0], cmap);
        new MovementAction(ACTIONSOUTH, domain, this.transitionDynamics[1], cmap);
        new MovementAction(ACTIONEAST, domain, this.transitionDynamics[2], cmap);
        new MovementAction(ACTIONWEST, domain, this.transitionDynamics[3], cmap);

        new AtLocationPF(PFATLOCATION, domain, new String[]{CLASSAGENT, CLASSLOCATION});
        new HasAllCoinsPF(PFHASALLCOINSORNOBUDGET, domain, new String[]{CLASSAGENT, CLASSLOCATION});
        
        new WallToPF(PFWALLNORTH, domain, new String[]{CLASSAGENT}, 0);
        new WallToPF(PFWALLSOUTH, domain, new String[]{CLASSAGENT}, 1);
        new WallToPF(PFWALLEAST, domain, new String[]{CLASSAGENT}, 2);
        new WallToPF(PFWALLWEST, domain, new String[]{CLASSAGENT}, 3);

        return domain;
    }

    /**
     * Sets the first agent object in s to the specified x and y position.
     *
     * @param s the state with the agent whose position to set
     * @param x the x position of the agent
     * @param y the y position of the agent
     */
    public static void setAgent(State s, int x, int y) {
        ObjectInstance o = s.getObjectsOfClass(CLASSAGENT).get(0);

        o.setValue(ATTX, x);
        o.setValue(ATTY, y);

        // Set coins initial state
        o.setValue(C1, 0);
        o.setValue(C2, 0);
        // TO-DO:   este numero debería ajustarse en funcion del 
        //          la cantidad inicial
        o.setValue(BUDGET, 2);
    }

    /**
     * Sets the i'th location object to the specified x and y position. The
     * location type will be set to 0.
     *
     * @param s the state with the location object
     * @param i specifies which location object index to set
     * @param x the x position of the location
     * @param y the y position of the location
     */
    public static void setLocation(State s, int i, int x, int y) {
        ObjectInstance o = s.getObjectsOfClass(CLASSLOCATION).get(i);

        o.setValue(ATTX, x);
        o.setValue(ATTY, y);

        o.setValue(ATTLOCTYPE, 0);
    }

    /**
     * Will set the map of the world to no walls and 2 mines and two coins
     */
    public void setMapToTwoCoinsAndTwoMines() {
        
        this.width = 11;
        this.height = 11;
        this.makeEmptyMap();

        this.coins = new HashMap<>();
        this.mines = new HashMap<>();

        Integer[] coinCoordinates1 = {7, 2};
        Integer[] coinCoordinates2 = {3, 3};

        Integer[] mineCoordinates1 = {5, 5};
        Integer[] mineCoordinates2 = {7, 7};

        this.coins.put(C1, coinCoordinates1);
        this.coins.put(C2, coinCoordinates2);

        this.mines.put("m1", mineCoordinates1);
        this.mines.put("m2", mineCoordinates2);

        this.coinVal = 10;
        this.mineVal = -5;
        this.noBudgetVal = -30;
        
        this.budget = this.initBudget;
    }

    /**
     * Attempts to move the agent into the given position, taking into account
     * walls and blocks
     *
     * @param s the current state
     * @param xd the attempted new X position of the agent
     * @param yd the attempted new Y position of the agent
     */
    protected void move(State s, int xd, int yd, int[][] map) {

        ObjectInstance agent = s.getObjectsOfClass(CLASSAGENT).get(0);
        int ax = agent.getIntValForAttribute(ATTX);
        int ay = agent.getIntValForAttribute(ATTY);

        int nx = ax + xd;
        int ny = ay + yd;

        //hit wall, so do not change position
        if (nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length || map[nx][ny] == 1
                || (xd > 0 && (map[ax][ay] == 3 || map[ax][ay] == 4)) || (xd < 0 && (map[nx][ny] == 3 || map[nx][ny] == 4))
                || (yd > 0 && (map[ax][ay] == 2 || map[ax][ay] == 4)) || (yd < 0 && (map[nx][ny] == 2 || map[nx][ny] == 4))) {
            nx = ax;
            ny = ay;
        }

        agent.setValue(ATTX, nx);
        agent.setValue(ATTY, ny);

        HashMap<String, Integer[]> coinCoordinates = this.getCoinCoordinates();

        // Verifica si encontró una moneda
        for (String m : coinCoordinates.keySet()) {
            if (coinCoordinates.get(m)[0] == nx && coinCoordinates.get(m)[1] == ny) {
                //System.out.println("Found coin: " + m + " at " + nx + "," + ny);
                agent.setValue(m, 1);
                //System.out.println(agent.getObjectDescription());
            }
        }

        //System.out.println("MineWorldDomain::move. budget: " + this.getBudget());
    }
    
    public void updateBudgetState(State s){
        ObjectInstance agent = s.getObjectsOfClass(CLASSAGENT).get(0);
        
        int budgetStateBefore = agent.getIntValForAttribute(BUDGET);
        System.out.println("MineWorldDomain::updateBudgetState. before update budget: "+this.getBudget()+" "+budgetStateBefore);
        
        agent.setValue(BUDGET, this.calculateBudgetState());
        
        int budgetStateAfter = agent.getIntValForAttribute(BUDGET);
        System.out.println("MineWorldDomain::updateBudgetState. after update budget: "+this.getBudget()+" "+budgetStateAfter);
     
    }
    
    public int calculateBudgetState(){
        
        int budgetVal = this.budget;
        System.out.println("MineWorldDomain::calculateBudgetState. Budget: "+budgetVal);
        int budgetState = 0;

        if(budgetVal > 0 && budgetVal <= 10){
                budgetState = 1;
            }
        if(budgetVal > 10 && budgetVal <=20){
                budgetState = 2;
            }
        if(budgetVal > 20 ){
                budgetState = 3;
            }   
        return budgetState;
    }

    public HashMap<String, Integer[]> getCoinCoordinates() {
        return this.coins;
    }

    public HashMap<String, Integer[]> getMineCoordinates() {
        return this.mines;
    }
    
    public int resetBudget(State s){
        this.budget = this.initBudget;
        this.updateBudgetState(s);
        return this.budget;
    }

    public int updateBudget(int value, State s) {
        this.budget += value;
        this.updateBudgetState(s);
        return this.budget;
    }

    public int getBudget() {
        return this.budget;
    }
    
    public boolean getNoBudgetTransition(){
        return this.noBudgetTransition;
    }

    public int getCoinVal() {
        return this.coinVal;
    }

    public int getMineVal() {
        return this.mineVal;
    }
    
    public int getNoBudgetVal(){
        return this.noBudgetVal;
    }

    public class HasAllCoinsPF extends PropositionalFunction {

        /**
         * Initializes with given name domain and parameter object class types
         *
         * @param name name of function
         * @param domain the domain of the function
         * @param parameterClasses the object class types for the parameters
         */
        public HasAllCoinsPF(String name, Domain domain, String[] parameterClasses) {
            super(name, domain, parameterClasses);
        }

        @Override
        public boolean isTrue(State st, String[] params) {

            ObjectInstance agent = st.getObject(params[0]);
            
            boolean c1 = agent.getBooleanValForAttribute("c1");
            boolean c2 = agent.getBooleanValForAttribute("c2");
            int budgs = agent.getIntValForAttribute("budget");
            
//            System.out.println(c1 + " " + c2 + " " + (budgs == 0));
//            System.out.println(c1 && c2);
//            System.out.println(budgs == 0);
//            System.exit(0);

            if((c1 && c2) || (budgs == 0)){
                System.out.println("HasAllCoinsPF::isTrue. Coins: "+(c1 && c2)+" Budgs: "+(budgs == 0)+" Ending... ");
                return true;
            }
            return false;
        }

    }
    

}
