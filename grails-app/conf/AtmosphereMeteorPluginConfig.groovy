grails {
	plugin {
		atmosphere_meteor {
			plugin {
				initializeBeans {
					delay = 1000  // milliseconds
					period = 2000 // milliseconds
					attempts = 30 // number of attempts the TimerTask will make before quitting
				}
			}
		}
	}
}
