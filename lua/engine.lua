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

function Player:addPermission(permission)
    EventEngine.player.addPermission(self.name, permission);
end

function Player:addWorldPermission(permission, world)
    EventEngine.player.addWorldPermission(self.name, permission, world);
end

function Player:removePermission(permission)
    EventEngine.player.removePermission(self.name, permission);
end

function Player:removeWorldPermission(permission, world)
    EventEngine.player.removeWorldPermission(self.name, permission, world);
end

function Player:removePotionEffects()
    EventEngine.player.removePotionEffects(self.name);
end

function Player:closeInventory()
    EventEngine.player.closeInventory(self.name);
end

function Player:setVelocity(x, y, z)
    EventEngine.player.setVelocity(self.name, x, y, z);
end

function Player:dismount()
    EventEngine.player.dismount(self.name);
end

-- World object
World = class('World');
function World:initialize(name)
    self.name = name;
end

function World:getPlayers()
    return EventEngine.world.getPlayers(self.name);
end

function World:broadcast(message)
    local players = {self:getPlayers()};
    for index, playerName in pairs(players) do
        local player = Player:new(playerName);
        if player:isOnline() then
            player:sendMessage(message);
        end
    end
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

function Location:playSound(sound, volume, pitch)
    EventEngine.world.playSound(self.world, self.x, self.y, self.z, sound, volume, pitch);
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

function Location:cloneChestToPlayer(player)
    EventEngine.world.cloneChestToPlayer(self.world, self.x, self.y, self.z, player);
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
    self.id = EventEngine.timer.scheduleTask(self.func, self.delay);
end

function Timer:cancel()
    if self.id ~= nil then
        EventEngine.timer.cancelTask(self.id);
    end
    self.id = nil;
end

-- Entity object
Entity = class('Entity');
function Entity:initialize(location)
    self.world = location.world;
    self.x = location.x;
    self.y = location.y;
    self.z = location.z;
end

function Entity:spawn(entityType)
    self.entityID = EventEngine.mobs.spawnEntity(entityType, self.world, self.x, self.y, self.z);
end

function Entity:spawnMinecart(itemID, dataValue)
    self.entityID = EventEngine.mobs.spawnCustomMinecart(itemID, dataValue, self.world, self.x, self.y, self.z);
end

function Entity:spawnControlledEntity(entityType)
    self.entityID = EventEngine.mobs.spawnControlledEntity(entityType, self.world, self.x, self.y, self.z);
end

function Entity:goTo(loc, speed)
    EventEngine.mobs.goTo(self.entityID, loc.world, loc.x, loc.y, loc.z, speed);
end

function Entity:despawn()
    if self.entityID ~= nil then
        EventEngine.mobs.despawnEntity(self.world, self.entityID);
    end
end

function Entity:putOnPlayer(player)
    if self.entityID ~= nil then
        EventEngine.mobs.putOnPlayer(self.world, self.entityID, player);
    end
end

function Entity:putPlayerOn(player)
    if self.entityID ~= nil then
        EventEngine.mobs.putPlayerOn(self.world, self.entityID, player);
    end
end

function Entity:dismount()
    if self.entityID ~= nil then
        EventEngine.mobs.dismount(self.world, self.entityID);
    end
end