include("Platform-BungeeCord")
project(":Platform-BungeeCord").name = "EnhancedGlist-BungeeCord"

include("Platform-BungeeCord-API")
project(":Platform-BungeeCord-API").name = "EnhancedGlist-BungeeCord-API"

include("Platform-Spigot")
project(":Platform-Spigot").name = "EnhancedGlist-Spigot-Bridge"

include("Platform-Common")
project(":Platform-Common").name = "EnhancedGlist-Common"

include("Platform-Velocity")
project(":Platform-Velocity").name = "EnhancedGlist-Velocity"

include("Platform-Velocity-API")
project(":Platform-Velocity-API").name = "EnhancedGlist-Velocity-API"

include("Updater")
project(":Updater").name = "EnhancedGlist-Updater"

include("Messenger")
project(":Messenger").name = "EnhancedGlist-Messenger"
