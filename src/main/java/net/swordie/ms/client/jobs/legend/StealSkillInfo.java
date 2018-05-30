package net.swordie.ms.client.jobs.legend;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.constants.SkillConstants;

/**
 * Created by Asura on 11-5-2018.
 */
public class StealSkillInfo {

    public void setSkill(Char chr, int skillID) { //Works
        int smJobID = SkillConstants.getStealSkillManagerTabFromSkill(skillID); //4
        int maxPos = SkillConstants.getMaxPosBysmJobID(smJobID); //1
        int startingPos = SkillConstants.getStartPosBysmJobID(smJobID); //11

        for(int i = startingPos; i <= (startingPos+maxPos); i++) {
            if(chr.getStolenSkills()[i] == 0) {

                chr.getStolenSkills()[i] = skillID;
                chr.setStolenSkills(chr.getStolenSkills());
                break;
            }
        }
    }

    public void removeSkill(Char chr, int skillID) {
        int smJobID = SkillConstants.getStealSkillManagerTabFromSkill(skillID); //4
        int maxPos = SkillConstants.getMaxPosBysmJobID(smJobID); //1
        int startingPos = SkillConstants.getStartPosBysmJobID(smJobID); //11

        for(int i = startingPos; i <= (startingPos+maxPos); i++) {
            if(chr.getStolenSkills()[i] == skillID) {
                chr.getStolenSkills()[i] = 0;
                chr.setStolenSkills(chr.getStolenSkills());
                break;
            }
        }
    }

    public int getEmptyPosition(Char chr, int skillID) {
        int smJobID = SkillConstants.getStealSkillManagerTabFromSkill(skillID);
        int maxPos = SkillConstants.getMaxPosBysmJobID(smJobID);
        int startingPos = SkillConstants.getStartPosBysmJobID(smJobID);
        int pos = 5;

        for(int i = startingPos; i <= (startingPos+maxPos); i++) {
            if(chr.getStolenSkills()[i] == 0) {
                pos = (i - startingPos);
                break;
            }
        }
        return pos;
    }

    public int getPositionBySkillID(Char chr, int skillID) {
        int smJobID = SkillConstants.getStealSkillManagerTabFromSkill(skillID);
        int maxPos = SkillConstants.getMaxPosBysmJobID(smJobID);
        int startingPos = SkillConstants.getStartPosBysmJobID(smJobID);
        int pos = 5;

        for(int i = startingPos; i <= (startingPos+maxPos); i++) {
            if(chr.getStolenSkills()[i] == skillID) {
                pos = (i - startingPos);
            }
        }
        return pos;
    }
}