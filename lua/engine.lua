require "middleclass"

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
    engine.player.kill(self.name);
end

function Player:sendMessage(message)
    engine.player.sendMessage(self.name, message);
end

function Player:setHealth(health)
    engine.player.setHealth(self.name, health);
end

function Player:teleport(location)
    if location.yaw ~= nil and location.pitch ~= nil then
        engine.player.teleportToLocationRotation(self.name, location.world, location.x, location.y, location.z, location.yaw, location.pitch);
    else
        engine.player.teleportToLocation(self.name, location.world, location.x, location.y, location.z);
    end
end

function Player:teleportToPlayer(player)
    engine.player.teleportToPlayer(self.name, player.name);
end

function Player:cloneInventory(player)
    engine.player.cloneInventory(player.name, self.name);
end

function Player:getLocation()
    return engine.player.getLocation(self.name);
end

function Player:isDead()
    return engine.player.isDead(self.name);
end

function Player:sendEvent(event)
    engine.player.sendEvent(self.name, event);
end

function Player:clearInventory()
    engine.player.clearInventory(self.name);
end

function Player:addItem(itemID, itemData, itemAmount)
    engine.player.addItem(self.name, itemID, itemData, itemAmount);
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
    return Player:new(engine.player.getPlayerAtLocation(self.world, self.x, self.y, self.z));
end

function Location:setBlock(blockID, data)
    engine.world.setBlock(self.world, self.x, self.y, self.z, blockID, data);
end

function Location:getBlock()
    return engine.world.getBlock(self.world, self.x, self.y, self.z);
end

function Location:playSong(file, volume)
    return Song:new(engine.sound.playSong(self.world, self.x, self.y, self.z, file, volume));
end

function Location:lightningStrike()
    engine.effects.strikeLightning(self.world, self.x, self.y, self.z);
end

function Location:firework(type, colour, fade, flicker, trail)
    engine.effects.firework(self.world, self.x, self.y, self.z, type, colour, fade, flicker, trail);
end

function Location:explosion(power, breakBlocks, fire)
    engine.effects.explosion(self.world, self.x, self.y, self.z, power, breakBlocks, fire);
end

-- AI Object
AI = class('AI');
function AI:initialize(name, group, world)
    self.id = engine.ai.create(name, group, world);
end

function AI:speak(message)
    engine.ai.speak(self.id, message);
end

-- Song object
Song = class('Song');
function Song:initialize(id)
    self.id = id;
end

function Song:stop()
    engine.song.stopSong(self.id);
end