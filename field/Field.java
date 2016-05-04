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

package field;

import field.Cell;

/**
 * Field class
 *
 * Represents the playing field for one player.
 * Has some basic methods already implemented.
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class Field {

	private int width;
	private int height;
	private Cell grid[][];

	public Field(int width, int height, String fieldString) {
		this.width = width;
		this.height = height;

		parse(fieldString);
	}

	/**
	 * Parses the input string to get a grid with Cell objects
	 * @param fieldString : input string
	 */
	private void parse(String fieldString) {

		this.grid = new Cell[this.width][this.height];

		// get the separate rows
		String[] rows = fieldString.split(";");
		for(int y=0; y < this.height; y++) {
			String[] rowCells = rows[y].split(",");

			// parse each cell of the row
			for(int x=0; x < this.width; x++) {
				int cellCode = Integer.parseInt(rowCells[x]);
				this.grid[x][y] = new Cell(x, y, CellType.values()[cellCode]);
			}
		}
	}

	public Cell getCell(int x, int y) {
		if(x < 0 || x >= this.width || y < 0 || y >= this.height)
			return null;
		return this.grid[x][y];
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	//check if piece can still move down
	public boolean hasDown(Shape piece){
		Shape tempPiece = piece.clone();
		tempPiece.oneDown();
		Cell[] tempBlocks = tempPiece.getBlocks();
		for(int i = 0; i < tempBlocks.length; i++){
			if(tempBlocks[i].hasCollision(this) || tempBlocks[i].isOutOfBoundaries(this))
				return false;
		}
		return true;
	}

	//check if piece can still move left
	public boolean hasLeft(Shape piece){
		Shape tempPiece = piece.clone();
		tempPiece.oneLeft();
		Cell[] tempBlocks = tempPiece.getBlocks();
		for(int i = 0; i < tempBlocks.length; i++){
			if(tempBlocks[i].hasCollision(this) || tempBlocks[i].isOutOfBoundaries(this))
				return false;
		}
		return true;
	}

	//calculate the reward according to hole and row clean
	public int getReward(){
		int score = 0;
		int lineCount = 0;
		int h = this.blockHeight();
		for (int i = h; i < 20; i++){
			boolean line = true;
			for (int j = 0; j < 10; j++){
				if (this.grid[j][i].isEmpty()){
					score -= (i);
					line = false;
				}
			}
			if(line == true){
				lineCount++;
			}
		}
		score += (lineCount*138);
		return score;
	}

	//create a new field string for the new field
	private String newstate() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				String value = "";
				switch (this.grid[j][i].getState()) {
					case EMPTY:
						value = "0";
						break;
					case SHAPE:
						value = "1";
						break;
					case SOLID:
						value = "3";
						break;
					case BLOCK:
						value = "2";
						break;
				}
				builder.append(value);
				builder.append(",");
			}
			builder.append(";");
		}
		return builder.toString();
	}

	//copy the field
	public Field clone(){
		return new Field(this.width, this.height, this.newstate());
	}

	private void setCell(Cell cell) {
		int x = (int) cell.getLocation().getX();
		int y = (int) cell.getLocation().getY();
		if(x < 0 || x >= this.width || y < 0 || y >= this.height)
			return;
		this.grid[x][y].setBlock();
	}

	//add the new pice into field
	public void addPiece(Shape piece){
		for(int i = 0; i < piece.getBlocks().length; i++){
			setCell(piece.getBlocks()[i]);
		}
	}

	//check if the new piece fit into the field
	public boolean isValid(Shape piece){
		Cell[] tempBlocks = piece.getBlocks();
		for(int i = 0; i < tempBlocks.length; i++){
			if(tempBlocks[i].hasCollision(this) || tempBlocks[i].isOutOfBoundaries(this))
				return false;
		}
		return true;
	}

	//get the height of current block
	public int blockHeight(){
		int bheight = 0;
		boolean done = false;
		for (int i = 0; i < 20; i++){
			for(int j = 0; j < 10; j++)
				if(this.grid[j][i].isBlock()){
					bheight = i;
					done = true;
					break;
				}
				if(done == true){
					break;
				}
		}
		return bheight;
	}
}
