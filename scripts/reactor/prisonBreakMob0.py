# Key Chest (921160600) | Spawns mobs & Drops a key | Used in Escape Party Quest

PRISON_GUARD_BOAR = 9300452
PRISON_GUARD_RHINO = 9300453
PRISON_KEY = 4001528

hitCount = 0

def init():
    action(0)

def action(type):
    sm.chat(str(type))
    if type == 0:
        global hitCount
        hitCount += 1
        sm.chat(str(hitCount))
        if hitCount >= 1:
            i = 0
            while i < 10:
                sm.spawnMob(PRISON_GUARD_BOAR, 3134 + (i*2), -139, False)
                sm.spawnMob(PRISON_GUARD_RHINO, 3131 + (i*2), -139, False)
                i += 1
            sm.dropItem(PRISON_KEY, sm.getChr().getPosition().getX(), sm.getChr().getPosition().getY())
            sm.removeReactor()
            sm.dispose()