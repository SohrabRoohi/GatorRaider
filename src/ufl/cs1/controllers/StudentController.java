package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Actor;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

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
		actions[0] = DefenderAction(enemies.get(0), game);
		actions[1] = DefenderAction(enemies.get(1), game);
		actions[2] = thirdDefenderAction(enemies.get(2), game);
		actions[3] = fourthDefenderAction(enemies.get(3), game);
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
				if(defender.isVulnerable() && possibleDirs.contains(1)) {
					return 1;
				}
				return 3;
			} else if (defenderX < attackerX && possibleDirs.contains(1) && xDistance >= yDistance) {
				if(defender.isVulnerable() && possibleDirs.contains(3)) {
					return 3;
				}
				return 1;
			} else if ((defenderY > attackerY && possibleDirs.contains(0) && yDistance >= xDistance) || (yDistance <= xDistance && defenderY > attackerY && !possibleDirs.contains(1) && !possibleDirs.contains(3))) {
				if(defender.isVulnerable() && possibleDirs.contains(2)) {
					return 2;
				}
				return 0;
			} else if (defenderY < attackerY && possibleDirs.contains(2) && yDistance >= xDistance || (yDistance <= xDistance && defenderY < attackerY && !possibleDirs.contains(1) && !possibleDirs.contains(3))) {
				if(defender.isVulnerable() && possibleDirs.contains(0)) {
					return 0;
				}
				return 2;
			} else {
				return -1;
			}
		else
			return -1;
	}

    public int thirdDefenderAction(Actor defender, Game game) {
        int direction;
        boolean approach = !game.getDefender(0).isVulnerable();
        direction = defender.getNextDir(game.getAttacker().getLocation(), approach);

        if (!approach) {
            direction = defender.getReverse();
        }
        return direction;
    }



	public int fourthDefenderAction(Defender defender, Game game){

		List<Node> powerPillNodes = game.getPowerPillList();
		int distAttacktoPowerpill;
		List<Defender> listDefenders = game.getDefenders();
		int distBetweenDefenders;


		for(int i = 0; i < powerPillNodes.size(); i++) {

			distAttacktoPowerpill = game.getAttacker().getLocation().getPathDistance(powerPillNodes.get(i));

			if ((distAttacktoPowerpill >= 6 && distAttacktoPowerpill <= 9) || defender.isVulnerable()) {
				return defender.getNextDir(game.getAttacker().getLocation(), false);
			}
			else{
				return defender.getNextDir(game.getAttacker().getLocation(), true);
			}

		}

		for(int j = 0; j < NUM_DEFENDER - 1; j++){
			distBetweenDefenders = defender.getLocation().getPathDistance(listDefenders.get(j).getLocation());
			if(distBetweenDefenders < 2 && (defender.getDirection() == listDefenders.get(j).getDirection())){
				return defender.getReverse();
			}
		}

		return defender.getNextDir(game.getAttacker().getLocation(), true);
	}
}

