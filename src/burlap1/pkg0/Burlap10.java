/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burlap1.pkg0;

import burlap.MineWorldDomain;

import burlap.behavior.singleagent.*;
import burlap.domain.singleagent.gridworld.*;
import burlap.oomdp.core.*;
import burlap.oomdp.singleagent.*;
import burlap.oomdp.singleagent.common.*;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.behavior.singleagent.learning.*;
import burlap.behavior.singleagent.learning.tdmethods.*;
import burlap.behavior.singleagent.planning.*;
import burlap.behavior.singleagent.planning.deterministic.*;
import burlap.oomdp.visualizer.Visualizer;
import burlap.oomdp.auxiliary.StateGenerator;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.ConstantStateGenerator;
import burlap.behavior.singleagent.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.statehashing.DiscreteMaskHashingFactory;
import burlap.oomdp.singleagent.common.VisualActionObserver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jonathan
 */
public class Burlap10 {

    MineWorldDomain gwdg;
    Domain domain;
    StateParser sp;
    RewardFunction rf;
    TerminalFunction tf;
    TerminalFunction nobudgetTF;
    StateConditionTest goalCondition;
    State initialState;
    DiscreteStateHashFactory hashingFactory;
    int initialBudget;

    public static void main(String[] args) {
        Burlap10 example = new Burlap10();
        String outputPath = "output/";

        example.QLearningExample(outputPath);

        //run the visualizer (only use if you don't use the experiment plotter example)
        example.visualize(outputPath);
    }

    public Burlap10() {

        //create the domain
        gwdg = new MineWorldDomain(11, 11);
        gwdg.setMapToTwoCoinsAndTwoMines();
        domain = gwdg.generateDomain();
        
        //create the state parser
        sp = new GridWorldStateParser(domain);

        //define the task
        
//         rf = new UniformCostRF();
        rf = new UniformCostPlusMinesRF(gwdg);
        
//        tf = new SinglePFTF(domain.getPropFunction(MineWorldDomain.PFATLOCATION));
        tf = new SinglePFTF(domain.getPropFunction(MineWorldDomain.PFHASALLCOINSORNOBUDGET));
        
        goalCondition = new TFGoalCondition(tf);

        //set up the initial state of the task
        initialState = MineWorldDomain.getOneAgentOneLocationState(domain);
        gwdg.setMWDAgent(initialState, 0, 0, 20);
        gwdg.setMWDLocation(initialState, 0, 10, 10);
        
        List<Attribute> toObserve = new ArrayList<>();

        //set up the state hashing system
        hashingFactory = new DiscreteMaskHashingFactory();
        hashingFactory.setAttributesForClass(MineWorldDomain.CLASSAGENT,
                domain.getObjectClass(MineWorldDomain.CLASSAGENT).attributeList);

        //add visual observer
        VisualActionObserver observer = new VisualActionObserver(domain,
                MineWorldVisualizer.getVisualizer(gwdg.getMap()));
        ((SADomain) this.domain).setActionObserverForAllAction(observer);
        observer.initGUI();

    }

    public void visualize(String outputPath) {
        Visualizer v = GridWorldVisualizer.getVisualizer(gwdg.getMap());
        EpisodeSequenceVisualizer evis = new EpisodeSequenceVisualizer(v,
                domain, sp, outputPath);
    }

    public void QLearningExample(String outputPath) {
        
        

        if (!outputPath.endsWith("/")) {
            outputPath = outputPath + "/";
        } 

        //discount= 0.99; initialQ=0.0; learning rate=0.9
        LearningAgent agent = new QLearning(domain, rf, tf, 0.99, hashingFactory, 0., 0.9);

        //run learning for 100 episodes
        for (int i = 0; i < 100; i++) {
            int totalReward = 0;
            EpisodeAnalysis ea = agent.runLearningEpisodeFrom(initialState);
            ea.writeToFile(String.format("%se%03d", outputPath, i), sp);
            
            for(int j = 1; j < ea.numTimeSteps() ; j++){
                totalReward += ea.getReward(j);
            }
            System.out.println(" Step: " + i + " Timesteps: " + ea.numTimeSteps() + " Reward: " + totalReward);
        }

    }

    public void experimenterAndPlotter() {

        //custom reward function for more interesting results
        final RewardFunction rf = new GoalBasedRF(this.goalCondition, 5., -0.1);

        /**
         * Create factories for Q-learning agent and SARSA agent to compare
         */
        LearningAgentFactory qLearningFactory = new LearningAgentFactory() {

            @Override
            public String getAgentName() {
                return "Q-learning";
            }

            @Override
            public LearningAgent generateAgent() {
                return new QLearning(domain, rf, tf, 0.99, hashingFactory, 0.3, 0.1);
            }
        };

        LearningAgentFactory sarsaLearningFactory = new LearningAgentFactory() {

            @Override
            public String getAgentName() {
                return "SARSA";
            }

            @Override
            public LearningAgent generateAgent() {
                return new SarsaLam(domain, rf, tf, 0.99, hashingFactory, 0.0, 0.1, 1.);
            }
        };

        StateGenerator sg = new ConstantStateGenerator(this.initialState);

        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter((SADomain) this.domain,
                rf, sg, 10, 100, qLearningFactory, sarsaLearningFactory);

        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOSTRECENTANDAVERAGE,
                PerformanceMetric.CUMULATIVESTEPSPEREPISODE,
                PerformanceMetric.AVERAGEEPISODEREWARD);

        exp.startExperiment();

        exp.writeStepAndEpisodeDataToCSV("expData");

    }

}
