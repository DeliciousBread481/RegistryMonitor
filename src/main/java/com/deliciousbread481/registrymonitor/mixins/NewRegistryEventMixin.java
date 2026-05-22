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
import net.neoforged.fml.loading.FMLLoader;  
import net.neoforged.neoforge.registries.NewRegistryEvent;  
import net.minecraft.core.Registry;  
import org.spongepowered.asm.mixin.Mixin;  
import org.spongepowered.asm.mixin.injection.At;  
import org.spongepowered.asm.mixin.injection.Inject;  
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;  
  
@Mixin(NewRegistryEvent.class)  
public class NewRegistryEventMixin {  
      
    private static final String TARGET_REGISTRY = "minecraft:wolf_sound_variant";  
    private static final String LOG_FILE = "registry_monitor.log";  
      
    static {  
        System.err.println("[RegistryMonitor] NewRegistryEventMixin loaded!");  
        try {  
            Path gamePath = FMLLoader.getGamePath();  
            Path logFile = gamePath.resolve(LOG_FILE);  
            System.err.println("[RegistryMonitor] Game path: " + gamePath);  
            System.err.println("[RegistryMonitor] Log file will be: " + logFile);  
              
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile.toFile(), true))) {  
                writer.println("Registry Monitor Mod Initialized");  
                writer.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));  
                writer.println("Game Path: " + gamePath);  
                writer.println();  
                writer.flush();  
            }  
        } catch (Exception e) {  
            System.err.println("[RegistryMonitor] Failed to initialize log: " + e.getMessage());  
            e.printStackTrace();  
        }  
    }  
      
    @Inject(method = "register", at = @At("HEAD"))  
    private void onRegister(Registry<?> registry, CallbackInfo ci) {  
        ResourceLocation registryName = registry.key().location();  
        
        System.err.println("[RegistryMonitor] Registry registered: " + registryName);  
          
        logRegistryRegistration(registryName, false);  
        
        if (registryName.toString().equals(TARGET_REGISTRY)) {  
            System.err.println("[RegistryMonitor] TARGET REGISTRY FOUND: " + registryName);  
            logRegistryRegistration(registryName, true);  
        }  
    }  
      
    private void logRegistryRegistration(ResourceLocation registryName, boolean isTarget) {  
        try {  
            Path gamePath = FMLLoader.getGamePath();  
            Path logFile = gamePath.resolve(LOG_FILE);  
              
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile.toFile(), true))) {  
                writer.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));  
                writer.println("Registry: " + registryName);  
                writer.println("Is Target: " + isTarget);  
                  
                if (isTarget) {  
                    writer.println("Call Stack:");  
                    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();  
                    for (StackTraceElement element : stackTrace) {  
                        writer.println("    " + element);  
                    }  
                }  
                  
                writer.println();  
                writer.flush();  
            }  
              
        } catch (IOException e) {  
            System.err.println("[RegistryMonitor] Failed to write log file: " + e.getMessage());  
            e.printStackTrace();  
        }  
    }  
}