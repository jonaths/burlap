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
import burlap.oomdp.core.State;
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
    
    public HashMap<String,Integer[]> coins;
    public HashMap<String,Integer[]> mines;
    public int coinVal;
    public int mineVal;

    public MineWorldDomain(int width, int height) {
        super(width, height);
    }

    @Override
    public Domain generateDomain() {

        Domain domain = new SADomain();

        //Creates a new Attribute object
        Attribute xatt = new Attribute(domain, ATTX, Attribute.AttributeType.INT);
        xatt.setLims(0, this.width - 1);

        Attribute yatt = new Attribute(domain, ATTY, Attribute.AttributeType.INT);
        yatt.setLims(0., this.height - 1);

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

        ObjectClass locationClass = new ObjectClass(domain, CLASSLOCATION);
        locationClass.addAttribute(xatt);
        locationClass.addAttribute(yatt);

        // Add coins attribute
        locationClass.addAttribute(c1);
        locationClass.addAttribute(c2);

        locationClass.addAttribute(ltatt);

        int[][] cmap = this.getMap();

        new MovementAction(ACTIONNORTH, domain, this.transitionDynamics[0], cmap);
        new MovementAction(ACTIONSOUTH, domain, this.transitionDynamics[1], cmap);
        new MovementAction(ACTIONEAST, domain, this.transitionDynamics[2], cmap);
        new MovementAction(ACTIONWEST, domain, this.transitionDynamics[3], cmap);

        new AtLocationPF(PFATLOCATION, domain, new String[]{CLASSAGENT, CLASSLOCATION});

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

        // Set coins initial state
        o.setValue(C1, 0);
        o.setValue(C2, 0);
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
        
        Integer[] coinCoordinates1 = {1,2};
        Integer[] coinCoordinates2 = {3,3};
        
        Integer[] mineCoordinates1 = {5,5};
        Integer[] mineCoordinates2 = {7,7};
        
        this.coins.put(C1,coinCoordinates1);
        this.coins.put(C2,coinCoordinates2);
        
        this.mines.put("m1",mineCoordinates1);
        this.mines.put("m2",mineCoordinates2);
        
        this.coinVal = 10;
        this.mineVal = -5;
    }
    
    public HashMap<String,Integer[]> getCoinCoordinates(){
        return this.coins;
    }
    
    public HashMap<String,Integer[]> getMineCoordinates(){
        return this.mines;
    }
    
    public int getCoinVal(){
        return this.coinVal;
    }
    
    public int getMineVal(){
        return this.mineVal;
    }

}
