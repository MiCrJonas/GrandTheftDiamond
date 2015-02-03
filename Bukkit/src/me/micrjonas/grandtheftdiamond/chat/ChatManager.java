package me.micrjonas.grandtheftdiamond.chat;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;
import me.micrjonas.grandtheftdiamond.util.Enums;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class ChatManager implements FileReloadListener, PlayerDataUser {
	
	private static ChatManager instance = new ChatManager();
	
	public static ChatManager getInstance() {
		return instance;
	}
	
	private ChatMode defaultChatType;
	private final Map<UUID, ChatMode> playerChatModes = new HashMap<>();
	private final Map<ChatMode, String> messagePrefix = new EnumMap<>(ChatMode.class);
	
	private ChatManager() {
		GrandTheftDiamond.registerPlayerDataUser(this);
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			for (ChatMode mode : ChatMode.values()) {
				messagePrefix.put(mode, fileConfiguration.getString("chat." + mode.name().toLowerCase() + ".messagePrefix"));
			}
		}
	}
	
	@Override
	public void clearPlayerData(UUID player) {
		FileManager.getInstance().getPlayerData(player).set("chatType", playerChatModes.get(player).name());
		playerChatModes.remove(player);
	}

	@Override
	public void loadPlayerData(UUID player) {
		ChatMode chatType = Enums.getEnumFromConfig(ChatMode.class, FileManager.getInstance().getPlayerData(player), "chatType");
		if (chatType != null) {
			this.playerChatModes.put(player, chatType);
		}
	}
	
	/**
	 * Returns the {@link ChatMode} of the Player with the given UUID
	 * @param playerId The UUID of the Player
	 * @return The {@link ChatMode} of the Player with the given UUID
	 * @throws IllegalArgumentException If playerId is {@code null}
	 */
	public ChatMode getChatMode(UUID playerId) throws IllegalArgumentException {
		if (playerId == null) {
			throw new IllegalArgumentException("playerId is not allowed to be null");
		}
		ChatMode mode = playerChatModes.get(playerId);
		if (mode == null) {
			return defaultChatType;
		}
		return mode;
	}
	
	/**
	 * Returns the {@link ChatMode} of the given Player
	 * @param p The Player to get the {@link ChatMode}
	 * @return The {@link ChatMode} of the given Player
	 * @throws NullPointerException If p is {@code null}
	 */
	public ChatMode getChatMode(Player p) throws NullPointerException {
		return getChatMode(p.getUniqueId());
	}
	
	/**
	 * Sets the {@link ChatMode} of the Player with the given UUID
	 * @param playerId The UUID of the Player
	 * @param mode The {@link ChatMode} to set
	 * @throws IllegalArgumentException If playerId is {@code null}
	 */
	public void setChatMode(UUID playerId, ChatMode mode) throws IllegalArgumentException {
		if (playerId == null) {
			throw new IllegalArgumentException("playerId is not allowed to be null");
		}
		if (mode == null) {
			playerChatModes.remove(playerId);
		}
		else {
			playerChatModes.put(playerId, mode);
		}
	}
	
	/**
	 * Sets the {@link ChatMode} of the given Player
	 * @param p The Player to set the {@link ChatMode}
	 * @param mode The {@link ChatMode} to set; null for default mode
	 * @throws IllegalArgumentException If p is {@code null}
	 */
	public void setChatMode(Player p, ChatMode mode) throws IllegalArgumentException {
		setChatMode(p.getUniqueId(), mode);
	}
	
	/**
	 * Returns the message prefix of the given {@link ChatMode}
	 * @param mode The {@link ChatMode} to get the message prefix
	 * @return The message prefix of the given {@link ChatMode}
	 * @throws IllegalArgumentException If mode is {@code null}
	 */
	public String getMessagePrefix(ChatMode mode) throws IllegalArgumentException {
		if (mode == null) {
			throw new IllegalArgumentException("mode is not allowed to be null");
		}
		return messagePrefix.get(mode);
	}

}
