package net.mohron.skyclaims.island.layout;


import net.mohron.skyclaims.Region;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.claim.SkyClaim;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.util.ConfigUtil;

import java.awt.*;
import java.util.ArrayList;

public class SpiralLayout implements ILayout {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static GlobalConfig config = PLUGIN.getConfig();
	private static final int spawnRegions = (int) Math.pow(ConfigUtil.get(config.world.spawnRegions, 1), 2);

	/**
	 * A method to generate a region-scaled spiral pattern and return the x/y pairs of each region
	 *
	 * @return An ArrayList of Points containing the x,y of regions, representing a spiral shape
	 */
	private static ArrayList<Region> generateRegionSpiral() {
		int islandCount = SkyClaims.islands.size();
		int generationSize = (int) Math.sqrt((double) islandCount + spawnRegions) + 1;

		ArrayList<Region> coordinates = new ArrayList<>(generationSize);
		int[] delta = {0, -1};
		int x = 0;
		int y = 0;

		for (int i = (int) Math.pow(Math.max(generationSize, generationSize), 2); i > 0; i--) {
			if ((-generationSize / 2 < x && x <= generationSize / 2) && (-generationSize / 2 < y && y <= generationSize / 2))
				coordinates.add(new Region(x, y));
			if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
				// change direction
				int a = delta[0];
				delta[0] = -delta[1];
				delta[1] = a;
			}
			PLUGIN.getLogger().info(x + ", " + y);
			x += delta[0];
			y += delta[1];
		}

		return coordinates;
	}

	public Region nextRegion() {
		ArrayList<Island> currentIslands = new ArrayList<Island>(SkyClaims.islands.values());
		ArrayList<Region> regions = generateRegionSpiral();

		PLUGIN.getLogger().info(String.format("Checking for next region out of %s points with %s spawn regions.", regions.size(), spawnRegions));

		if (!regions.isEmpty()) {
			for (int i = 0; i < regions.size(); i++) {
				Region region = regions.get(i);

				// Skip the spawn regions for checking
				if (i < spawnRegions) {
					PLUGIN.getLogger().info(String.format("Skipping (%s, %s) for spawn", region.x, region.z));
					continue;
				}

				PLUGIN.getLogger().info(String.format("Checking region (%s, %s) for island", region.z, region.z));

				// If there are no islands, the one after spawn will be taken
				if (currentIslands.isEmpty())
					return region;

				// Ensure there are regions to get at this index
				for (Island island : currentIslands) {
					if (region.x == island.getRegionX() && region.z == island.getRegionZ())
						continue;
				}

//				for (Island island : SkyClaims.islands.values()) {
//					PLUGIN.getLogger().info(String.format("Checking %s's island (%s,%s) against region (%s, %s)", island.getOwnerName(), island.getRegionX(), island.getRegionZ(), point.x, point.y));
//					if (point.x == island.getRegionX() && point.y == island.getRegionZ())
//						continue point;
//					PLUGIN.getLogger().info(String.format("Next Available Region is (%s, %s)", point.x, point.y));
//					return point;
//				}
			}
		}

		return regions.get(regions.size() - 1);
	}
}