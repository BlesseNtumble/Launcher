package launchserver.binary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import launcher.Launcher;
import launcher.LauncherAPI;
import launcher.helper.IOHelper;
import launcher.helper.LogHelper;
import launcher.serialize.config.ConfigObject;
import launcher.serialize.config.entry.BlockConfigEntry;
import launcher.serialize.config.entry.BooleanConfigEntry;
import launcher.serialize.config.entry.StringConfigEntry;
import launcher.serialize.signed.SignedBytesHolder;
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
	private static final String DOWNLOAD_URL = "http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html"; // Oracle
																																		// JRE
																																		// 8

	public final ExeConf config;
	// File constants
	private final Path faviconFile;

	@LauncherAPI
	public EXEL4JLauncherBinary(LaunchServer server) {
		super(server, server.dir.resolve(server.config.binaryName + ".exe"));
		config = new ExeConf(server.config.launch4J);
		faviconFile = server.dir.resolve("favicon.ico");
		setConfig(config);
	}

	@Override
	public void build() throws IOException {
		if (config.enabled) {
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
		} else {
			if (IOHelper.isFile(binaryFile)) {
				LogHelper.subWarning("Deleting obsolete launcher EXE binary file");
				Files.delete(binaryFile);
			}
		}
	}

	@Override
	public boolean sync() throws IOException {
		return config.enabled ? super.sync() : !super.sync();
	}

	private void setConfig(ExeConf configCnt) {
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
		info.setProductName(configCnt.productName);
		info.setProductVersion(configCnt.productVer);
		info.setFileDescription(configCnt.fileDesc);
		info.setFileVersion(configCnt.fileVer);
		info.setCopyright(configCnt.copyright);
		info.setTrademarks(configCnt.trademarks);
		info.setInternalName(configCnt.internalName);
		// Prepare version info (file)
		info.setTxtFileVersion(Launcher.VERSION + ", build " + Launcher.BUILD);
		info.setTxtProductVersion(Launcher.VERSION + ", build " + Launcher.BUILD);
		// Prepare version info (misc)
		info.setOriginalFilename(binaryFile.getFileName().toString());
		info.setLanguage(LanguageID.RUSSIAN);
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

	public static class ExeConf extends ConfigObject {
		public final boolean enabled;
		public String productName;
		public String productVer;
		public String fileDesc;
		public String fileVer;
		public String internalName;
		public String copyright;
		public String trademarks;

		private ExeConf(BlockConfigEntry block) {
			super(block);
			enabled = block.getEntryValue("enabled", BooleanConfigEntry.class);
			productName = block.hasEntry("productName") ? block.getEntryValue("productName", StringConfigEntry.class)
					: "sashok724's Launcher v3 mod by Gravit";
			productVer = block.hasEntry("productVer") ? block.getEntryValue("productVer", StringConfigEntry.class)
					: "1.0.0.0";
			fileDesc = block.hasEntry("fileDesc") ? block.getEntryValue("fileDesc", StringConfigEntry.class)
					: "sashok724's Launcher v3 mod by Gravit";
			fileVer = block.hasEntry("fileVer") ? block.getEntryValue("fileVer", StringConfigEntry.class) : "1.0.0.0";
			internalName = block.hasEntry("internalName") ? block.getEntryValue("internalName", StringConfigEntry.class)
					: "Launcher";
			copyright = block.hasEntry("copyright") ? block.getEntryValue("copyright", StringConfigEntry.class)
					: "© sashok724 LLC";
			trademarks = block.hasEntry("trademarks") ? block.getEntryValue("trademarks", StringConfigEntry.class)
					: "This product is licensed under MIT License";
		}
	}
}
