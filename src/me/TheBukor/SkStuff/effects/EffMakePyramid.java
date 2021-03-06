package me.TheBukor.SkStuff.effects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Patterns;
import com.sk89q.worldedit.function.pattern.RandomPattern;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffMakePyramid extends Effect {
	private Expression<Location> location;
	private Expression<Integer> radius;
	private Expression<EditSession> editSession;
	private Expression<ItemStack> blockList;
	private boolean filled = true;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult result) {
		location = (Expression<Location>) expr[0];
		radius = (Expression<Integer>) expr[1];
		editSession = (Expression<EditSession>) expr[2];
		blockList = (Expression<ItemStack>) expr[3];
		if (result.mark == 1)
			filled = false;
		return true;
	}
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "create a pyramid at " + location.toString(e, debug) + " with a radius of " + radius.toString(e, debug) + " using an edit session with " + blockList.toString(e, debug);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		Location loc = location.getSingle(e);
		Integer rad = radius.getSingle(e);
		EditSession session = editSession.getSingle(e);
		ItemStack[] blocks = blockList.getAll(e);
		RandomPattern random = new RandomPattern();
		if (session == null) return;
		for (ItemStack b : blocks) {
			if (b.getType().isBlock()) {
				random.add(new BlockPattern(new BaseBlock(b.getTypeId(), b.getDurability())), 50);
			}
		}
		try {
			session.makePyramid(BukkitUtil.toVector(loc), Patterns.wrap(random), rad, filled);
			session.flushQueue();
		} catch (WorldEditException ex) {
			if (ex instanceof MaxChangedBlocksException)
				return;
			else
				ex.printStackTrace();
		}
	}
}