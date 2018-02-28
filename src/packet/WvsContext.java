package packet;

import client.character.*;
import client.character.items.Equip;
import client.character.items.Item;
import client.character.skills.Skill;
import client.character.skills.TemporaryStatManager;
import client.jobs.resistance.WildHunterInfo;
import client.life.movement.*;
import connection.InPacket;
import connection.OutPacket;
import enums.InvType;
import enums.InventoryOperation;
import enums.MessageType;
import enums.Stat;
import handling.OutHeader;
import util.FileTime;
import util.Position;

import java.util.*;

import static enums.MessageType.*;

/**
 * Created on 12/22/2017.
 */
public class WvsContext {

    public static void dispose(Char chr) {
        chr.dispose();
    }

    public static OutPacket exclRequest() {
        return new OutPacket(OutHeader.EXCL_REQUEST);
    }

    public static OutPacket statChanged(Map<Stat, Object> stats) {
        return statChanged(stats, true, (byte) -1, (byte) 0, (byte) 0, (byte) 0, false, 0, 0);
    }

    public static OutPacket statChanged(Map<Stat, Object> stats, boolean exclRequestSent, byte mixBaseHairColor,
                                        byte mixAddHairColor, byte mixHairBaseProb, byte charmOld, boolean updateCovery,
                                        int hpRecovery, int mpRecovery) {
        OutPacket outPacket = new OutPacket(OutHeader.STAT_CHANGED);

        outPacket.encodeByte(exclRequestSent);
        // GW_CharacterStat::DecodeChangeStat
        int mask = 0;
        for(Stat stat : stats.keySet()) {
            mask |= stat.getVal();
        }
        outPacket.encodeLong(mask);
        Comparator statComper = Comparator.comparingInt(o -> ((Stat) o).getVal());
        TreeMap<Stat, Object> sortedStats = new TreeMap<>(statComper);
        sortedStats.putAll(stats);
        for(Map.Entry<Stat, Object> entry : sortedStats.entrySet()) {
            Stat stat = entry.getKey();
            Object value = entry.getValue();
            switch(stat) {
                case skin:
                case level:
                case fatigue:
                    outPacket.encodeByte((Byte) value);
                    break;
                case face:
                case hair:
                case hp:
                case mhp:
                case mp:
                case mmp:
                case pop:
                case charismaEXP:
                case insightEXP:
                case willEXP:
                case craftEXP:
                case senseEXP:
                case charmEXP:
                case eventPoints:
                    outPacket.encodeInt((Integer) value);
                    break;
                case str:
                case dex:
                case inte:
                case luk:
                case ap:
                    outPacket.encodeShort((Short) value);
                    break;
                case sp:
                    if(value instanceof ExtendSP) {
                        ((ExtendSP) value).encode(outPacket);
                    } else {
                        outPacket.encodeShort((Short) value);
                    }
                    break;
                case exp:
                case money:
                    outPacket.encodeLong((Long) value);
                    break;
                case dayLimit:
                    ((NonCombatStatDayLimit) value).encode(outPacket);
                    break;
                case albaActivity:
                    //TODO
                    break;
                case characterCard:
                    ((CharacterCard) value).encode(outPacket);
                    break;
                case pvp1:
                case pvp2:
                    break;
                case subJob:
                    outPacket.encodeShort((Short) value);
                    outPacket.encodeShort(0);
            }
        }

        outPacket.encodeByte(mixBaseHairColor);
        outPacket.encodeByte(mixAddHairColor);
        outPacket.encodeByte(mixHairBaseProb);
        outPacket.encodeByte(charmOld > 0);
        if(charmOld > 0) {
            outPacket.encodeByte(charmOld);
        }
        outPacket.encodeByte(updateCovery);
        if(updateCovery) {
            outPacket.encodeInt(hpRecovery);
            outPacket.encodeInt(mpRecovery);
        }
        return outPacket;
    }

    public static OutPacket inventoryOperation(boolean exclRequestSent, boolean notRemoveAddInfo, InventoryOperation type, short oldPos, short newPos,
                                               int bagPos, Item item) {
        OutPacket outPacket = new OutPacket(OutHeader.INVENTORY_OPERATION);

        outPacket.encodeByte(exclRequestSent);
        outPacket.encodeByte(1); // size
        outPacket.encodeByte(notRemoveAddInfo);

        outPacket.encodeByte(type.getVal()); // move
        outPacket.encodeByte(item.getInvType().getVal());
        outPacket.encodeShort(oldPos);
        switch(type) {
            case ADD: // new or update
                item.encode(outPacket);
                break;
            case UPDATE_QUANTITY: // Quantity change
                outPacket.encodeShort(item.getQuantity());
                break;
            case MOVE:  // move
                outPacket.encodeShort(newPos);
                if (item.getInvType() == InvType.EQUIP && (oldPos < 0 || newPos < 0)) {
                    outPacket.encodeByte(item.getCashItemSerialNumber() > 0);
                }
                break;
            case REMOVE: // remove
                break;
            case ITEM_EXP:
                outPacket.encodeLong(((Equip) item).getExp());
                break;
            case UPDATE_BAG_POS:
                outPacket.encodeInt(bagPos);
                break;
            case UPDATE_BAG_QUANTITY:
                outPacket.encodeShort(newPos);
                break;
            case UNK_1:
                break;
            case UNK_2:
                outPacket.encodeShort(bagPos); // ?
                break;
            case UPDATE_ITEM_INFO:
                item.encode(outPacket);
                break;
            case UNK_3:
                break;
        }
        return outPacket;
    }

    public static OutPacket updateEventNameTag(int[] tags) {
        OutPacket outPacket = new OutPacket(OutHeader.EVENT_NAME_TAG);

        for (int i = 0; i < 5; i++) {
            outPacket.encodeString("");
            if(i >= tags.length) {
                outPacket.encodeByte(-1);
            } else {
                outPacket.encodeByte(tags[i]);
            }
        }

        return outPacket;
    }

    public static OutPacket changeSkillRecordResult(List<Skill> skills, boolean exclRequestSent, boolean showResult,
                                                    boolean removeLinkSkill, boolean sn) {
        OutPacket outPacket = new OutPacket(OutHeader.CHANGE_SKILL_RECORD_RESULT);

        outPacket.encodeByte(exclRequestSent);
        outPacket.encodeByte(showResult);
        outPacket.encodeByte(removeLinkSkill);
        outPacket.encodeShort(skills.size());
        for(Skill skill : skills) {
            outPacket.encodeInt(skill.getSkillId());
            outPacket.encodeInt(skill.getCurrentLevel());
            outPacket.encodeInt(skill.getMasterLevel());
            outPacket.encodeFT(new FileTime(0));
        }
        outPacket.encodeByte(sn);

        return outPacket;
    }

    public static List<Movement> parseMovement(InPacket inPacket) {
        List<Movement> res = new ArrayList<>();
        byte size = inPacket.decodeByte();
        for (int i = 0; i < size; i++) {
            byte type = inPacket.decodeByte();
            switch (type) {
                case 0:
                case 8:
                case 15:
                case 17:
                case 19:
                case 67:
                case 68:
                case 69:
                    res.add(new Movement1(inPacket, type));
                    break;
                case 56:
                case 66:
                case 85:
                    res.add(new Movement2(inPacket, type));
                    break;
                case 1:
                case 2:
                case 18:
                case 21:
                case 22:
                case 24:
                case 62:
                case 63:
                case 64:
                case 65:
                    res.add(new Movement3(inPacket, type));
                    break;
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 57:
                case 58:
                case 59:
                case 60:
                case 70:
                case 71:
                case 72:
                case 74:
                case 79:
                case 81:
                case 83:
                    res.add(new Movement4(inPacket, type));
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 26:
                case 27:
                case 52:
                case 53:
                case 54:
                case 61:
                case 76:
                case 77:
                case 78:
                case 80:
                case 82:
                    res.add(new Movement5(inPacket, type));
                    break;
                case 14:
                case 16:
                    res.add(new Movement6(inPacket, type));
                    break;
                case 23:
                    res.add(new Movement7(inPacket, type));
                    break;
                case 12:
                    res.add(new Movement8(inPacket, type));
                    break;
                default:
                    System.out.printf("The type (%s) is unhandled. %n", type);
                    break;
            }
        }
        return res;
    }

    public static OutPacket temporaryStatSet(TemporaryStatManager tsm) {
        OutPacket outPacket = new OutPacket(OutHeader.TEMPORARY_STAT_SET);

        boolean hasMovingAffectingStat = tsm.hasNewMovingEffectingStat(); // encoding flushes new stats
        tsm.encodeForLocal(outPacket);

        outPacket.encodeInt(0); // ?
        outPacket.encodeShort(1);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        if(hasMovingAffectingStat) {
            outPacket.encodeByte(0);
        }

        return outPacket;
    }

    public static OutPacket temporaryStatReset(TemporaryStatManager temporaryStatManager, boolean demount) {
        OutPacket outPacket = new OutPacket(OutHeader.TEMPORARY_STAT_RESET);

        for(int i : temporaryStatManager.getRemovedMask()) {
            outPacket.encodeInt(i);
        }
//        temporaryStatManager.getRemovedStats().forEach((cts, option) -> outPacket.encodeInt(0));
        temporaryStatManager.encodeRemovedIndieTempStat(outPacket);
        if(temporaryStatManager.hasRemovedMovingEffectingStat()) {
            outPacket.encodeByte(0);
        }
        outPacket.encodeByte(0); // ?
        outPacket.encodeByte(demount);

        temporaryStatManager.getRemovedStats().clear();
        return outPacket;
    }

    public static OutPacket skillUseResult(boolean stillGoing) {
        OutPacket outPacket = new OutPacket(OutHeader.SKILL_USE_RESULT);
        // 2221011 - Frozen Breath
        outPacket.encodeByte(stillGoing);

        return outPacket;
    }

    public static OutPacket explosionAttack(int skillID, Position pos, int mobID, int count) {
        OutPacket outPacket = new OutPacket(OutHeader.EXPLOSION_ATTACK);

        outPacket.encodeInt(skillID);
        outPacket.encodeInt(pos.getX());
        outPacket.encodeInt(pos.getY());
        outPacket.encodeInt(mobID);
        outPacket.encodeInt(count);

        return outPacket;
    }

    public static OutPacket dropPickupMessage(int money, short internetCafeExtra, short smallChangeExtra) {
        return dropPickupMessage(money, (byte) 1, internetCafeExtra, smallChangeExtra, (short) 0);
    }

    public static OutPacket dropPickupMessage(Item item, short quantity) {
        return dropPickupMessage(item.getItemId(), (byte) 0, (short) 0, (short) 0, quantity);
    }

    public static OutPacket dropPickupMessage(int i, byte type, short internetCafeExtra, short smallChangeExtra, short quantity) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(DROP_PICKUP_MESSAGE.getVal());
        outPacket.encodeByte(type);
        // also error (?) codes -2, ,-3, -4, -5, <default>
        switch (type) {
            case 1: // Mesos
                outPacket.encodeByte(false); // boolean: portion was lost after falling to the ground
                outPacket.encodeInt(i); // Mesos
                outPacket.encodeShort(internetCafeExtra); // Internet cafe
                outPacket.encodeShort(smallChangeExtra); // Spotting small change
                break;
            case 0: // item
                outPacket.encodeInt(i);
                outPacket.encodeInt(quantity); // ?
                break;
            case 2: // ?
                outPacket.encodeInt(100);
                break;
        }

        return outPacket;
    }

    public static OutPacket questRecordMessage(int qrKey, byte state, boolean validCheck) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        if(validCheck) {
            outPacket.encodeByte(QUEST_RECORD_MESSAGE_ADD_VALID_CHECK.getVal());
            outPacket.encodeInt(qrKey);
            outPacket.encodeByte(validCheck);
            outPacket.encodeByte(state);
        } else {
            outPacket.encodeByte(QUEST_RECORD_MESSAGE.getVal());
            outPacket.encodeInt(qrKey);
            outPacket.encodeByte(state);
        }

        return outPacket;
    }

    public static OutPacket incExpMessage(ExpIncreaseInfo eii) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(INC_EXP_MESSAGE.getVal());
        eii.encode(outPacket);

        return outPacket;
    }

    public static OutPacket incSpMessage(short job, byte amount) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(INC_SP_MESSAGE.getVal());
        outPacket.encodeShort(job);
        outPacket.encodeByte(amount);

        return outPacket;
    }

    public static OutPacket incMoneyMessage(String clientName, int amount, int charID) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(INC_MONEY_MESSAGE.getVal());
        outPacket.encodeInt(amount);
        outPacket.encodeInt(1);
        outPacket.encodeString(clientName);

        return outPacket;
    }

    /**
     * Returns a packet for messages with the following {@link MessageType}:<br>
     * GENERAL_ITEM_EXPIRE_MESSAGE<br>
     * ITEM_PROTECT_EXPIRE_MESSAGE<br>
     * ITEM_ABILITY_TIME_LIMITED_EXPIRE_MESSAGE<br>
     * SKILL_EXPIRE_MESSAGE
     * @param mt The message type.
     * @param items The list of ints that should be encoded.
     * @return The message OutPacket.
     */
    public static OutPacket message(MessageType mt, List<Integer> items) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(mt.getVal());
        switch(mt) {
            case GENERAL_ITEM_EXPIRE_MESSAGE:
            case ITEM_PROTECT_EXPIRE_MESSAGE:
            case ITEM_ABILITY_TIME_LIMITED_EXPIRE_MESSAGE:
            case SKILL_EXPIRE_MESSAGE:
                outPacket.encodeByte(items.size());
                items.forEach(outPacket::encodeInt);
                break;
        }
        return outPacket;
    }

    public static OutPacket itemExpireReplaceMessage(List<String> strings) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(ITEM_EXPIRE_REPLACE_MESSAGE.getVal());
        outPacket.encodeByte(strings.size());
        strings.forEach(outPacket::encodeString);

        return outPacket;
    }

    /**
     * Returns a packet for messages with the following {@link MessageType}:<br>
     *     int: <br>
     *     CASH_ITEM_EXPIRE_MESSAGE<br>
     *     INC_POP_MESSAGE<br>
     *     INC_GP_MESSAGE<br>
     *     GIVE_BUFF_MESSAGE<br><br>
     *     int + byte: <br>
     *     INC_COMMITMENT_MESSAGE<br><br>
     *     String: <br>
     *     SYSTEM_MESSAGE<br><br>
     *     int + String: <br>
     *     QUEST_RECORD_EX_MESSAGE<br>
     *     WORLD_SHARE_RECORD_MESSAGE<br>
     * @param mt The message type.
     * @param i The integer to encode.
     * @param string The String to encode.
     * @param type The type (byte) to encode.
     * @return The message OutPacket.
     */
    public static OutPacket message(MessageType mt, int i, String string, byte type) {
        OutPacket outPacket = new OutPacket(OutHeader.MESSAGE);

        outPacket.encodeByte(mt.getVal());
        switch(mt) {
            case CASH_ITEM_EXPIRE_MESSAGE:
            case INC_POP_MESSAGE:
            case INC_GP_MESSAGE:
            case GIVE_BUFF_MESSAGE:
                outPacket.encodeInt(i);
                break;
            case INC_COMMITMENT_MESSAGE:
                outPacket.encodeInt(i);
                outPacket.encodeByte(type);
                break;
            case SYSTEM_MESSAGE:
                outPacket.encodeString(string);
                break;
            case QUEST_RECORD_EX_MESSAGE:
            case WORLD_SHARE_RECORD_MESSAGE:
                outPacket.encodeInt(i);
                outPacket.encodeString(string);
        }

        return outPacket;
    }

    public static OutPacket flipTheCoinEnabled(byte enabled) {
        OutPacket outPacket = new OutPacket(OutHeader.SET_FLIP_THE_COIN_ENABLED);

        outPacket.encodeByte(enabled);

        return outPacket;
    }

    public static OutPacket modComboResponse(int combo) {
        OutPacket outPacket = new OutPacket(OutHeader.MOD_COMBO_RESPONSE);

        outPacket.encodeInt(combo);

        return outPacket;
    }

    public static OutPacket wildHunterInfo(WildHunterInfo whi) {
        OutPacket outPacket = new OutPacket(OutHeader.WILD_HUNTER_INFO);

        whi.encode(outPacket);

        return outPacket;
    }

    public static OutPacket zeroInfo(ZeroInfo currentInfo) {
        OutPacket outPacket = new OutPacket(OutHeader.ZERO_INFO);

        currentInfo.encode(outPacket);

        return outPacket;
    }

    public static OutPacket gatherItemResult(byte type) {
        OutPacket outPacket = new OutPacket(OutHeader.GATHER_ITEM_RESULT);

        outPacket.encodeByte(0); // doesn't get used
        outPacket.encodeByte(type);

        return outPacket;
    }

    public static OutPacket sortItemResult(byte type) {
        OutPacket outPacket = new OutPacket(OutHeader.GATHER_ITEM_RESULT);

        outPacket.encodeByte(0); // doesn't get used
        outPacket.encodeByte(type);

        return outPacket;
    }
}
