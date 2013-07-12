-- Hook registering
function registerHook(hookType, functionName, ...)
    engine.hooks.registerHook(hookType, functionName, ...); -- Register the hook with the server.
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
    api.player.kill(self.name);
end

function Player:sendMessage(message)
    api.player.sendMessage(self.name, message);
end

function Player:setHealth(health)
    api.player.setHealth(self.name, health);
end

function Player:teleport(location)
    if location.yaw ~= nil and location.pitch ~= nil then
        api.player.teleportToLocationRotation(self.name, location.world, location.x, location.y, location.z, location.yaw, location.pitch);
    else
        api.player.teleportToLocation(self.name, location.world, location.x, location.y, location.z);
    end
end

function Player:teleportToPlayer(player)
    api.player.teleportToPlayer(self.name, player.name);
end

function Player:cloneInventory(player)
    api.player.cloneInventory(player.name, self.name);
end

function Player:getLocation()
    return api.player.getLocation(self.name);
end

function Player:isDead()
    return api.player.isDead(self.name);
end

function Player:sendEvent(event)
    api.player.sendEvent(self.name, event);
end

function Player:clearInventory()
    api.player.clearInventory(self.name);
end

function Player:addItem(itemID, itemData, itemAmount)
    api.player.addItem(self.name, itemID, itemData, itemAmount);
end

-- Location object
Location = class('Location');
function Location:initialize(world, x, y, z)
    self.world = world;
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
    return Player:new(api.player.getPlayerAtLocation(self.world, self.x, self.y, self.z));
end

function Location:setBlock(blockID, data)
    api.world.setBlock(self.world, self.x, self.y, self.z, blockID, data);
end

function Location:getBlock()
    return api.world.getBlock(self.world, self.x, self.y, self.z);
end

function Location:playSong(file, volume)
    return Song:new(api.sound.playSong(self.world, self.x, self.y, self.z, file, volume));
end

function Location:lightningStrike()
    api.effects.strikeLightning(self.world, self.x, self.y, self.z);
end

function Location:firework(type, colour, fade, flicker, trail)
    api.effects.firework(self.world, self.x, self.y, self.z, type, colour, fade, flicker, trail);
end

function Location:explosion(power, breakBlocks, fire)
    api.effects.explosion(self.world, self.x, self.y, self.z, power, breakBlocks, fire);
end

-- AI Object
AI = class('AI');
function AI:initialize(name, group, world)
    self.id = api.ai.create(name, group, world);
end

function AI:speak(message)
    api.ai.speak(self.id, message);
end

-- Song object
Song = class('Song');
function Song:initialize(id)
    self.id = id;
end

function Song:stop()
    api.song.stopSong(self.id);
end