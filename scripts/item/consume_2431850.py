#Mystic Stolen Boss (2431850)

from net.swordie.ms.client.character.skills.temp import CharacterTemporaryStat

def init():
    sm.heal()
    sm.giveCTS(CharacterTemporaryStat.PAD, 30, -parentID, 180) # Item's rOption are negative as opposed to Skills