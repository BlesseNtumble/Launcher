package launchserver.manangers;

import launcher.AutogenConfig;
import launchserver.binary.JAConfigurator;

import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipOutputStream;

public class BuildHookManager {
    private static final Set<PostBuildHook> POST_HOOKS = new HashSet<>(4);
    private static final Set<PreBuildHook> PRE_HOOKS = new HashSet<>(4);
    private static final Set<String> CLASS_BLACKLIST = new HashSet<>(4);
    private static final Set<String> MODULE_CLASS = new HashSet<>(4);
    public static void registerPostHook(PostBuildHook hook)
    {
        POST_HOOKS.add(hook);
    }
    public static void registerIgnoredClass(String clazz)
    {
        CLASS_BLACKLIST.add(clazz);
    }
    public static void registerClientModuleClass(String clazz)
    {
        MODULE_CLASS.add(clazz);
    }
    public static void registerAllClientModuleClass(JAConfigurator cfg)
    {
        for(String clazz : MODULE_CLASS) cfg.addModuleClass(clazz);
    }
    public static boolean isContainsBlacklist(String clazz)
    {
        return CLASS_BLACKLIST.contains(clazz);
    }
    public static void postHook(ZipOutputStream output)
    {
        for(PostBuildHook hook : POST_HOOKS) hook.build(output);
    }
    public static void preHook(ZipOutputStream output)
    {
        for(PreBuildHook hook : PRE_HOOKS) hook.build(output);
    }
    public static void registerPreHook(PreBuildHook hook)
    {
        PRE_HOOKS.add(hook);
    }
    @FunctionalInterface
    public interface PostBuildHook
    {
        void build(ZipOutputStream output);
    }
    @FunctionalInterface
    public interface PreBuildHook
    {
        void build(ZipOutputStream output);
    }
    static {
        registerIgnoredClass(AutogenConfig.class.getName().replace('.','/').concat(".class"));
    }
}
