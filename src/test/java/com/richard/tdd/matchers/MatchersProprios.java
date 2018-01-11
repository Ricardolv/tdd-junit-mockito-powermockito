package com.richard.tdd.matchers;

import java.util.Calendar;

public class MatchersProprios {
	
	public static DiaSemanaMatcher caiEm(final Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}

	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
}
