package com.pkadev.pkaadventure.types;

import com.pkadev.pkaadventure.interfaces.Ability;
import com.pkadev.pkaadventure.objects.abilities.Flame_Thrower;
import com.pkadev.pkaadventure.objects.abilities.Berzerk;
import com.pkadev.pkaadventure.utils.MessageUtil;

public enum AbilityType {

	Flame_Thrower(Flame_Thrower.class),
	Berzerk(Berzerk.class);
	
	private Class<? extends Ability> ability;
	
	AbilityType(Class<? extends Ability> ability) {
		setAbility(ability);
	}
	
	private void setAbility(Class<? extends Ability> ability) {
		this.ability = ability;
	}
	
	public Ability getAbility() {
		try {
			return ability.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			MessageUtil.severe("Could not initialize new instance of " + ability.toString());
			e.printStackTrace();
		}
		return null;
	}
	
}
