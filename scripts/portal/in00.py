status = -1

from net.swordie.ms.constants import GameConstants

if sm.getFieldID() == 951000000:
	# Monster Park

	minLv = 105
	maxLv = 115

	maps = [
		["Auto Security Area (Lv.105-114)", 953020000]
	]
	sm.setSpeakerID(9071004)

	def init():
		if not sm.getParty() is None:
			sm.sendSayOkay("Please leave your party to enter Monster Park.")
			sm.dispose()
		else:
			if sm.getChr().getLevel() < minLv or sm.getChr().getLevel() > maxLv:
				sm.sendSayOkay("You need to be between Level "+ str(minLv) +" and "+ str(maxLv) +" to enter.")
				sm.dispose()
			else:
				if sm.getMonsterParkCount() >= GameConstants.MAX_MONSTER_PARK_RUNS:
					colour = "#r"
				else:
					colour = "#b"
				string = "#eToday is #b"+ sm.getDay() +"#k.\r\nToday's Clear Count "+ colour +""+ str(sm.getMonsterParkCount()) +"/"+ str(GameConstants.MAX_MONSTER_PARK_RUNS) +"#k (per Maple Character)\r\n\r\nYou have #b"+ str(2) +"#k free clears left for today.\r\n\r\n#n#b"
				i = 0
				while i < len(maps):
					string += "#L"+ str(i) +"#"+ maps[i][0] +"#l\r\n"
					i += 1
				sm.sendNext(string)

	def action(response, answer):
		global status, selection
		status += 1

		if status == 0:
			if sm.getMonsterParkCount() >= GameConstants.MAX_MONSTER_PARK_RUNS:
				sm.sendSayOkay("I'm sorry, but you've used up all your clears for today.")
				sm.dispose()
			else:
				selection = answer
				sm.sendAskYesNo("#eToday is #b"+ sm.getDay() +"#k.\r\n\r\n"
								"Selected Dungeon: #b"+ maps[selection][0] +"#k\r\n"
								"Clearing the dungeon will use up #bone of your free clears#k \r\nfor today.\r\n\r\n"
								"Would you like to enter the dungeon?")


		elif status == 1:
			if response == 1:
				sm.warpInstanceIn(maps[selection][1])
				sm.incrementMonsterParkCount()
				sm.createQuestWithQRValue(GameConstants.MONSTER_PARK_EXP_QUEST, "0")
			sm.dispose()




else:
	fields = {
		# fromField : [toField, portal]
		240010500 : [240010501, 1],
		103020000 : [103020100, 2],
		200000300 : [200000301, 3],
		103020310 : [103020320, 2],
		260010601 : [915020100, 1],
		915020100 : [915020101, 1],
		106030100 : [106030000, 2],# Mush Castle Castle Entrace : Banquet Hall
		106030200 : [106030300, 2],
		120041800 : [120041900, 2],
		106030501 : [106030600, 2],
		271030600 : [271040000, 5],
		863010300 : [863010310, 1],
		863010400 : [863010410, 1],
		863010220 : [863010230, 1],
		863010230 : [863010240, 0],
		863010210 : [863010240, 0],
		863010240 : [863010500, 0],
		863010500 : [863010600, 0],
		863010320 : [863010330, 0],
		863010420 : [863010430, 0],
		221023200 : [221023300, 0],
		223000000 : [223010000, 1],
		223010100 : [223010110, 0],
		200020001 : [915020000, 2],
		915020000 : [915020001, 2],
		915020200 : [915020201, 2],
		240010102 : [915020200, 1],
		200090510 : [270000100, 2],# Dragon Flight 2nd Map  ->  Temple of Time
		310050100 : [931000200, 1],
		310060221 : [931000300, 0],
		222020000 : [922030400, 0],
		270000000 : [270010000, 3],
		270010100 : [270010110, 0],
		270010200 : [270010300, 0],
		270010300 : [270010400, 5],
		270010400 : [270010500, 0],
		931000001 : [931000010, 0],
		252010300 : [252020000, 0],
		910150001 : [910150004, 0], # FFF : Elluel  ->  FFF : Royal Chamber		(FFF = Frozen Fairy Forest)
	}

	def init():
		currentMap = sm.getFieldID()
		warp = True

		if currentMap == 103030100:
			warp = False
			sm.chatRed("There seems to be a mysterious presence blocking you from entering.")
			sm.dispose()

		elif currentMap == 102010100:
			warp = False
			sm.chatRed("There seems to be a mysterious presence blocking you from entering.")
			sm.dispose()

		elif currentMap == 310050100: # Verne Mine : Power Plant Lobby
			sm.chat("Destroy the Energy Conducting Device!")
			# warp is meant to stay True

		elif currentMap == 310060221: # Hidden Street : Leery Corridor
			if sm.hasQuest(23043):
				sm.completeQuest(23043)

		elif currentMap == 222020000: # Ludi tower: Helios Tower <Library> (CoK 3rd job portal)
			if not sm.hasQuest(20880): # 3rd job quest
				warp = False
				sm.chat("Only knights looking to job advance to the third job may enter here.")
				sm.dispose()


		# ToT Portals
		elif currentMap == 270000000: # Time Lane: Three doors
			if not sm.hasQuestCompleted(3500): # time lane quest
				warp = False
				sm.chat("You have not completed the appropriate quest to enter here.")
				sm.dispose()

		elif currentMap == 270010100: # Time Lane: Memory Lane 1
			if not sm.hasQuestCompleted(3501): # time lane quest
				warp = False
				sm.chat("You have not completed the appropriate quest to enter here.")
				sm.dispose()

		elif currentMap == 270010200: # Time Lane: Memory Lane 2
			if not sm.hasQuestCompleted(3502): # time lane quest
				warp = False
				sm.chat("You have not completed the appropriate quest to enter here.")
				sm.dispose()

		elif currentMap == 270010300: # Time Lane: Memory Lane 3
			if not sm.hasQuestCompleted(3503): # time lane quest
				warp = False
				sm.chat("You have not completed the appropriate quest to enter here.")
				sm.dispose()

		elif currentMap == 270010400: # Time Lane: Memory Lane 4
			if not sm.hasQuestCompleted(3504): # time lane quest
				warp = False
				sm.chat("You have not completed the appropriate quest to enter here.")
				sm.dispose()

		elif currentMap == 910150001: # Frozen Fairy Forest : Elluel
			if not sm.hasQuest(24005): # Cursed Slumber
				warp = False
				sm.dispose()

		# Boss Portal
		elif currentMap == 223030200:
			warp = False
			sm.sendAskYesNo("Would you like to battle scarlion and targa?")

		elif currentMap == 271040000:
			warp = False
			sm.sendAskYesNo("Would you like to battle cygnus?")


		# Default script
		elif currentMap not in fields:
			sm.chat("(Portal - in00) This script isn't coded for this map.")
			warp = False
			sm.dispose()

		# Warp
		if warp:
			sm.warp(fields[currentMap][0], fields[currentMap][1])
			sm.dispose()

	def action(response, answer):
		currentMap = sm.getFieldID()
		if response == 1:
			if sm.getParty() is None:
				sm.sendSayOkay("Please create a party before going in.")
			elif not sm.isPartyLeader():
				sm.sendSayOkay("Please have your party leader enter if you wish to face Cygnus.")
			elif sm.checkParty():
				if currentMap == 271040000:
					sm.warpPartyIn(271040100)
				elif currentMap == 223030200:
					sm.warpPartyIn(223030210)
		sm.dispose()