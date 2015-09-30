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
import burlap.oomdp.singleagent.SADomain;
import java.util.HashMap;

/**
 *
 * @author jonathan
 */
public class MineWorldDomain extends GridWorldDomain {

    public static final String C1 = "c1";
    public static final String C2 = "c2";
    public static final String NB = "nobudget";
    public static final String BUDGETSTATE = "budgetstate";
    public static final String  PFHASALLCOINSORNOBUDGET = "hasAllCoinsOrNoBudget";
    
    public HashMap<String, Integer[]> mines;
    public HashMap<String, Integer[]> coins;
    public int coinVal;
    public int mineVal;
    public int budget;

    public MineWorldDomain(int width, int height) {
        super(width, height);
    }

    @Override
    public Domain generateDomain() {

        Domain domain = new SADomain();

        //Create attributes
        Attribute xatt = new Attribute(domain, ATTX, Attribute.AttributeType.INT);
        xatt.setLims(0, this.width - 1);

        Attribute yatt = new Attribute(domain, ATTY, Attribute.AttributeType.INT);
        yatt.setLims(0., this.height - 1);

        Attribute c1 = new Attribute(domain, C1, Attribute.AttributeType.BOOLEAN);
        Attribute c2 = new Attribute(domain, C2, Attribute.AttributeType.BOOLEAN);
        Attribute nb = new Attribute(domain, NB, Attribute.AttributeType.BOOLEAN);
        Attribute budgetstate = new Attribute(domain, BUDGETSTATE, Attribute.AttributeType.INT);
        budgetstate.setLims(0, 3);

        Attribute ltatt = new Attribute(domain, ATTLOCTYPE, Attribute.AttributeType.DISC);
        ltatt.setDiscValuesForRange(0, numLocationTypes - 1, 1);

        // Create agentClass and assign attributes
        ObjectClass agentClass = new ObjectClass(domain, CLASSAGENT);
        agentClass.addAttribute(xatt);
        agentClass.addAttribute(yatt);
        agentClass.addAttribute(c1);
        agentClass.addAttribute(c2);
        agentClass.addAttribute(nb);
        agentClass.addAttribute(budgetstate);

        // Create locationClass and assign attributes
        ObjectClass locationClass = new ObjectClass(domain, CLASSLOCATION);
        locationClass.addAttribute(xatt);
        locationClass.addAttribute(yatt);
        locationClass.addAttribute(ltatt);

        this.budget = 0;

        int[][] cmap = this.getMap();

        new MovementAction(ACTIONNORTH, domain, this.transitionDynamics[0], cmap);
        new MovementAction(ACTIONSOUTH, domain, this.transitionDynamics[1], cmap);
        new MovementAction(ACTIONEAST, domain, this.transitionDynamics[2], cmap);
        new MovementAction(ACTIONWEST, domain, this.transitionDynamics[3], cmap);

        new AtLocationPF(PFATLOCATION, domain, new String[]{CLASSAGENT, CLASSLOCATION});
        new HasAllCoinsOrNoBudgetPF(PFHASALLCOINSORNOBUDGET, domain, new String[]{CLASSAGENT, CLASSLOCATION});

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
     * @param budget the initial budget
     */
    public void setMWDAgent(State s, int x, int y, int budget) {
        ObjectInstance o = s.getObjectsOfClass(CLASSAGENT).get(0);

        o.setValue(ATTX, x);
        o.setValue(ATTY, y);
        o.setValue(C1, 0);
        o.setValue(C2, 0);
        o.setValue(NB, 0);
        this.budget = budget;
        setBudgetState(o);
        

        System.out.println(o.getObjectDescription() + "Budget: " + this.getBudget());
        //System.exit(0);
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
    public void setMWDLocation(State s, int i, int x, int y) {
        ObjectInstance o = s.getObjectsOfClass(CLASSLOCATION).get(i);

        o.setValue(ATTX, x);
        o.setValue(ATTY, y);
        o.setValue(ATTLOCTYPE, 0);
    }

    /**
     * Calcula nuevo state budget usando setBudget
     *
     * @param s
     * @param i
     * @param budget
     * @return
     */
    public int setBudgetState(State s, int i) {
        ObjectInstance o = s.getObjectsOfClass(CLASSAGENT).get(i);
//        System.out.println(s.getCompleteStateDescription());
        int budgetState = setBudgetState(o);
        if(budgetState == 1){
            
            System.out.println(s.getCompleteStateDescription());
            //System.exit(0);
        }
        return budgetState;

    }

    /**
     * Calcula nuevo state budget
     *
     * @param o
     * @param budget
     * @return
     */
    public int setBudgetState(ObjectInstance o) {
        int budgetState = calculateBudgetState();
        o.setValue(BUDGETSTATE, budgetState);
        return o.getDiscValForAttribute(BUDGETSTATE);
    }

    /**
     * Calcula el budget state en funcion del budget
     *
     * @param budget
     * @return
     */
    public int calculateBudgetState() {
        if (this.budget <= 0) {
            return 0;
        }
        if (this.budget > 0 && this.budget <= 10) {
            return 1;
        }
        if (this.budget > 10 && this.budget <= 20) {
            return 2;
        }
        if (this.budget > 20) {
            return 3;
        }
        // Si es cualquier otro número regresa -1 (error)
        return -1;
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

        Integer[] coinCoordinates1 = {1, 2};
        Integer[] coinCoordinates2 = {10, 10};

        Integer[] mineCoordinates1 = {5, 5};
        Integer[] mineCoordinates2 = {5, 3};

        this.coins.put(C1, coinCoordinates1);
        this.coins.put(C2, coinCoordinates2);

        this.mines.put("m1", mineCoordinates1);
        this.mines.put("m2", mineCoordinates2);

        this.coinVal = 10;
        this.mineVal = -5;
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
        
        this.setBudgetState(s,0);
        System.out.println("s: " + s.getCompleteStateDescription());

        boolean foundCoinFlag = false;
        boolean noBudgetFlag = false;

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
                //System.out.println("MineWorldDomain::move. Found coin: " + m + " at " + nx + "," + ny);
                foundCoinFlag = true;
                agent.setValue(m, 1);
                //System.out.println(agent.getObjectDescription());
            }
        }

        // Verifica si se va a quedar sin presupuesto
        if (foundCoinFlag && (this.getBudget() + this.getCoinVal()) >= 0) {
            noBudgetFlag = false;
        } else if ((this.getBudget() - 1) <= 0) {
            System.out.println("MineWorldDomain::move. noBudgetFlag is true. Budget: " + (this.getBudget()-1));
            noBudgetFlag = true;
            agent.setValue(NB, 1);
            //System.exit(0);
        }
        //System.out.println("s: " + s.getCompleteStateDescription());
    }

    public HashMap<String, Integer[]> getCoinCoordinates() {
        return this.coins;
    }

    public HashMap<String, Integer[]> getMineCoordinates() {
        return this.mines;
    }

    public int getCoinVal() {
        return this.coinVal;
    }

    public int getMineVal() {
        return this.mineVal;
    }

    public int getBudget() {
        return this.budget;
    }

    public int updateBudget(int update) {
        this.budget += update;
        return this.budget;
    }
    
public class HasAllCoinsOrNoBudgetPF extends PropositionalFunction {

        /**
         * Initializes with given name domain and parameter object class types
         *
         * @param name name of function
         * @param domain the domain of the function
         * @param parameterClasses the object class types for the parameters
         */
        public HasAllCoinsOrNoBudgetPF(String name, Domain domain, String[] parameterClasses) {
            super(name, domain, parameterClasses);
        }

        @Override
        public boolean isTrue(State st, String[] params) {

            ObjectInstance agent = st.getObject(params[0]);
            
            boolean c1 = agent.getBooleanValForAttribute("c1");
            boolean c2 = agent.getBooleanValForAttribute("c2");
            boolean nb = agent.getBooleanValForAttribute(NB);
            
//            System.out.println(c1 + " " + c2 + " " + (budgs == 0));
//            System.out.println(c1 && c2);
//            System.out.println(budgs == 0);
//            System.exit(0);

            if((c1 && c2) || nb){
                System.out.println("HasAllCoinsPF::isTrue. coins: " + (c1 && c2)+", nb: " + nb + " Ending... ");
                System.out.println("HasAllCoinsPF::isTrue. description: " + st.getCompleteStateDescription());
                return true;
            }
            return false;
        }

    }
    

}
