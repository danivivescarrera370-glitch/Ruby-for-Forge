package com.example.rubymod.language;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.neoforgespi.language.IModLanguageProvider;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * Custom Language Adapter for NeoForge to bootstrap Ruby-based mods via JRuby.
 */
public class RubyLanguageAdapter implements IModLanguageProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String name() {
        // Must match the 'language' target string specified in your mods.toml
        return "ruby";
    }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() {
        // Allows scanning classpath / JAR annotations if your scripts utilize them
        return scanData -> {
            LOGGER.debug("Scanning classpath metadata for Ruby mod annotations...");
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends IModLanguageLoader> R getLanguageLoader() {
        return (R) new RubyLanguageLoader();
    }

    public static class RubyLanguageLoader implements IModLanguageLoader {

        @Override
        public <T> T loadMod(ModContainer container, ModFileScanData scanData, ClassLoader classLoader) {
            try {
                // Pull custom ruby script configurations from the mods.toml file definitions
                String scriptPath = (String) container.getCustomModProperties().get("rubyScript");
                if (scriptPath == null) {
                    scriptPath = container.getModId() + "/main.rb"; // Standard structural fallback
                }

                LOGGER.info("Loading Ruby entry point for mod '{}' from: {}", container.getModId(), scriptPath);

                // Initialize your JRuby runtime environment container
                org.jruby.embed.ScriptingContainer rubyContainer = new org.jruby.embed.ScriptingContainer();
                rubyContainer.setClassLoader(classLoader);

                // Execute the target script file through the classloader context stream
                Object rubyEntryInstance = rubyContainer.runScriptlet(
                    classLoader.getResourceAsStream(scriptPath), 
                    scriptPath
                );

                // Returns the initialized instance to be tracked inside NeoForge's active lifecycle context
                return (T) rubyEntryInstance;

            } catch (Exception e) {
                LOGGER.error("Failed executing Ruby startup sequences", e);
                throw new ModLoadingException(
                    ModLoadingIssue.error("fml.modloading.failedtoloadmod")
                        .withCause(e)
                        .withAffectedMod(container.getModInfo())
                );
            }
        }
    }
}
