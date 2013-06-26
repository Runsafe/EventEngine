-- Hook registering
function registerHook(hookType, functionName, ...)
    engine.hooks.registerHook(hookType, functionName, ...); -- Register the hook with the server.
end

-- Player object
Player = {};
function Player:new(playerName)
    local o = {};
    setmetatable(o, self);
    self.__index = self;
    o.name = playerName;
    return o;
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
    engine.player.teleportToLocation(location.world, location.x, location.y, location.z);
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

-- Location object
Location = {};
function Location:new(world, x, y, z)
    local o = {
        world = world,
        x = x,
        y = y,
        z = z
    };
    setmetatable(o, self);
    self.__index = self;
    return o;
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
AI = {};
function AI:new(name, world)
    local o = {
        id = engine.ai.create(name, world)
    }
    setmetatable(o, self);
    self.__index = self;
    return o;
end

function AI:speak(message)
    engine.ai.speak(self.id, message);
end

-- Song object
Song = {};
function Song:new(id)
    local o = {
        id = id
    };
    setmetatable(o, self);
    self.__index = self;
    return o;
end

function Song:stop()
    engine.song.stopSong(self.id);
end