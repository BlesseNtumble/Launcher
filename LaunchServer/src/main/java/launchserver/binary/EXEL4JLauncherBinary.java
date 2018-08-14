package launchserver.binary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import launcher.Launcher;
import launcher.LauncherAPI;
import launcher.helper.IOHelper;
import launcher.helper.LogHelper;
import launchserver.LaunchServer;
import net.sf.launch4j.Builder;
import net.sf.launch4j.Log;
import net.sf.launch4j.config.Config;
import net.sf.launch4j.config.ConfigPersister;
import net.sf.launch4j.config.Jre;
import net.sf.launch4j.config.LanguageID;
import net.sf.launch4j.config.VersionInfo;

public final class EXEL4JLauncherBinary extends LauncherBinary {
    // URL constants
    private static final String DOWNLOAD_URL = "http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html"; // Oracle JRE 8

    // File constants
    private final Path faviconFile;

    @LauncherAPI
    public EXEL4JLauncherBinary(LaunchServer server) {
        super(server, server.dir.resolve(EXELauncherBinary.EXE_BINARY_FILE));
        faviconFile = server.dir.resolve("favicon.ico");
        setConfig();
    }

    @Override
    public void build() throws IOException {
        LogHelper.info("Building launcher EXE binary file (Using Launch4J)");

        // Set favicon path
        Config config = ConfigPersister.getInstance().getConfig();
        if (IOHelper.isFile(faviconFile)) {
            config.setIcon(new File("favicon.ico"));
        } else {
            config.setIcon(null);
            LogHelper.warning("Missing favicon.ico file");
        }

        // Start building
        Builder builder = new Builder(Launch4JLog.INSTANCE);
        try {
            builder.build();
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    private void setConfig() {
        Config config = new Config();

        // Set string options
        config.setChdir(".");
        config.setErrTitle("JVM Error");
        config.setDownloadUrl(DOWNLOAD_URL);

        // Set boolean options
        config.setPriorityIndex(0);
        config.setHeaderType(Config.GUI_HEADER);
        config.setStayAlive(false);
        config.setRestartOnCrash(false);

        // Prepare JRE
        Jre jre = new Jre();
        jre.setMinVersion("1.8.0");
        jre.setRuntimeBits(Jre.RUNTIME_BITS_64_AND_32);
        jre.setJdkPreference(Jre.JDK_PREFERENCE_PREFER_JRE);
        config.setJre(jre);

        // Prepare version info (product)
        VersionInfo info = new VersionInfo();
        info.setProductName("sashok724's Launcher v3 mod by Gravit");
        info.setProductVersion("1.0.0.0");
        info.setTxtProductVersion(Launcher.VERSION + ", build " + Launcher.BUILD);

        // Prepare version info (file)
        info.setFileDescription("sashok724's Launcher v3 mod by Gravit");
        info.setFileVersion("1.0.0.0");
        info.setTxtFileVersion(Launcher.VERSION + ", build " + Launcher.BUILD);
        info.setOriginalFilename(binaryFile.getFileName().toString());

        // Prepare version info (misc)
        info.setInternalName("Launcher");
        info.setCopyright("© sashok724 LLC");
        info.setTrademarks("This product is licensed under MIT License");
        info.setLanguage(LanguageID.ENGLISH_US);
        config.setVersionInfo(info);

        // Set JAR wrapping options
        config.setDontWrapJar(false);
        config.setJar(server.launcherBinary.binaryFile.toFile());
        config.setOutfile(binaryFile.toFile());

        // Return prepared config
        ConfigPersister.getInstance().setAntConfig(config, null);
    }

    private static final class Launch4JLog extends Log {
        private static final Launch4JLog INSTANCE = new Launch4JLog();

        @Override
        public void append(String s) {
            LogHelper.subInfo(s);
        }

        @Override
        public void clear() {
            // Do nothing
        }
    }
    
    public Map<String, String> parse(Path ini) throws IOException {
    	Map<String, String> toRet = new HashMap<String, String>();
    	int i = 0;
    	for (String line : java.nio.file.Files.readAllLines(ini)) {
    		/*for (char c : line.toCharArray()) {
    			if (c == '=') {
    				System.a
    			} else i++;
    		}
    		String[] props = line.split("=");
    		if (props.length > 1)
    		toRet.put(props[0], props[1]);
    		*/
    	}
    	return toRet;
    }
}
