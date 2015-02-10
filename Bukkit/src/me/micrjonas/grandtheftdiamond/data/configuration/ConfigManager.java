package me.micrjonas.grandtheftdiamond.data.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
 
public class ConfigManager {
 
	private final static ConfigManager instance = new ConfigManager();
	
	public static ConfigManager getInstance() {
		return instance;
	}
 
    private ConfigManager() { }
 
    /**
    * Get new configuration with header
    * @param filePath - Path to file
    * @return - New SimpleConfig
    */
    public Config getNewConfig(String filePath, String[] header) {
 
        File file = this.getConfigFile(filePath);
 
        if(!file.exists()) {
            this.prepareFile(filePath);
 
            if(header != null && header.length != 0) {
                this.setHeader(file, header);
            }
 
        }
 
        Config config = new Config(this.getConfigContent(filePath), file, this.getCommentsNum(file));
        return config;
 
    }
 
    /**
    * Get new configuration
    * @param filePath - Path to file
    * @return - New SimpleConfig
    */
    public Config getNewConfig(String filePath) {
        return this.getNewConfig(filePath, null);
    }
 
    /**
    * Get configuration file from string
    * @param file - File path
    * @return - New file object
    */
    private File getConfigFile(String file) {
 
        if(file == null || file.isEmpty()) {
            return null;
        }
 
        File configFile;
 
        if(file.contains("/")) {
 
            if(file.startsWith("/")) {
                configFile = new File(GrandTheftDiamond.getDataFolder() + File.separator + file.replace("/", File.separator));
            } else {
                configFile = new File(GrandTheftDiamond.getDataFolder() + File.separator + file.replace("/", File.separator));
            }
 
        } else {
            configFile = new File(GrandTheftDiamond.getDataFolder(), file);
        }
 
        return configFile;
 
    }
 
    /**
    * Create new file for config and copy resource into it
    * @param file - Path to file
    * @param resource - Resource to copy
    */
    public void prepareFile(String filePath, String resource) {
 
        File file = this.getConfigFile(filePath);
 
        if(file.exists()) {
            return;
        }
 
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
 
            if(resource != null && !resource.isEmpty()) {
                this.copyResource(BukkitGrandTheftDiamondPlugin.getInstance().getResource(resource), file);
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
 
    /**
    * Create new file for config without resource
    * @param file - File to create
    */
    public void prepareFile(String filePath) {
        this.prepareFile(filePath, null);
    }
 
    /**
    * Adds header block to config
    * @param file - Config file
    * @param header - Header lines
    */
    public void setHeader(File file, String[] header) {
 
        if(!file.exists()) {
            return;
        }
 
        try {
            String currentLine;
            StringBuilder config = new StringBuilder("");
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            while((currentLine = reader.readLine()) != null) {
                config.append(currentLine + "\n");
            }
 
            reader.close();
            config.append("# +------------------------------------------------------------------------+ #\n");
 
            for(String line : header) {
 
                if(line.length() > 50) {
                    continue;
                }
 
                int lenght = (70 - line.length()) / 2;
                StringBuilder finalLine = new StringBuilder(line);
 
                for(int i = 0; i < lenght; i++) {
                    finalLine.append(" ");
                    finalLine.reverse();
                    finalLine.append(" ");
                    finalLine.reverse();
                }
 
                if(line.length() % 2 != 0) {
                    finalLine.append(" ");
                }
 
                config.append("# < " + finalLine.toString() + " > #\n");
 
        }
 
        config.append("# +------------------------------------------------------------------------+ #");
 
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(this.prepareConfigString(config.toString()));
        writer.flush();
        writer.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
 
    /**
    * Read file and make comments SnakeYAML friendly
    * @param filePath - Path to file
    * @return - File as Input Stream
    */
    public String getConfigContent(File file) {
 
        if(!file.exists()) {
            return null;
        }
 
        try {
            int commentNum = 0;
 
            String addLine;
            String currentLine;
            int reqSpaces = 0;
 
            StringBuilder whole = new StringBuilder("");
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            while((currentLine = reader.readLine()) != null) {
 
                if(currentLine.startsWith("# ")) {
                	char[] spaces = new char[reqSpaces];
                	for (int i = 0; i < reqSpaces; i++) {
                		spaces[i] = ' ';
                	}
                    addLine = currentLine.replaceFirst("#", new String(spaces) + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine + "\n");
                    commentNum++;
                } else {
                	char[] chars = currentLine.toCharArray();
                	for (int spaceCount = 0; spaceCount <chars.length; spaceCount++) {
                		if (chars[spaceCount] != ' ') {
                			if (spaceCount > 0) {
                				reqSpaces = spaceCount + 2;
                			}
                			else {
                				reqSpaces = 2;
                			}
                			break;
                		}
                	}
                    whole.append(currentLine + "\n");
                }
            }
            reader.close();
            return whole.toString();
 
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
 
    }
 
    /**
    * Get comments from file
    * @param file - File
    * @return - Comments number
    */
    private int getCommentsNum(File file) {
 
        if(!file.exists()) {
            return 0;
        }
 
        try {
            int comments = 0;
            String currentLine;
 
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            while((currentLine = reader.readLine()) != null) {
 
                if(currentLine.startsWith("#")) {
                    comments++;
                }
 
            }
 
        reader.close();
        return comments;
 
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
 
    }
 
    /**
    * Get config content from file
    * @param filePath - Path to file
    * @return - readied file
    */
    public String getConfigContent(String filePath) {
        return this.getConfigContent(this.getConfigFile(filePath));
    }
 
 
    private String prepareConfigString(String configString) {
        boolean lastLineComment = true;
        int headerLineStart = 0;
 
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder("");
 
        for(String line : lines) {
        	StringBuilder lineBuilder = new StringBuilder(line.length());
        	char[] chars = line.toCharArray();
        	for (int i = 0; i < chars.length; i++) {
        		if (chars[i] != ' ') {
        			for (; i < chars.length; i++) {
        				lineBuilder.append(chars[i]);
        			}
        			break;
        		}
        	}
        	String tmpLine = lineBuilder.toString();
            if(tmpLine.startsWith("_COMMENT")) {
            	line = line.trim();
                String comment = "#" + line.trim().substring(line.indexOf(":") + 1);
                if(comment.startsWith("# +-")) {
                    /*
                    * If header line = 0 then it is
                    * header start, if it's equal
                    * to 1 it's the end of header
                    */
 
                    if(headerLineStart == 0) {
                        config.append(comment + "\n");
 
                        lastLineComment = true;
                        headerLineStart = 1;
 
                    } else if(headerLineStart == 1) {
                        config.append(comment + "\n\n");
 
                        lastLineComment = true;
                        headerLineStart = 0;
 
                    }
 
                } else {
 
                    /*
                    * Last line = 0 - Comment
                    * Last line = 1 - Normal path
                    */
 
                    String normalComment;
                    if(comment.startsWith("# ' ")) {
                        normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# ' ", "# ");
                    } else {
                        normalComment = comment;
                    }
 
                    if(lastLineComment == true) {
                        config.append(normalComment + "\n");
                    } else if(!lastLineComment) {
                        config.append("\n" + normalComment + "\n");
                    }
 
                    lastLineComment = true;
 
                }
 
            } else {
                config.append(line + "\n");
                lastLineComment = false;
            }
 
        }
        return config.toString();
    }
 
 
    /**
    * Saves configuration to file
    * @param configString - Config string
    * @param file - Config file
    */
    public void saveConfig(String configString, File file) {
        String configuration = this.prepareConfigString(configString);
 
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(configuration);
            writer.flush();
            writer.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
 
    /**
    * Copy resource from Input Stream to file
    * @param resource - Resource from .jar
    * @param file - File to write
    */
    private void copyResource(InputStream resource, File file) {
 
        try {
            OutputStream out = new FileOutputStream(file);
 
            int lenght;
            byte[] buf = new byte[1024];
 
            while((lenght = resource.read(buf)) > 0){
                out.write(buf, 0, lenght);
            }
 
            out.close();
            resource.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
}