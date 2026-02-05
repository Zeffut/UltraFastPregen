package com.ultrafastpregen.generation;

public enum GenerationShape {
    SQUARE("Carré"),
    CIRCLE("Cercle");
    
    private final String displayName;
    
    GenerationShape(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static GenerationShape fromString(String str) {
        if (str == null) return SQUARE;
        
        switch (str.toLowerCase()) {
            case "circle":
            case "cercle":
            case "rond":
            case "c":
                return CIRCLE;
            case "square":
            case "carre":
            case "carré":
            case "s":
            default:
                return SQUARE;
        }
    }
}
