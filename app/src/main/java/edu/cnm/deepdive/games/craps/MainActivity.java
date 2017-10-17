package edu.cnm.deepdive.games.craps;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.LinkedList;
import java.util.Random;



public class MainActivity extends AppCompatActivity {

  private static final String DIE_IMAGE_PREFIX = "face_";
  private static final String DRAWABLE_RESOURCE_TYPE = "drawable";
  private static final int SLEEP_TIME = 1;
  private static final int UPDATE_INTERVAL = 100;

  private Game game;
  private Button playButton;
  private ToggleButton playToggle;
  private Button resetButton;
  private TextView tallyView;
  private String tallyMessage;
  private ListView rollsViews;
  private boolean runningFast;
  private Drawable[] faces;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializedFields();
    initializeListeners();
    reset();

  }

  private void initializedFields() {
    Resources res = getResources();
    playButton = (Button) findViewById(R.id.playButton);
    playToggle = (ToggleButton) findViewById(R.id.playToggle);
    resetButton = (Button) findViewById(R.id.resetButton);
    tallyView = (TextView) findViewById(R.id.tallyView);
    tallyMessage = getString(R.string.tally_message);
    rollsViews = (ListView) findViewById(R.id.rollsView);
    runningFast = false;
    faces = new Drawable[6];
    for (int i = 0; i < faces.length; i++) {
      int id = res
          .getIdentifier(DIE_IMAGE_PREFIX + (i + 1), DRAWABLE_RESOURCE_TYPE, getPackageName());
      faces[i] = res.getDrawable(id, null);
    }

  }

  private void initializeListeners() {
    playButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        play();
      }
    });
    playToggle.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
       toggle();
      }

      });
    resetButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        reset();

      }
    });
  } private void play() {
    game.reset();
    game.play();
    displayRolls(true);
    setTallyMessage(game.getWins(),game.getPlays());
  }
  private void toggle() {
    playToggle.setEnabled(false);
    if (isRunningFast()) {
      setRunningFast(false);
    }else{
      setRunningFast(true);
      new FastSimulator(SLEEP_TIME, UPDATE_INTERVAL).start();
    }
}
 private void reset() {
    game = new Game(this);
    game.setRng(new Random());
    displayRolls(true);
    setTallyMessage(0, 0);

 }

   private void setTallyMessage(long wins, long plays) {
    String winsMessage = getResources().getQuantityString(R.plurals.wins_noun, (int) wins, wins);
    String playMessage = getResources().getQuantityString(R.plurals.plays_noun, (int) plays, plays);
    String percentMessage = (plays > 0) ?
        getString(R.string.winning_percentage, 100.0 * wins / plays) : "";
    tallyView.setText(String.format(tallyMessage, winsMessage, playMessage, percentMessage));

   }

   private void displayRolls(boolean populate) {
    rollsViews.setAdapter(
     new RollAdapter(this, (populate ? game.getRolls() : new LinkedList<Game.Roll>()), faces));
   }
    private void setInteractive(boolean enabled) {
    playButton.setEnabled(enabled);
    resetButton.setEnabled(enabled);
      rollsViews.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
      displayRolls(enabled);

    }

  public synchronized boolean isRunningFast() {
    return runningFast;
  }

  public synchronized void setRunningFast(boolean runningFast) {
    this.runningFast = runningFast;
  }
  private class FastSimulator extends Thread {

    private int sleepTime;
    private int updateInterval;

    public  FastSimulator(int sleepTime, int updateInterval) {
      this.sleepTime = sleepTime;
      this.updateInterval = updateInterval;

    }

    @Override
    public void run() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setInteractive(false);
          playToggle.setEnabled(true);
        }
      });
      while (isRunningFast()) {
        game.reset();
        game.play();
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
          // do nothing
        }
        if (game.getPlays() % updateInterval == 0) {
          final long wins = game.getWins();
          final long plays = game.getPlays();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              setTallyMessage(wins, plays);

            }
          });
        }
    }
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        setTallyMessage(game.getWins(), game.getPlays());
        setInteractive(true);
        playToggle.setEnabled(true);
      }
    });
  }
}
}
