package client.guild;

import connection.OutPacket;
import enums.GuildResultType;

/**
 * Created on 3/21/2018.
 */
public class GuildMsg implements GuildResultInfo {

    public GuildResultType type;

    public GuildMsg() {
    }

    public GuildMsg(GuildResultType type) {
        this.type = type;
    }

    @Override
    public GuildResultType getType() {
        return type;
    }

    @Override
    public void encode(OutPacket outPacket) {

    }
}
