package me.jumper251.replay;


import java.util.HashMap;


import me.jumper251.replay.dev.mrflyn.extended.VanillaListeners;
import me.jumper251.replay.dev.mrflyn.extended.worldmanagers.IWorldManger;
import me.jumper251.replay.dev.mrflyn.extended.worldmanagers.SWMWorldManager;
import me.jumper251.replay.dev.mrflyn.extended.worldmanagers.VanillaWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;



import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.filesystem.saving.DatabaseReplaySaver;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.utils.ReplayCleanup;
import me.jumper251.replay.utils.Metrics;
import me.jumper251.replay.utils.ReplayManager;
import me.jumper251.replay.utils.Updater;


public class ReplaySystem extends JavaPlugin {

	
	public static ReplaySystem instance;
	
	public static Updater updater;
	public static Metrics metrics;
	public IWorldManger worldManger;
	public VanillaWorldManager vanillaWorldManager;
	
	public final static String PREFIX = "§8[§3Replay§8] §r§7";

	
	@Override
	public void onDisable() {
		for (Replay replay : new HashMap<>(ReplayManager.activeReplays).values()) {
		    if (replay.isRecording() && replay.getRecorder().getData().getActions().size() > 0) {
				replay.getRecorder().stop(ConfigManager.SAVE_STOP);
			}
		}

	}
	
	@Override
	public void onEnable() {
		instance = this;

		if (Bukkit.getServer().getPluginManager().getPlugin("SlimeWorldManager")!=null){
			worldManger = new SWMWorldManager();

		}else {
			worldManger = new VanillaWorldManager();
		}



		vanillaWorldManager = new VanillaWorldManager();

		Long start = System.currentTimeMillis();

		getLogger().info("Loading Replay v" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0));
		
		ConfigManager.loadConfigs();
		ReplayManager.register();
		ReplaySaver.register(ConfigManager.USE_DATABASE ? new DatabaseReplaySaver() : new DefaultReplaySaver());

		if (ConfigManager.UPLOAD_WORLDS&&ConfigManager.USE_DATABASE){
			getServer().getPluginManager().registerEvents(worldManger.getListener(), this);

		}


		updater = new Updater();
		metrics = new Metrics(this, 2188);
		
		if (ConfigManager.CLEANUP_REPLAYS > 0) {
			ReplayCleanup.cleanupReplays();
		}

		for(World w : Bukkit.getWorlds()){
			worldManger.onWorldLoad(w);
		}

		getLogger().info("Finished (" + (System.currentTimeMillis() - start) + "ms)");

	}
	
	
	public static ReplaySystem getInstance() {
		return instance;
	}
}
