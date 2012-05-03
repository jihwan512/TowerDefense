package com.cdm.view.enemy;

import java.util.SortedSet;
import java.util.TreeSet;

import com.cdm.view.Position;
import com.cdm.view.elements.EnemyUnits;
import com.cdm.view.elements.Level;

public class EnemyPlayer {
	private static final int MAX_TRIALS = 50;
	public final float WAITING_TIME = 3.0f;

	enum Mode {
		ATTACK, WAIT
	};

	private Mode mode = Mode.WAIT;
	private static int levelNo = 1;
	private float enemyStrength = 3.0f;
	private Level level;
	private float timeToNextWave = WAITING_TIME;
	private float timeInWave = 0.0f;
	private SortedSet<EnemyDef> defs = new TreeSet<EnemyDef>();
	private boolean alreadySent = false;
	public static boolean level2 = false;
	public static boolean level3 = false;

	public Level getLevel() {
		return level;
	}

	public int getLevelNo() {
		return levelNo;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void addTime(float t) {
		if (mode.equals(Mode.ATTACK)) {
			if (level.hasEnemies() || !alreadySent) {
				timeToNextWave = WAITING_TIME;
				timeInWave += t;
				while (defs.size() > 0 && defs.first().time < timeInWave) {
					EnemyDef def = defs.first();
					defs.remove(def);
					alreadySent = true;
					// Position x = new Position(level.getEnemyStartPosition());
					if (level2) {
						float i = (float) Math.random();
						if (i <= 0.5) {
							Position x = new Position(
									level.getEnemyStartPosition2());
							EnemyUnit e = EnemyUnits.create(def.type, x,
									levelNo);
							level.add(e);
						} else {
							Position x = new Position(
									level.getEnemyStartPosition());
							EnemyUnit e = EnemyUnits.create(def.type, x,
									levelNo);
							level.add(e);
						}
					} else {
						Position x = new Position(level.getEnemyStartPosition());
						EnemyUnit e = EnemyUnits.create(def.type, x, levelNo);
						level.add(e);
					}

				}
			} else
				startWait();
		} else {
			if (timeToNextWave < 0) {
				startNewWave();
			} else {
				timeToNextWave -= t;
			}
		}
	}

	private void startWait() {
		levelNo += 1;
		mode = Mode.WAIT;
		timeToNextWave = WAITING_TIME;
	}

	private void startNewWave() {
		timeInWave = 0.0f;
		mode = Mode.ATTACK;
		alreadySent = false;
		/*
		 * if ((getLevelNo() > 1) && !level3) level2 = true; if (getLevelNo() >
		 * 3){
		 * 
		 * level2=false; level3 =true; }
		 */
		defs.clear();

		// strength-based randomized enemy creation
		enemyStrength += (1.5f * (float) getLevelNo());
		Float currentStrength = enemyStrength;
		Float lastTime = 0.0f;
		int trials = MAX_TRIALS; // don' run endlessly

		while (currentStrength > 0 && trials > 0) {
			EnemyType t = EnemyType.random();
			float strength = t.getStrength(getLevelNo());

			if (strength < currentStrength + EnemyType.STRENGTH_THRESHOLD) {
				currentStrength -= strength;
				lastTime += (float) Math.random() * 5.0f + 0.6f;
				defs.add(new EnemyDef(t, lastTime));
				trials = MAX_TRIALS;
			} else
				trials -= 1;
		}

	}

	public static void setLevelNo(int i) {
		levelNo = i;

	}

}
