package com.cdm.view.elements.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.cdm.view.IRenderer;
import com.cdm.view.Position;
import com.cdm.view.elements.Element;
import com.cdm.view.elements.Level;
import com.cdm.view.elements.shots.ShotTarget;
import com.cdm.view.enemy.EnemyUnit;

public abstract class Unit implements Element , ShotTarget {

	public enum UnitType {
		CANNON, ROCKET_THROWER, STUNNER, PHAZER;

		public int getCost() {
			switch (this) {
			case CANNON:
				return 5;
			case ROCKET_THROWER:
				return 20;
			case STUNNER:
				return 10;
			case PHAZER:
				return 120;
			}
			return 0;
		}
	}

	private Position pos = new Position(0, 0, Position.LEVEL_REF),
			oldpos = new Position(1, 1, Position.LEVEL_REF);
	private float size;
	private Level level;
	private int cost;
	private int speed;
	private int unitenergy = 3;

	public Unit(Position p) {
		pos = p;
		size = 0.4f;
		level = null;
	}

	public boolean destroyed() {
		return false;
	}

	public abstract void move(float time);

	public abstract void draw(IRenderer renderer);

	public abstract void drawAfter(IRenderer renderer);

	@Override
	public void setPosition(Position p) {
		setPosition(p, false);
	}

	public void setPosition(Position p, boolean initial) {
		if (level != null) {
			boolean modified = (!oldpos.alignedToGrid().equals(
					p.alignedToGrid()));
			if (!initial) {
				if (modified) {
					level.removeMeFromGrid(oldpos, this);
				}
			}
			pos.assignFrom(p);
			if (modified || initial) {
				level.addMeToGrid(p, this);
				oldpos.assignFrom(pos);
			}
		} else {
			pos.assignFrom(p);
		}
	}

	public Position getPosition() {
		return pos;
	}

	protected void setSize(float f) {
		size = f;
	}

	public void setLevel(Level level) {
		this.level = level;
		level.addMeToGrid(pos, this);
	}

	public Level getLevel() {
		return level;
	}

	public float getSize() {
		return size;
	}


	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

	public abstract int getZLayer();

	public void wasAttacked(Unit unit) {
		if (!(unit instanceof EnemyUnit)) {
			if (unitenergy <= 0) {
				getLevel().unitDestroyed(unit.getPosition().alignedToGrid(),
						unit);
				Gdx.app.log("", "KILLED...");
			} else {

				Gdx.app.log("", "ATTACK!");
				setUnitEnergy(getUnitEnergy() - 1);
				Gdx.app.log("Energy", Integer.toString(getUnitEnergy()));
			}
		}
	}

	public int getUnitEnergy() {
		return unitenergy;
	}

	public void setUnitEnergy(int energy) {
		this.unitenergy = energy;
	}

	@Override
	public int compareTo(Element arg0) {
		return arg0.hashCode() - this.hashCode();
	}

	public void drawInLayer(int zLayer, IRenderer renderer) {
		if (getZLayer() == zLayer)
			draw(renderer);
	}
	
	private Vector3 result = new Vector3();

	protected Position anticipatePosition(Position startingPos,
			ShotTarget enemy, float speed) {
		float enemyDistance = startingPos.distance(enemy.getPosition());
		float timeToTarget = (enemyDistance / speed);
		float enemyMoveDistance = timeToTarget * enemy.getSpeed();

		result.set(enemy.getPosition().toVector());
		result.add(enemy.getMovingDirection().mul(enemyMoveDistance));
		return new Position(result.x, result.y, Position.LEVEL_REF);
	}

}
