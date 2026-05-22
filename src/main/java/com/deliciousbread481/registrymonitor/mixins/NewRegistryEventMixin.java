package com.deliciousbread481.registrymonitor.mixins;  
  
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.PrintWriter;  
import java.nio.file.Files;  
import java.nio.file.Path;  
import java.nio.file.Paths;  
import java.time.LocalDateTime;  
import java.time.format.DateTimeFormatter;  
import net.minecraft.resources.ResourceLocation;  
import net.neoforged.neoforge.registries.NewRegistryEvent;  
import net.minecraft.core.Registry;  
import org.spongepowered.asm.mixin.Mixin;  
import org.spongepowered.asm.mixin.injection.At;  
import org.spongepowered.asm.mixin.injection.Inject;  
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;  
  
@Mixin(NewRegistryEvent.class)  
public class NewRegistryEventMixin {  
      
    private static final String TARGET_REGISTRY = "minecraft:wolf_sound_variant";  
    private static final String LOG_DIR = "logs";  
    private static final String LOG_FILE = "registry_monitor.log";  
      
    @Inject(method = "register", at = @At("HEAD"))  
    private void onRegister(Registry<?> registry, CallbackInfo ci) {  
        ResourceLocation registryName = registry.key().location();  
          
        if (registryName.toString().equals(TARGET_REGISTRY)) {  
            logRegistryRegistration(registryName);  
        }  
    }  
      
    private void logRegistryRegistration(ResourceLocation registryName) {  
        try {  
            String version = "1.21.1";
            Path logDir = Paths.get(LOG_DIR, version);  
            Files.createDirectories(logDir);  
              
            Path logFile = logDir.resolve(LOG_FILE);  
              
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile.toFile(), true))) {  
                writer.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));  
                writer.println("Registry: " + registryName);  
                writer.println("Call Stack:");  
                  
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();  
                for (StackTraceElement element : stackTrace) {  
                    writer.println("    " + element);  
                }  
                  
                writer.println();  
                writer.flush();  
            }  
              
            System.err.println("[RegistryMonitor] Logged registration of " + registryName + " to " + logFile);  
              
        } catch (IOException e) {  
            System.err.println("[RegistryMonitor] Failed to write log file: " + e.getMessage());  
            e.printStackTrace();  
        }  
    }  
}