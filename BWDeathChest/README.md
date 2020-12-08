# BW Death Chest

BW Death Chest is a Spigot plugin that - upon a players death - places a chest and fills it with the players inventory. The chest is marked by a sign stating the players name as well as when they died according to the server's calendar.

## 1\. Dependencies

DW Death Chest is dependant on [BWTimeManagement](https://github.com/Msvenda/TimeManagement/tree/master/BWTimeManagement) and will not work without it.

## 2\. Plugin commands

**doDeathHere:**  
- Enables or disables death chests in world   
- Required permission: deathchest.manager   
- Usage: "/doDeathHere [boolean]"</list>

## 3\. Plugin configuration

The plugin will automatically generate a configuration file if it does not find a valid one located at `...plugins/BWDeathChest/config.yml`. The structure of the config file is as follows:

    worlds: [] #list of uid's of excluded worlds, populated through doDeathHere command.  
