<h1>BW Death Chest</h1>
<p>BW Death Chest is a Spigot plugin that - upon a players death - places a chest and fills it with the players inventory. The chest is marked by a sign stating the players name as well as when they died according to the server's calendar.</p>

<h2>1. Dependencies</h2>
<p>DW Death Chest is dependant on <a href = "https://github.com/Msvenda/TimeManagement/tree/master/BWTimeManagement">BWTimeManagement</a> and will not work without it.</p>

<h2>2. Plugin commands</h2>
<b>doDeathHere:</b>
<list>
    <li>Enables or disables death chests in world</li>
    <li>Required permission: deathchest.manager</li>
    <li>Usage: "/doDeathHere [boolean]"</li>
</list>
<h2>3. Plugin configuration</h2>

<p>The plugin will automatically generate a configuration file if it does not find a valid one located at <code>...plugins/BWDeathChest/config.yml</code>. The structure of the config file is as follows:</p>

<p>
<code>
worlds: [] #list of uid's of excluded worlds, populated through doDeathHere command.</br>
</code>
</p>