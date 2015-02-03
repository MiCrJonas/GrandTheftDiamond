package me.micrjonas.grandtheftdiamond.stats;

public enum StatsType {
	
	DEATHS(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	EXP(true) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	JAILED_GANGSTERS(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	KILLED_CIVILIANS(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	KILLED_COPS(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	KILLED_GANGSTERS(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	MONEY(true) {
		@Override
		public boolean validValue(int value) {
			return true;
		}
	},
	
	RESPECT(true) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	ROBBED_SAFES(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	TOTAL_WANTED_LEVEL(false) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	},
	
	WANTED_LEVEL(true) {
		@Override
		public boolean validValue(int value) {
			return value >= 0;
		}
	};
// End of static	

	private final boolean manipulable;
	
	private StatsType(boolean manipulable) {
		this.manipulable = manipulable;
	}
	
	public boolean manipulable() {
		return manipulable;
	}
	
	public abstract boolean validValue(int value);

}
