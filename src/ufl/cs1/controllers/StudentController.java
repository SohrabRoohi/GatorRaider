package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;

public final class StudentController implements DefenderController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game,long timeDue)
	{
		int[] actions = new int[Game.NUM_DEFENDER];
		List<Defender> enemies = game.getDefenders();
		
		//Chooses a random LEGAL action if required. Could be much simpler by simply returning
		//any random number of all of the ghosts
		for(int i = 0; i < actions.length; i++)
		{
			List<Node> attackerPossibleLocations = game.getAttacker().getPossibleLocations(true);
			int attackerX = -1;
			int attackerY = -1;
			for(int j = 0; j < 3; j++) {
				if(attackerPossibleLocations.get(j) != null) {
					attackerX = attackerPossibleLocations.get(j).getX();
					attackerY = attackerPossibleLocations.get(j).getY();
				}
			}
			Defender defender = enemies.get(i);
			List<Node> defenderPossibleLocations = defender.getPossibleLocations();
			int defenderX = -1;
			int defenderY = -1;
			for(int j = 0; j < 3; j++) {
				if(defenderPossibleLocations.get(j) != null) {
					defenderX = defenderPossibleLocations.get(j).getX();
					defenderY = defenderPossibleLocations.get(j).getY();
				}
			}
			int xDistance = Math.abs(attackerX - defenderX);
			int yDistance = Math.abs(attackerY - defenderY);
			List<Integer> possibleDirs = defender.getPossibleDirs();
			if (possibleDirs.size() != 0)
				// 0 is up
				// 1 is right
				// 2 is down
				// 3 is left
				if(defenderX > attackerX && possibleDirs.contains(3) && xDistance > yDistance) {
					actions[i] = 3;
				}
				else if(defenderX < attackerX && possibleDirs.contains(1) && xDistance > yDistance) {
					actions[i] = 1;
				}
				else if(defenderY > attackerY && possibleDirs.contains(0) && yDistance > xDistance) {
					actions[i] = 0;
				}
				else if(defenderY < attackerY && possibleDirs.contains(2) && yDistance > xDistance) {
					actions[i] = 2;
				}
				else {
					actions[i] = -1;
				}
			else
				actions[i] = -1;
		}
		return actions;
	}
}