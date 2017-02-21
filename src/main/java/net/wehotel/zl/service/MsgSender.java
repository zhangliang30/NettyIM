package net.wehotel.zl.service;

import io.netty.channel.Channel;
import net.wehotel.zl.api.domain.ChatMsgDomain;
import net.wehotel.zl.db.entity.ChatMsgInfo;
import net.wehotel.zl.service.dbservice.ChatMsgDBService;
import net.wehotel.zl.util.GsonUtil;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgSender {
    private Logger logger = LoggerFactory.getLogger(MsgSender.class);

    @Autowired
    private ClientStatusService clientStatusService;
    @Autowired
    private ChatMsgDBService chatMsgDBService;

    public void sendSimpleMsg(ChatMsgDomain msgDomain) {
        Channel receiverChannel = clientStatusService.getClientById(msgDomain.getReceiverid());
        if (receiverChannel != null) {
            receiverChannel.write(GsonUtil.toJsonStr(msgDomain));
        } else {
            ChatMsgInfo record = new ChatMsgInfo();
            try {
                PropertyUtils.copyProperties(record, msgDomain);
                chatMsgDBService.insert(record);
            } catch (Exception e) {
                logger.error("消息入库失败,msg:" + msgDomain.toString(), e);
            }
        }
    }
}
