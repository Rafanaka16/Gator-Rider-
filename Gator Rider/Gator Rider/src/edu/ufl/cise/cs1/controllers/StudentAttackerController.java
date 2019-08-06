package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.controllers.DefenderController;
import game.models.*;
import game.system.Pair;
import java.util.List;

public final class StudentAttackerController implements AttackerController {
    public void init(Game game) { }

    public void shutdown(Game game) { }

    public int update(Game game, long timeDue) {

        // generating Pair data structure to return both defender and Integer value
        Pair<Defender,Integer> result = nearestDefender(game, false);
        Defender vulnerableDefender = result.first();
        int vulnerableDefenderDistance = result.second();

        result = nearestDefender(game, true);
        Defender normalDefender = result.first();
        int normalDefenderDistance = result.second();

        // checking if there is a defender near me and its not vulnerable
        if(normalDefender != null && (normalDefenderDistance <= vulnerableDefenderDistance && normalDefenderDistance <= 6)){
            return game.getAttacker().getNextDir(normalDefender.getLocation(), false);
        }// if above is false, then there is no threat OR is safe to chase vulnerable
        else if(vulnerableDefender != null){
            return game.getAttacker().getNextDir(vulnerableDefender.getLocation(), true);
        }else if (powerPillNear(game)){// if there is a power pill near the attacker, stay there
            if(normalDefenderDistance <= 6){//if there is a defender in the way of the vulnerable one, flee instead of chasing
                return eatPill(game);
            }
            return game.getAttacker().getReverse();
        }
        else if(normalDefenderDistance <= 6){
            return game.getAttacker().getNextDir(normalDefender.getLocation(), false);
        }
        else return eatPill(game); //eat power pills

    }


    private int eatPill(Game game) {// sends the attacker to eat power pills and when there are none, eat regular pills

        Node _nodePowerPill = game.getAttacker().getTargetNode(game.getPowerPillList(), true);
        Node _nodePill = game.getAttacker().getTargetNode(game.getPillList(), true);
        try {
            return game.getAttacker().getNextDir(_nodePowerPill, true);
        } catch (NullPointerException e) {
            return game.getAttacker().getNextDir(_nodePill, true);
        }
    }


    private Pair<Defender,Integer> nearestDefender(Game game, boolean bool) { // true for distance from defender to attacker and false for the opposite

        Defender defender = null;
        int tempInt = Integer.MAX_VALUE;

        Node attackerLocation = game.getAttacker().getLocation();
        if (bool) {//checking for the boolean that i passed in when i called the method in Update
            for (int i = 0; i < 4; i++) {

                Defender temp = game.getDefenders().get(i);
                if (tempInt > temp.getPathTo(attackerLocation).size() && !temp.isVulnerable() && temp.getLairTime() == 0 ) {
                    defender = temp;
                    tempInt =   temp.getPathTo((attackerLocation)).size();
                }
            }
        } else {// else for when i need the path from the attacker to the closest defender that is vulnerable

            for (int i = 0; i < 4; i++) {

                Defender temp = game.getDefenders().get(i);
                if (tempInt > attackerLocation.getPathDistance(temp.getLocation()) && temp.isVulnerable()) {
                    defender = temp;
                    tempInt = attackerLocation.getPathDistance(temp.getLocation());

                }
            }
        }
        return new Pair<>(defender,tempInt); // return the pair, with the defender and the distance
    }



    private boolean powerPillNear(Game game) { // checking for a power pill near the attacker
        List<Node> nodes =  game.getAttacker().getLocation().getNeighbors();
        for(int i = 0; i < nodes.size(); i ++) {
                if (nodes.get(i) != null && game.checkPowerPill(nodes.get(i))) {
                    return true;
                }
        }
       return false;

    }



}







