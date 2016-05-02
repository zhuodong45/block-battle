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
		if(nPiece == ShapeType.O)	//2*2 piece
			loc = new Point(4,-1);
		else
			loc = new Point(3,-1);	//3*3 or 4*4 piece
		Shape nextPiece = new Shape(nPiece, cfield, loc);

		int[] best = getBestReward(cfield, currentPiece, nextPiece);	//calculate the reward, index 0 is reward, 1 is move, 2 is turn

		int goleft = best[1];	//get the best move
		int turn = best[2];	//get teh best turn

		for (int c = turn; c > 0; c--)	//turn the piece
			moves.add(MoveType.TURNRIGHT);
		if (goleft < 0)	//go right
			for (int a = goleft; a < 0; a++)
				moves.add(MoveType.RIGHT);
		else	//go left
			for (int b = goleft; b > 0; b--)
				moves.add(MoveType.LEFT);
		moves.add(MoveType.DROP);	//drop at the end
		return moves;
	}

	public static void main(String[] args)
	{
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}

	int[] getBestReward(Field field, Shape piece, Shape nPiece) {
		int[] reward = new int[3];	//store reward, move, turn
		reward[0] = -1000000;
		int turn = 0;
		for(int rotation = 0; rotation < 4; rotation++) {	//turn the piece, there are 4 turn
			int left = 0;
			if(rotation !=0) {	//ignore the first turn
				turn = rotation;
				piece.turnRight();
			}
			Shape tempPiece = piece.clone();	//copy a temp piece use for calculate reward
			while(field.hasLeft(tempPiece)){	//move to the left most of the field
				tempPiece.oneLeft();
				left++;
			}
			while(field.isValid(tempPiece)){
				Shape copyPiece = tempPiece.clone();	//make another copy before move down
				while(field.hasDown(copyPiece)){	//move the piece down to the top of the block
					copyPiece.oneDown();
				}
				int score;
				Field tempField = field.clone();	//copy a temp field
				tempField.addPiece(copyPiece);	//add current piece into temp field
				score = tempField.getReward();	//calculate the current reward
				if (nPiece != null) {
					Shape next = nPiece.clone();
					int[] secondReward = getBestReward(tempField, next, null);
					score += secondReward[0];
				}

				if ((score >= reward[0])) {	//if current reward is better than best reward
					//update the score, move and turn
					reward[0] = score;
					reward[1] = left;
					reward[2] = turn;
				}
				left--;
				tempPiece.oneRight();	//move temp piece to the next right
			}
		}
		return reward;
	}
}
