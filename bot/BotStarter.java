// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import field.Field;
import field.Shape;
import field.ShapeType;
import moves.MoveType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.*;
import moves.MoveType;

/**
 * BotStarter class
 *
 * This class is where the main logic should be. Implement getMoves() to
 * return something better than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter {

	public BotStarter() {}

	/**
	 * Returns a random amount of random moves
	 * @param state : current state of the bot
	 * @param timeout : time to respond
	 * @return : a list of moves to execute
	 */
	public ArrayList<MoveType> getMoves(BotState state, long timeout) {
		ArrayList<MoveType> moves = new ArrayList<MoveType>();
		Field cfield = state.getMyField();
		ShapeType cPiece = state.getCurrentShape();
		ShapeType nPiece = state.getNextShape();

		Shape currentPiece = new Shape(cPiece, cfield, state.getShapeLocation());
		Point loc = null;
		if(cPiece == ShapeType.O)
			loc = new Point(4,-1);
		else
			loc = new Point(3,-1);
		Shape nextPiece = new Shape(nPiece, cfield, loc);
		int[] best = getBestReward(cfield, currentPiece);

		int goleft = best[1];
		int turn = best[2];

		for (int c = turn; c > 0; c--)
			moves.add(MoveType.TURNRIGHT);
		if (goleft < 0)
			for (int a = goleft; a < 0; a++)
				moves.add(MoveType.RIGHT);
		else
			for (int b = goleft; b > 0; b--)
				moves.add(MoveType.LEFT);
		moves.add(MoveType.DROP);
		return moves;
	}

	public static void main(String[] args)
	{
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}

	int[] getBestReward(Field field, Shape piece) {
		int[] reward = new int[3];
		reward[0] = -1000000;
		int turn = 0;
		for(int rotation = 0; rotation < 4; rotation++) {
			int left = 0;
			if(rotation !=0) {
				turn = rotation;
				piece.turnRight();
			}
			Shape tempPiece = piece.clone();
			while(field.hasLeft(tempPiece)){
				tempPiece.oneLeft();
				left++;
			}
			while(field.isValid(tempPiece)){
				Shape copyPiece = tempPiece.clone();
				while(field.hasDown(copyPiece)){
					copyPiece.oneDown();
				}
				if(true) {
					int score;
					Field tempField = field.clone();
					tempField.addPiece(copyPiece);

					score = tempField.getReward();
					if ((score >= reward[0])) {
						reward[0] = score;
						reward[1] = left;
						reward[2] = turn;
					}
				}
				left--;
				tempPiece.oneRight();
			}
		}
		return reward;
	}
}
