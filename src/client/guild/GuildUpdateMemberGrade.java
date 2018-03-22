package client.guild;

import connection.OutPacket;
import enums.GuildResultType;

/**
 * Created on 3/21/2018.
 */
public class GuildUpdateMemberGrade implements GuildResultInfo {

    public int guildID;
    public GuildMember gm;

    @Override
    public GuildResultType getType() {
        return GuildResultType.UpdateMemberGrade;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(guildID);
        outPacket.encodeInt(gm.getCharID());
        outPacket.encodeByte(gm.getGrade());
    }
}
