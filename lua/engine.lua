-- Hook registering
function registerHook(hookType, functionName, ...)
    EventEngine.hooks.registerHook(hookType, functionName, ...); -- Register the hook with the server.
end

-- Player object
Player = class('Player');
function Player:initialize(playerName)
    self.name = playerName;
end

function Player:getName()
    return self.name;
end

function Player:kill()
    EventEngine.player.kill(self.name);
end

function Player:sendMessage(message)
    EventEngine.player.sendMessage(self.name, message);
end

function Player:setHealth(health)
    EventEngine.player.setHealth(self.name, health);
end

function Player:teleport(location)
    if location.yaw ~= nil and location.pitch ~= nil then
        EventEngine.player.teleportToLocationRotation(self.name, location.world, location.x, location.y, location.z, location.yaw, location.pitch);
    else
        EventEngine.player.teleportToLocation(self.name, location.world, location.x, location.y, location.z);
    end
end

function Player:teleportToPlayer(player)
    EventEngine.player.teleportToPlayer(self.name, player.name);
end

function Player:cloneInventory(player)
    EventEngine.player.cloneInventory(player.name, self.name);
end

function Player:getLocation()
    return EventEngine.player.getLocation(self.name);
end

function Player:isDead()
    return EventEngine.player.isDead(self.name);
end

function Player:sendEvent(event)
    EventEngine.player.sendEvent(self.name, event);
end

function Player:clearInventory()
    EventEngine.player.clearInventory(self.name);
end

function Player:addItem(itemID, itemData, itemAmount)
    EventEngine.player.addItem(self.name, itemID, itemData, itemAmount);
end

function Player:isOnline()
    return EventEngine.player.isOnline(self.name);
end

-- World object
World = class('World');
function World:initialize(name)
    self.name = name;
end

function World:getPlayers()
    return EventEngine.world.getPlayers(self.name);
end

-- Location object
Location = class('Location');
function Location:initialize(world, x, y, z)
    if type(world) == "string" then
        self.world = world;
    elseif world.name ~= nil then
        self.world = world.name;
    else
        print("Error: Location object expects world or string!");
        return;
    end
    self.x = x;
    self.y = y;
    self.z = z;
end

function Location:setYaw(yaw)
    self.yaw = yaw;
end

function Location:setPitch(pitch)
    self.pitch = pitch;
end

function Location:getClosestPlayer()
    return Player:new(EventEngine.player.getPlayerAtLocation(self.world, self.x, self.y, self.z));
end

function Location:setBlock(blockID, data)
    EventEngine.world.setBlock(self.world, self.x, self.y, self.z, blockID, data);
end

function Location:getBlock()
    return EventEngine.world.getBlock(self.world, self.x, self.y, self.z);
end

function Location:playSong(file, volume)
    return Song:new(EventEngine.sound.playSong(self.world, self.x, self.y, self.z, file, volume));
end

function Location:lightningStrike()
    EventEngine.effects.strikeLightning(self.world, self.x, self.y, self.z);
end

function Location:firework(type, colour, fade, flicker, trail)
    EventEngine.effects.firework(self.world, self.x, self.y, self.z, type, colour, fade, flicker, trail);
end

function Location:explosion(power, breakBlocks, fire)
    EventEngine.effects.explosion(self.world, self.x, self.y, self.z, power, breakBlocks, fire);
end

-- AI Object
AI = class('AI');
function AI:initialize(name, group, world)
    self.id = EventEngine.ai.create(name, group, world);
end

function AI:speak(message)
    EventEngine.ai.speak(self.id, message);
end

-- Timer object
Timer = class('Timer');
function Timer:initialize(func, delay)
    self.func = func;
    self.delay = delay;
end

function Timer:startRepeating()
    self.id = EventEngine.timer.scheduleRepeatingTask(self.func, self.delay);
end

function Timer:start()
    self.id = EventEngine.timer.scheduleTak(self.func, self.delay);
end

function Timer:cancel()
    if self.id ~= nil then
        EventEngine.timer.cancelTask(self.id);
    end
    self.id = nil;
end