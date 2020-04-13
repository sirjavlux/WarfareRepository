package com.coding.sirjavlux.projectiles;

public enum BodyPart {
	
	Head(1.7),
	Leg(0.7),
	Chest(1);
	
    private final double modifier;

    BodyPart(double modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return modifier + "";
    }
}
