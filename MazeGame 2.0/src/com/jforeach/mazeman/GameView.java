package com.jforeach.mazeman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	
	//width and height of the whole maze and width of lines which
	//make the walls
	private int width, height, lineWidth;
	//size of the maze i.e. number of cells in it
	private int mazeSizeX, mazeSizeY;
	//width and height of cells in the maze
	float cellWidth, cellHeight;
	//the following store result of cellWidth+lineWidth 
	//and cellHeight+lineWidth respectively 
	float totalCellWidth, totalCellHeight;
	//the finishing point of the maze
	private int mazeFinishX, mazeFinishY;
	private Maze maze;
	private Context context;
	private Paint line, ball, background;
	boolean[][] hLines;
	boolean[][] vLines;
	boolean dragging = false;
	
	public GameView(Context context) {
		super(context);
		this.context = (Activity)context;
		
		//get game colors from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int bgColor = getResources().getColor(R.color.game_bg);
		String bgColorStr = prefs.getString("bgColor","");
		if(bgColorStr.length() > 0) {
			bgColor = Color.parseColor(bgColorStr);
		}
		int lineColor = getResources().getColor(R.color.line);
		String lineColorStr = prefs.getString("wallColor","");
		if(lineColorStr.length() > 0) {
			lineColor = Color.parseColor(lineColorStr);
		}
		int ballColor = getResources().getColor(R.color.position);
		String ballColorStr = prefs.getString("ballColor","");
		if(ballColorStr.length() > 0) {
			ballColor = Color.parseColor(ballColorStr);
		}
		
		line = new Paint();
		line.setColor(lineColor);
		ball = new Paint();
		ball.setColor(ballColor);
		background = new Paint();
		background.setColor(bgColor);
		setFocusable(true);
		this.setFocusableInTouchMode(true);
	}
	public void setMaze(Maze maze) {
		this.maze = maze;
		mazeFinishX = maze.getFinalX();
		mazeFinishY = maze.getFinalY();
		mazeSizeX = maze.getMazeWidth();
		mazeSizeY = maze.getMazeHeight();
		hLines = maze.getHorizontalLines();
		vLines = maze.getVerticalLines();
	}
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = (w < h)?w:h;
		height = width;         //for now square mazes
		lineWidth = 1;          //for now 1 pixel wide walls
		cellWidth = (width - ((float)mazeSizeX*lineWidth)) / mazeSizeX;
		totalCellWidth = cellWidth+lineWidth;
		cellHeight = (height - ((float)mazeSizeY*lineWidth)) / mazeSizeY;
		totalCellHeight = cellHeight+lineWidth;
		ball.setTextSize(cellHeight*0.75f);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	protected void onDraw(Canvas canvas) {
		//fill in the background
		canvas.drawRect(0, 0, width, height, background);
		
		//iterate over the boolean arrays to draw walls
		for(int i = 0; i < mazeSizeX; i++) {
			for(int j = 0; j < mazeSizeY; j++){
				float x = j * totalCellWidth;
				float y = i * totalCellHeight;
				if(j < mazeSizeX - 1 && vLines[i][j]) {
					//we'll draw a vertical line
					canvas.drawLine(x + cellWidth,   //start X
									y,               //start Y
									x + cellWidth,   //stop X
									y + cellHeight,  //stop Y
									line);
				}
				if(i < mazeSizeY - 1 && hLines[i][j]) {
					//we'll draw a horizontal line
					canvas.drawLine(x,               //startX 
									y + cellHeight,  //startY 
								    x + cellWidth,   //stopX 
								    y + cellHeight,  //stopY 
									line);
				}
			}
		}
		int currentX = maze.getCurrentX(),currentY = maze.getCurrentY();
		//draw the ball
		canvas.drawCircle((currentX * totalCellWidth)+(cellWidth/2),   //x of center
						  (currentY * totalCellHeight)+(cellWidth/2),  //y of center
						  (cellWidth*0.45f),                           //radius
						  ball);
		//draw the finishing point indicator
		canvas.drawText("F",
						(mazeFinishX * totalCellWidth)+(cellWidth*0.25f),
						(mazeFinishY * totalCellHeight)+(cellHeight*0.75f),
						ball);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent evt) {
		boolean moved = false;
		switch(keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				moved = maze.move(Maze.UP);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				moved = maze.move(Maze.DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				moved = maze.move(Maze.RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				moved = maze.move(Maze.LEFT);
				break;
			default:
				return super.onKeyDown(keyCode,evt);
		}
		if(moved) {
			//the ball was moved so we'll redraw the view
			invalidate();
			if(maze.isGameComplete()) {
				showFinishDialog();
			}
		}
		return true;
	}
	void showFinishDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getText(R.string.finished_title));
		LayoutInflater inflater = (LayoutInflater)context.
								getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.finish, null);
		builder.setView(view);
		final AlertDialog finishDialog = builder.create();
		View closeButton =view.findViewById(R.id.closeGame);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View clicked) {
				if(clicked.getId() == R.id.closeGame) {
					finishDialog.dismiss();
					((Activity)context).finish();
				}
			}
		});
		finishDialog.show();
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		int currentX = maze.getCurrentX();
		int currentY = maze.getCurrentY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			//touch gesture started
			if(Math.floor(touchX/totalCellWidth) == currentX && 
					Math.floor(touchY/totalCellHeight) == currentY) {
				//touch gesture in the cell where the ball is
				dragging = true;
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			//touch gesture completed
			dragging = false;
			return true;
		case MotionEvent.ACTION_MOVE:
			if(dragging) {
				int cellX = (int)Math.floor(touchX/totalCellWidth);
				int cellY = (int)Math.floor(touchY/totalCellHeight);
				
				if((cellX != currentX && cellY == currentY) || 
						(cellY != currentY && cellX == currentX)) {
					//either X or Y changed
					boolean moved = false;
					//check horizontal ball movement
					switch(cellX-currentX) {
					case 1:
						moved = maze.move(Maze.RIGHT);
						break;
					case -1:
						moved = maze.move(Maze.LEFT);
					}
					//check vertical ball movement
					switch(cellY-currentY) {
					case 1:
						moved = maze.move(Maze.DOWN);
						break;
					case -1:
						moved = maze.move(Maze.UP);
					}
					if(moved) {
						//the ball was moved so we'll redraw the view
						invalidate();
						if(maze.isGameComplete()) {
							//game is finished
							showFinishDialog();
						}
					}
				}
				return true;
			}
		}
		
		return false;
	}
	public void onSwingb() {
		// TODO Auto-generated method stub
		 boolean moved = maze.move(Maze.UP);
			
				invalidate();
		 
	}
	public void onSwingr() {
		// TODO Auto-generated method stub
		 boolean moved = maze.move(Maze.RIGHT);
			
				invalidate();
		 
	}
	public void onSwingl() {
		// TODO Auto-generated method stub
		 boolean moved = maze.move(Maze.LEFT);
			
				invalidate();
		 
	}
}
