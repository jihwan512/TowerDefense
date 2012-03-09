package com.cdm.view;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public interface IRenderer {

	void drawLines(Position pos, List<Vector3> lines, float angle, Color color);

	void drawRect(float f, float g, float h, float d);

	public void fillRect(float x0, float y0, float x1, float y1, Color c);

	void drawPoly(Position pos, List<Vector3> lines, float angle, Color color);

}