package org.flockofseagles.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;
import org.flockofseagles.DatabaseQuestionUtility;
import org.flockofseagles.Question;
import org.flockofseagles.ui.util.Difficulty;
import org.flockofseagles.util.DataStore;
import org.flockofseagles.util.SaveGame;

import java.util.HashMap;

public class PlayField extends GridPane {

	@Setter
	@Getter
	private   Difficulty difficulty;
	protected Canvas[][] field;
	protected boolean    correctAnswer = false;

	@Getter
	private final DataStore dataStore;

	public PlayField(Canvas canvas, Difficulty diff, SaveGame saveGame) {
		super();

		if (canvas == null) {
			throw new IllegalArgumentException("Null canvas object passed into PlayField Constructor");
		}

		HashMap<Question, Boolean> questions = new HashMap<>();
		DatabaseQuestionUtility    db        = new DatabaseQuestionUtility();
		db.loadQuestionSet().forEach(question -> questions.put(question, false));

		this.difficulty = diff;

		// Set up data store for serialization and save states
		if (saveGame.getData() == null) {
			this.dataStore = new DataStore(9, 9, new Player(0, 0), questions, difficulty);
		} else {
			this.dataStore = saveGame.getData();
		}

		this.setWidth(canvas.getWidth());
		this.setHeight(canvas.getHeight());
		this.field = initializePlayField(diff);
		setWalls();
	}

	public Canvas[][] initializePlayField(Difficulty difficulty) {
		int        size = difficulty.getSize();
		Canvas[][] cArra;
		Canvas     canvas;

		cArra = new Canvas[size][size];

		for (int i = 0; i < size; i++) {
			//cols
			for (int j = 0; j < size; j++) {
				canvas = new Canvas(this.getWidth() / size, this.getHeight() / size);
				this.add(canvas, j, i, 1, 1);
				cArra[i][j] = canvas;
				cArra[i][j].setUserData("Empty");
			}
		}
        return cArra;
    }

    public void setMaze() {
        Canvas canvas;
        Wall w;
        PowerUp p = new PowerUp();

        //rows
        for (int i = 0; i < this.field.length; i++) {
            //cols
            for (int j = 0; j < this.field.length; j++) {
	            int x = dataStore.getPlayer().xVal;
	            int y = dataStore.getPlayer().yVal;
	            if (i == x && j == y) {
		            canvas = this.field[i][j];
		            dataStore.getPlayer().draw(canvas);
	            }

	            // if current row is an even number
	            if ((i % 2) == 0 && j + 1 < this.field.length) {
		            j++;
		            canvas = this.field[i][j];
		            w      = getWall(i, j);
		            w.drawVert(canvas);
                } else if ((j % 2) == 0 && (i % 2) == 1) {
                    canvas = this.field[i][j];
                    w = getWall(i, j);
                    w.drawHorz(canvas);
                } else if ((j % 2) == 1 && (i % 2) == 1) {
                    canvas = this.field[i][j];
                    w = getWall(i, j);
                    w.drawMid(canvas);
                } else if (j == this.field.length - 1 && i == this.field.length - 1) {
                    canvas = this.field[i][j];
                    clearCanvas(canvas);
                } else {
                    canvas = this.field[i][j];
                }
            }
        }

    }

    public void updatePlayer(int i) {
        Wall w;
        Media pick;
        MediaPlayer mPlayer;
        Canvas canvas = getCanvas(dataStore.getPlayer().xVal, dataStore.getPlayer().yVal);

        //move up
        if (i == 1) {
            w = getWall(dataStore.getPlayer().xVal - 1, dataStore.getPlayer().yVal);

            if (correctAnswer) {
                w.isPassable = true;
                clearCanvas(canvas);
                canvas = this.field[dataStore.getPlayer().xVal - 2][dataStore.getPlayer().yVal];
                dataStore.getPlayer().draw(canvas);
                dataStore.getPlayer().xVal = dataStore.getPlayer().xVal - 2;
            } else {
                pick = new Media(getClass().getClassLoader().getResource("sounds/close_door.wav").toString());
                mPlayer = new MediaPlayer(pick);
                mPlayer.play();
	            canvas     = this.field[dataStore.getPlayer().xVal - 1][dataStore.getPlayer().yVal];
	            w.isLocked = true;
	            w.drawHorz(canvas);
            }
        } else if (i == 2) {    //move down

            w = getWall(dataStore.getPlayer().xVal + 1, dataStore.getPlayer().yVal);
            if (correctAnswer) {
                w.isPassable = true;
                clearCanvas(canvas);
                canvas = this.field[dataStore.getPlayer().xVal + 2][dataStore.getPlayer().yVal];
                dataStore.getPlayer().draw(canvas);
                dataStore.getPlayer().xVal = dataStore.getPlayer().xVal + 2;
            } else {

                pick = new Media(getClass().getClassLoader().getResource("sounds/close_door.wav").toString());
                mPlayer = new MediaPlayer(pick);
                mPlayer.play();

	            canvas     = this.field[dataStore.getPlayer().xVal + 1][dataStore.getPlayer().yVal];
	            w.isLocked = true;
	            w.drawHorz(canvas);
            }
        } else if (i == 3) { //move left

            w = getWall(dataStore.getPlayer().xVal, dataStore.getPlayer().yVal - 1);

            if (correctAnswer) {
                w.isPassable = true;
                clearCanvas(canvas);
                canvas = this.field[dataStore.getPlayer().xVal][dataStore.getPlayer().yVal - 2];
                dataStore.getPlayer().draw(canvas);
                dataStore.getPlayer().yVal = dataStore.getPlayer().yVal - 2;
            } else {
                pick = new Media(getClass().getClassLoader().getResource("sounds/close_door.wav").toString());
                mPlayer = new MediaPlayer(pick);
                mPlayer.play();

	            canvas     = this.field[dataStore.getPlayer().xVal][dataStore.getPlayer().yVal - 1];
	            w.isLocked = true;
	            w.drawVert(canvas);
            }
        } else if (i == 4) { //move right

            w = getWall(dataStore.getPlayer().xVal, dataStore.getPlayer().yVal + 1);
            if (correctAnswer) {
                w.isPassable = true;
                clearCanvas(canvas);
                canvas = this.field[dataStore.getPlayer().xVal][dataStore.getPlayer().yVal + 2];
                dataStore.getPlayer().draw(canvas);
                dataStore.getPlayer().yVal = dataStore.getPlayer().yVal + 2;
            } else {
                pick = new Media(getClass().getClassLoader().getResource("sounds/close_door.wav").toString());
                mPlayer = new MediaPlayer(pick);
                mPlayer.play();

	            canvas     = this.field[dataStore.getPlayer().xVal][dataStore.getPlayer().yVal + 1];
	            w.isLocked = true;
	            w.drawVert(canvas);
            }
        }
    }

    public void clearCanvas(Canvas canvas) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setUserData("Empty");
    }

    public void clearPlayField() {
        Canvas canvas;

        for (int i = 0; i < this.field.length; i++) {
            for (int j = 0; j < this.field.length; j++) {
                canvas = this.field[i][j];
                GraphicsContext g = canvas.getGraphicsContext2D();
                g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                canvas.setUserData("Empty");
            }
        }

	    dataStore.getPlayer().xVal = 0;
	    dataStore.getPlayer().yVal = 0;
	    dataStore.getWalls().clear();
	    System.gc();
    }

    public Canvas getCanvas(int x, int y) {
        return this.field[x][y];
    }

    public Wall getWall(int x, int y) {
	    for (Wall w : dataStore.getWalls()) {
		    if (w.xVal == x && w.yVal == y) {
			    return w;
		    }
	    }
        return null;
    }

    private void setWalls() {
        Wall w;

        //rows
        for (int i = 0; i < this.field.length; i++) {
            //cols
            for (int j = 0; j < this.field.length; j++) {

                // if current row is an even number
                if ((i % 2) == 0 && j + 1 < this.field.length) {
                    j++;
	                w = new Wall(i, j);
	                dataStore.getWalls().add(w);
                } else if ((j % 2) == 0 && (i % 2) == 1) {
	                w = new Wall(i, j);
	                dataStore.getWalls().add(w);
                } else if ((j % 2) == 1 && (i % 2) == 1) {
	                w = new Wall(i, j);
	                dataStore.getWalls().add(w);
                }
            }
        }
    }

    public Question getQuestion() {
        var question = (Question) this.dataStore.getQuestions().keySet().toArray()[0];
        this.dataStore.getQuestions().remove(question);
        return question;
    }

    public int getLength() {
        return this.field.length;
    }

    public boolean mazeIsSolvable(int x, int y, int targetX, int targetY) {
        boolean[][] visited = new boolean[field.length][field.length];
        boolean[][] badPath = new boolean[field.length][field.length];

        for (int i = 0; i < visited.length; i++)
            for (int j = 0; j < visited.length; j++) {
                visited[i][j] = false;
                badPath[i][j] = false;
            }


        return solveMaze(x, y, targetX, targetY, visited, badPath);
    }

    private boolean solveMaze(int x, int y, int targetX, int targetY, boolean[][] visited, boolean[][] badPath) {
        if (x >= field.length || y >= field.length || x < 0 || y < 0)
            return false;
        if (x == targetX && y == targetY)
            return true;

        visited[x][y] = true;

        if (getWall(x - 1, y) != null && !getWall(x - 1, y).isLocked && (x - 2 >= 0) && !badPath[x - 2][y] && !visited[x - 2][y] && solveMaze(x - 2, y, targetX, targetY, visited, badPath))
            return true;
        else if (getWall(x, y + 1) != null && !getWall(x, y + 1).isLocked && (y + 2 < field.length) && !badPath[x][y + 2] && !visited[x][y + 2] && solveMaze(x, y + 2, targetX, targetY, visited, badPath))
            return true;
        else if (getWall(x + 1, y) != null && !getWall(x + 1, y).isLocked && (x + 2 < field.length) && !badPath[x + 2][y] && !visited[x + 2][y] && solveMaze(x + 2, y, targetX, targetY, visited, badPath))
            return true;
        else if (getWall(x, y - 1) != null && !getWall(x, y - 1).isLocked && (y - 2 >= 0) && !badPath[x][y - 2] && !visited[x][y - 2] && solveMaze(x, y - 2, targetX, targetY, visited, badPath))
            return true;
        else {
            badPath[x][y] = true;
            visited[x][y] = false;
            return false;
        }
    }

    public boolean isEnd(int x, int y) {
        return x == this.getLength() - 1 && y == this.getLength() - 1;
    }

}
