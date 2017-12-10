package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;

import static game.models.Game.NUM_DEFENDER;

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
		actions[0] = thirdDefenderAction(enemies.get(0), game);
		actions[1] = DefenderAction(enemies.get(1), game);
		actions[2] = fourthDefenderAction(enemies.get(2), game);
		actions[3] = DefenderAction(enemies.get(3), game);
		return actions;
	}

	public int DefenderAction(Defender defender, Game game) {
		int attackerX = game.getAttacker().getLocation().getX();
		int attackerY = game.getAttacker().getLocation().getY();
		int defenderX = defender.getLocation().getX();
		int defenderY = defender.getLocation().getY();

		int xDistance = Math.abs(attackerX - defenderX);
		int yDistance = Math.abs(attackerY - defenderY);

		List<Integer> possibleDirs = defender.getPossibleDirs();
		if (possibleDirs.size() != 0)
			// 0 is up
			// 1 is right
			// 2 is down
			// 3 is left

			if (defenderX > attackerX && possibleDirs.contains(3) && xDistance >= yDistance) {
				if((defender.isVulnerable() || distanceToPill(game) < 15) && possibleDirs.contains(1)) {
					return 1;
				}
				return 3;
			} else if (defenderX < attackerX && possibleDirs.contains(1) && xDistance >= yDistance) {
				if((defender.isVulnerable() || distanceToPill(game) < 15) && possibleDirs.contains(3)) {
					return 3;
				}
				return 1;
			} else if ((defenderY > attackerY && possibleDirs.contains(0) && yDistance >= xDistance) || (yDistance <= xDistance && defenderY > attackerY && !possibleDirs.contains(1) && !possibleDirs.contains(3))) {
				if((defender.isVulnerable() || distanceToPill(game) < 15) && possibleDirs.contains(2)) {
					return 2;
				}
				return 0;
			} else if (defenderY < attackerY && possibleDirs.contains(2) && yDistance >= xDistance || (yDistance <= xDistance && defenderY < attackerY && !possibleDirs.contains(1) && !possibleDirs.contains(3))) {
				if((defender.isVulnerable() || distanceToPill(game) < 15) && possibleDirs.contains(0)) {
					return 0;
				}
				return 2;
			} else {
				return -1;
			}
		else
			return -1;
	}

	public int thirdDefenderAction(Defender defender, Game game) {
		int direction;
		boolean approach = !defender.isVulnerable();
		List<Node> pillLocations = game.getPowerPillList();
		Node aLocation = game.getAttacker().getLocation();
		Node dLocation = defender.getLocation();
		double distanceToAttacker = Math.sqrt(Math.pow(aLocation.getX() - dLocation.getX(), 2) + Math.pow(aLocation.getY() - dLocation.getY(), 2));
		double distanceToPill = 5000000;
		int pillIndex = -1;
		for(int i = 0; i < pillLocations.size(); i++) {
			double distance = distanceToPill(game);
			if(distance < distanceToPill) {
				distanceToPill = distance;
				pillIndex = i;
			}
		}
		if(pillIndex != -1 && distanceToAttacker > 40) {
			direction = defender.getNextDir(pillLocations.get(pillIndex), approach);
		}
		else {
			direction = defender.getNextDir(aLocation, approach);
		}
		if(distanceToAttacker < 30) {
			direction = trappedAlternateDirection(defender, game);
		}
		return direction;
	}



	public int fourthDefenderAction(Defender defender, Game game){
		int shortdistOtherDefendtoAttack = 100;
		int otherDefenderIndex = 0;

		for(int i = 0; i < NUM_DEFENDER; i++){
			if (i == 2){
				i = 3;
				continue;
			}
			if (game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation()) < shortdistOtherDefendtoAttack){
				shortdistOtherDefendtoAttack = game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation());
				otherDefenderIndex = i;
			}
		}

		int defDirection = defender.getDirection();
		int otherDefDirection = game.getDefender(otherDefenderIndex).getDirection();
		int attackerDirection = game.getAttacker().getDirection();

		if ((shortdistOtherDefendtoAttack < 4) && (attackerDirection == otherDefDirection) && (otherDefDirection == defDirection) && !defender.isVulnerable() ){
			return defender.getReverse();
		}
		else if(defender.isVulnerable() && game.getCurMaze().getNumberPowerPills() != 0){
			return defender.getNextDir(game.getAttacker().getLocation(), false);
		}
		else{
			return defender.getNextDir(game.getAttacker().getLocation(), true);
		}
		
	}

	public double distanceToPill(Game game) {
		Node aLocation = game.getAttacker().getLocation();
		List<Node> pillLocations = game.getPowerPillList();
		double distance = 50000000;
		for(int i = 0; i < pillLocations.size(); i++) {
			distance = Math.sqrt(Math.pow(aLocation.getX() - pillLocations.get(i).getX(), 2) + Math.pow(aLocation.getY() - pillLocations.get(i).getY(), 2));
		}
		return distance;
	}

	public int trappedAlternateDirection(Defender defender, Game game) {
		List<Defender> defenderList = game.getDefenders();
		boolean isClose = false;
		for(int i = 0; i < 4; i++) {
			if(defender != defenderList.get(i)) {
				if(distanceToAttacker(defenderList.get(i), game) < 5 && distanceToAttacker(defender, game) < 20) {
					isClose = true;
				}
			}
		}
		int direction = defender.getNextDir(game.getAttacker().getLocation(), true);
		ArrayList<Integer> nonDirection = new ArrayList<Integer>();
		for(int i = 0; i < 4; i++) {
			if(i != direction)  {
				nonDirection.add(direction);
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
				if (possibleDirections.contains(nonDirection.get(i)) && !isOppositeDirection(nonDirection.get(i), dX, dY, aX, aY)) {
					direction = nonDirection.get(i);
				}
			}
		}
		return direction;

	}

	public double distanceToAttacker(Defender defender, Game game) {
		Node aLocation = game.getAttacker().getLocation();
		int aX = aLocation.getX();
		int aY = aLocation.getY();
		int dX = defender.getLocation().getX();
		int dY = defender.getLocation().getY();
		double distance = Math.sqrt(Math.pow(aX - dX, 2) + Math.pow(aY - dY, 2));
		return distance;
	}

	boolean isOppositeDirection(int direction, int defenderX, int defenderY, int attackerX, int attackerY) {
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
