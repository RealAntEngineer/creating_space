package com.rae.creatingspace;

import java.lang.reflect.*;

public class PrintClassInfo {
    public static void main(String[] args) {
        printClass("com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder");
    }

    private static void printClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            System.out.println("=== CLASS: " + clazz.getName() + " ===");
            System.out.println("Fields:");
            for (Field f : clazz.getDeclaredFields()) {
                System.out.println("  " + Modifier.toString(f.getModifiers()) + " " + f.getType().getName() + " " + f.getName());
            }
        } catch (Exception e) {
            System.out.println("Failed to print class " + className + ": " + e.getMessage());
        }
    }
}
