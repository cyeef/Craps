package edu.cnm.deepdive.games.craps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import edu.cnm.deepdive.games.craps.Game.Roll;
import edu.cnm.deepdive.games.craps.Game.State;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  private Game game;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    game = new Game(this);
    game.setRng(new Random());
    ((Button) findViewById(R.id.playButton)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (game.getState()== State.LOSE || game.getState() == State.WIN) {
          game.reset();
        }
        game.play();
        ArrayAdapter<Game.Roll> adapter
            = new ArrayAdapter<Roll>(MainActivity.this, R.layout.rolls_list_item ,game.getRolls());
        ((ListView) findViewById(R.id.rollsView)).setAdapter(adapter);
        ((TextView) findViewById(R.id.tallyView))
            .setText(String.format(getString(R.string.tally_message) ,
                game.getWins(), game.getLosses(), 100.0 * game.getWins() / game.getPlays()));
      }
    });
  }
}
