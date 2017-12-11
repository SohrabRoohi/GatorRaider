package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;

import static game.models.Game.NUM_DEFENDER;



//By Sohrab Roohi, Tyler Knightes, Yadi Qian
public final class StudentController implements DefenderController {
	public void init(Game game) {
	}

	public void shutdown(Game game) {
	}

	public int[] update(Game game, long timeDue) {
		int[] actions = new int[Game.NUM_DEFENDER];
		List<Defender> enemies = game.getDefenders();

		//Chooses a random LEGAL action if required. Could be much simpler by simply returning
		//any random number of all of the ghosts
		actions[0] = firstDefenderAction(enemies.get(0), game);
		actions[1] = DefenderAction(enemies.get(1), game);
		actions[2] = thirdDefenderAction(enemies.get(2), game);
		actions[3] = DefenderAction(enemies.get(3), game);
		return actions;
	}

	public int DefenderAction(Defender defender, Game game) {
		int attackerX = game.getAttacker().getLocation().getX(); //Gets attacker X position
		int attackerY = game.getAttacker().getLocation().getY(); //Gets attacker Y position
		int defenderX = defender.getLocation().getX(); //Gets defender X position
		int defenderY = defender.getLocation().getY(); //Gets defender Y position

		int xDistance = Math.abs(attackerX - defenderX); //Calculates horizontal distance from defender to pacman
		int yDistance = Math.abs(attackerY - defenderY); //Calculates vertical distatnce from defender to pacman

		List<Integer> possibleDirs = defender.getPossibleDirs(); //Gets possible next directions of defender
		double attackerDistanceFromPill = distanceToPill(game); //Finds distance from pacman to closest powerpill
		boolean vulnerable = defender.isVulnerable(); //Checks to see if defender is vulnerable
		if (possibleDirs.size() != 0) {
			// 0 is up
			// 1 is right
			// 2 is down
			// 3 is left

			if (defenderX > attackerX && possibleDirs.contains(3) && xDistance >= yDistance) { //Prioritize moving horizontally
				if ((vulnerable || attackerDistanceFromPill < 1) && possibleDirs.contains(1)) { //Go opposite direction
					return 1;
				}
				return 3;
			} else if (defenderX < attackerX && possibleDirs.contains(1) && xDistance >= yDistance) { //Prioritize moving horizontally
				if ((vulnerable || attackerDistanceFromPill < 1) && possibleDirs.contains(3)) { //Go opposite direction
					return 3;
				}
				return 1;
			} else if ((defenderY > attackerY && possibleDirs.contains(0) && yDistance >= xDistance) || (yDistance <= xDistance && defenderY > attackerY && !possibleDirs.contains(1) && !possibleDirs.contains(3))) { //Prioritize moving vertically
				if ((vulnerable || attackerDistanceFromPill < 1) && possibleDirs.contains(2)) { //Go opposite direction
					return 2;
				}
				return 0;
			} else if (defenderY < attackerY && possibleDirs.contains(2) && yDistance >= xDistance || (yDistance <= xDistance && defenderY < attackerY && !possibleDirs.contains(1) && !possibleDirs.contains(3))) { //Prioritize moving vertically
				if ((vulnerable || attackerDistanceFromPill < 1) && possibleDirs.contains(0)) { //Go opposite direction
					return 0;
				}
				return 2;
			} else {
				return -1;
			}
		}
		else {
			return -1;
		}
	}

	public int firstDefenderAction(Defender defender, Game game) {
		int direction;
		boolean approach = !defender.isVulnerable();
		List<Node> pillLocations = game.getPowerPillList();
		Node aLocation = game.getAttacker().getLocation(); //location of the attacker
		Node dLocation = defender.getLocation(); //location of the defender
		double distanceToAttacker = Math.sqrt(Math.pow(aLocation.getX() - dLocation.getX(), 2) + Math.pow(aLocation.getY() - dLocation.getY(), 2)); //calculate the distance between defender and attacker with the distance formula
		double distanceToPill = 5000000; //choose a large number to compare with
		int pillIndex = -1;
		for(int i = 0; i < pillLocations.size(); i++) {
			double distance = distanceToPill(game);
			if(distance < distanceToPill) {
				distanceToPill = distance;
				pillIndex = i; //find the closest power pill
			}
		}
		if(pillIndex != -1 && distanceToAttacker > 40) {
			direction = defender.getNextDir(pillLocations.get(pillIndex), approach); //orient the defender towards the closest power pill when the distance is more than 40 unit distance away
		}
		else {
			direction = defender.getNextDir(aLocation, approach); //orient the defender towards the attacker when it's not vulnerable mode
		}
		if(distanceToAttacker < 30) {
			direction = trappedAlternateDirection(defender, game); //if the defender is less than 30 unit distance away from the attacker, get new direction to trap the attacker from the other side
		}
		return direction;
	}



	public int thirdDefenderAction(Defender defender, Game game){
		int shortdistOtherDefendtoAttack = 100;
		int otherDefenderIndex = 0;

		for(int i = 0; i < NUM_DEFENDER; i++){
			if (i == 2){ //If i = 2, that represents this defender, so it should be skipped, as it is not necessary to find the distance to itself
				i = 3;
				continue;
			}
			if (game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation()) < shortdistOtherDefendtoAttack){ //If the distance from defender i is shorter than the current shortest distance from a defender to the attacker, this executes
				shortdistOtherDefendtoAttack = game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation()); //The defender distance to attacker from above line is set to the variable shortdistOtherDefendtoAttack
				otherDefenderIndex = i; //Records the index of the closest defender
			}
		}

		int defDirection = defender.getDirection(); //Direction of this defender
		int otherDefDirection = game.getDefender(otherDefenderIndex).getDirection(); //Direction of closest other defender
		int attackerDirection = game.getAttacker().getDirection(); //Direction of attacker

		if ((shortdistOtherDefendtoAttack < 4) && (attackerDirection == otherDefDirection) && (otherDefDirection == defDirection) && !defender.isVulnerable() ){ //If statement executes only if the 4 conditions are true
			return defender.getReverse();
		}
		else if(defender.isVulnerable() && game.getCurMaze().getNumberPowerPills() != 0){ //Uses current maze state and defender state. If vulnerable or if there are still power pills on the maze, the defender runs away
			return defender.getNextDir(game.getAttacker().getLocation(), false);
		}
		else{
			return defender.getNextDir(game.getAttacker().getLocation(), true); //Otherwise, the defender will chase the attacker
		}

	}

	public double distanceToPill(Game game) {
		Node aLocation = game.getAttacker().getLocation(); //Get attacker location
		List<Node> pillLocations = game.getPowerPillList(); //Get powerpill list
		double distance = 50000000; //Sets large number for comparison
		for(int i = 0; i < pillLocations.size(); i++) { //Finds distance to closest powerpill
			double temp = Math.sqrt(Math.pow(aLocation.getX() - pillLocations.get(i).getX(), 2) + Math.pow(aLocation.getY() - pillLocations.get(i).getY(), 2));
			if(temp < distance) {
				distance = temp;
			}
		}
		return distance; //Returns distance
	}

	public int trappedAlternateDirection(Defender defender, Game game) {
		List<Defender> defenderList = game.getDefenders();
		boolean isClose = false;
		for(int i = 0; i < 4; i++) {
			if(defender != defenderList.get(i)) {
				if(distanceToAttacker(defenderList.get(i), game) < 5 && distanceToAttacker(defender, game) < 20) { //Checks to see if another defender is close to the pacman
					isClose = true;
				}
			}
		}
		int direction = defender.getNextDir(game.getAttacker().getLocation(), true);
		ArrayList<Integer> nonDirection = new ArrayList<Integer>();
		for(int i = 0; i < 4; i++) {
			if(i != direction)  {
				nonDirection.add(direction); //Adds directions that are alternate
			}
		}
		if(isClose) {
			List<Integer> possibleDirections = defender.getPossibleDirs();
			Node aLocation = game.getAttacker().getLocation();
			int aX = aLocation.getX();
			int aY = aLocation.getY();
			int dX = defender.getLocation().getX();
			int dY = defender.getLocation().getY();
			for (int i = 0; i < nonDirection.size(); i++) {
				if (possibleDirections.contains(nonDirection.get(i)) && !isOppositeDirection(nonDirection.get(i), dX, dY, aX, aY)) { //Finds another direction to trap the pacman
					direction = nonDirection.get(i);
				}
			}
		}
		return direction;

	}

	public double distanceToAttacker(Defender defender, Game game) {
		Node aLocation = game.getAttacker().getLocation(); //Gets attacker location
		int aX = aLocation.getX();
		int aY = aLocation.getY();
		int dX = defender.getLocation().getX();
		int dY = defender.getLocation().getY();
		double distance = Math.sqrt(Math.pow(aX - dX, 2) + Math.pow(aY - dY, 2)); //Uses distance formula to find distance from defender to attacker
		return distance;
	}

	boolean isOppositeDirection(int direction, int defenderX, int defenderY, int attackerX, int attackerY) {
		//Checks to see if going a certain direction will go away from the pacman
		if (direction == 0) {
			int upDifference = Math.abs(defenderY - attackerY) - Math.abs(defenderY - 1 - attackerY);
			if(upDifference < 0) {
				return true;
			}
			return false;
		}
		else if(direction == 1) {
			int rightDifference = Math.abs(defenderX - attackerX) - Math.abs(defenderX + 1 - attackerX);
			if(rightDifference < 0) {
				return true;
			}
			return false;
		}
		else if(direction == 2) {
			int downDifference = Math.abs(defenderY - attackerY) - Math.abs(defenderY + 1 - attackerY);
			if(downDifference < 0) {
				return true;
			}
			return false;
		}
		else if(direction == 3) {
			int leftDifference = Math.abs(defenderX - attackerX) - Math.abs(defenderX - 1 - attackerX);
			if(leftDifference < 0) {
				return true;
			}
			return false;
		}
		return false;
	}

}
