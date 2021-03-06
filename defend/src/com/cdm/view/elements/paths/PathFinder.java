package com.cdm.view.elements.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cdm.view.elements.Grid;
import com.cdm.view.elements.Grid.GridElement;

public class PathFinder {
	private static RoundQueue todoBuffer = null;

	public interface GridElementAccess {
		int read(GridElement e);

		void write(GridElement e, int value);
	}

	public static class GridGoalDistanceAccess implements GridElementAccess {

		@Override
		public int read(GridElement e) {
			return e.getDistToEnd();
		}

		@Override
		public void write(GridElement e, int value) {
			e.setDistToEnd(value);
		}
	}

	public static class GridTmpDistanceAccess implements GridElementAccess {

		@Override
		public int read(GridElement e) {
			return e.getTempValue();
		}

		@Override
		public void write(GridElement e, int value) {
			e.setTempValue(value);
		}
	}

	public static class GridUnitDistanceAccess implements GridElementAccess {

		@Override
		public int read(GridElement e) {
			return e.getDistToUnit();
		}

		@Override
		public void write(GridElement e, int value) {
			e.setDistToUnit(value);
		}
	}

	public static GridTmpDistanceAccess TMP_ACCESSOR = new GridTmpDistanceAccess();
	public static GridGoalDistanceAccess GOAL_ACCESSOR = new GridGoalDistanceAccess();
	public static GridUnitDistanceAccess UNITDIST_ACCESSOR = new GridUnitDistanceAccess();

	private static List<PathPos> tmpList = Arrays
			.asList(new PathPos[] { new PathPos() });

	public static boolean breadthSearch(Grid grid, GridElementAccess accessor,
			PathPos from, PathPos to, PathPos ignoreThis, boolean fastOut) {
		tmpList.set(0, to);
		return breadthSearch(grid, accessor, from,
				Arrays.asList(new PathPos[] { to }), ignoreThis, fastOut);
	}

	public static boolean breadthSearch(Grid grid, GridElementAccess accessor,
			PathPos from, List<PathPos> to, PathPos ignoreThis, boolean fastOut) {
		List<PathPos> p = new ArrayList<PathPos>();
		if (from != null)
			p.add(from);
		return breadthSearch(grid, accessor, p, to, ignoreThis, fastOut);

	}

	private static void cleanGrid(Grid grid, GridElementAccess accessor) {
		int x, y;
		for (x = 0; x < grid.getW(); x++)
			for (y = 0; y < grid.getH(); y++) {
				accessor.write(grid.getElement(x, y), -1);
			}
	}

	private static void checkTodoBuffer(Grid grid) {
		int size = grid.getH() * grid.getW();
		if (todoBuffer == null || todoBuffer.capacity() < size) {
			todoBuffer = new RoundQueue(size);
		} else {
			todoBuffer.cleanup();
		}

	}

	public static boolean breadthSearch(Grid grid, GridElementAccess accessor,
			List<PathPos> from, List<PathPos> to, PathPos ignoreThis,
			boolean fastOut) {

		checkTodoBuffer(grid);

		cleanGrid(grid, accessor);
		// boolean found = false;
		List<PathPos> found = new ArrayList<PathPos>();
		for (int i = 0; i < to.size(); i++)
			todoBuffer.add(to.get(i));
		for (int i = 0; i < from.size(); i++) {
			if (from.get(i) == null) {
				System.out.println("HÄ?");
			}
			if (!grid.passable(from.get(i).x, from.get(i).y))
				throw new RuntimeException("FROM " + from.get(i)
						+ " not passable!");
		}

		while (todoBuffer.size() > 0) {
			PathPos current = todoBuffer.first();

			todoBuffer.removeFirst();

			if (from.contains(current) && !found.contains(current)) {
				found.add(current);
				if (fastOut && found.size() == from.size())
					return true;
			}
			if (current.value > 0)
				if (!grid.passable(current.x, current.y)) {
					continue;
				}

			int currentValue = current.value;
			GridElement currentElement = grid.get(current.tmp());
			if (currentElement != null) {
				accessor.write(currentElement, currentValue);
			} else
				currentValue = 0;
			for (PathPos next : current.next()) {
				if (next.equals(ignoreThis))
					continue;
				GridElement ge = grid.get(next.tmp());
				if (ge != null) {

					if (accessor.read(ge) < 0) {
						todoBuffer.add(next);
					}

				}
			}
		}
		grid.print();
		return found.size() == from.size();
	}
}
