package org.whut.platform.fundamental.communication.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.whut.platform.fundamental.communication.api.MinaMessageDispatcher;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tgq
 * Date: 15-3-4
 * Time: 下午9:43
 * To change this template use File | Settings | File Templates.
 */
public class MinaServerHanlder extends IoHandlerAdapter {
    public static final PlatformLogger logger = PlatformLogger.getLogger(MinaServerHanlder.class);
    public static HashMap<IoSession,StringBuffer> msgBufferMap = new HashMap<IoSession, StringBuffer>();

    @Autowired
    private MinaMessageDispatcher minaMessageDispatcher;

    public MinaMessageDispatcher getMinaMessageDispatcher() {
        return minaMessageDispatcher;
    }

    public void setMinaMessageDispatcher(MinaMessageDispatcher minaMessageDispatcher) {
        this.minaMessageDispatcher = minaMessageDispatcher;
    }

    public StringBuffer getMsgBuffer(IoSession session){
        StringBuffer msgBuffer = msgBufferMap.get(session);
        if(msgBuffer==null){
            msgBuffer = new StringBuffer();
            msgBufferMap.put(session,msgBuffer);
        }
        return msgBuffer;
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message)throws Exception {
        String str = message.toString();
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.close(Boolean.TRUE);
            return;
        }
        resolveMessage(str,session);
        logger.info("server -消息已经接收到!"+message);
    }

    private void resolveMessage(String msg,IoSession key){
        //client.register(selector, SelectionKey.OP_WRITE);
        //对接受数据的处理
        StringBuffer msgBuffer = getMsgBuffer(key);
        String localMsg = msg;
        while (true){
            int startIndex = localMsg.indexOf("{"+FundamentalConfigProvider.get("message.nioServer.type")+":[{");
            int endIndex = localMsg.indexOf("}]}",startIndex);
            if(endIndex>=0&&startIndex>=0){
                if(endIndex<startIndex){
                    if(msgBuffer.length()>0){
                        logger.info("text1:append: " + msg.substring(0, endIndex + 3));
                        msgBuffer.append(msg.substring(0,endIndex+3));
                        minaMessageDispatcher.dispatchMessage(msgBuffer.toString());
                        msgBuffer.setLength(0);
                    }
                    localMsg = localMsg.substring(startIndex);
                }else{
                    logger.info("text2:append: "+localMsg.substring(startIndex,endIndex+3));
                    minaMessageDispatcher.dispatchMessage(localMsg.substring(startIndex, endIndex + 3));
                    if(msgBuffer.length()>0){
                        msgBuffer.setLength(0);
                    }
                    localMsg = localMsg.substring(endIndex+3);
                }
            }else if(endIndex>=0&&startIndex<0){
                if(msgBuffer.length()>0){
                    logger.info("text3:append: " + msg.substring(0, endIndex + 3));
                    msgBuffer.append(msg.substring(0,endIndex+3));
                    minaMessageDispatcher.dispatchMessage(msgBuffer.toString());
                    msgBuffer.setLength(0);
                }
                break;
            }else if(endIndex<0&&startIndex>=0){
                if(msgBuffer.length()>0){
                    msgBuffer.setLength(0);
                }
                msgBuffer.append(localMsg.substring(startIndex));
                break;
            }else {
                if(msgBuffer.length()>0){
                    msgBuffer.append(localMsg);
                }
                break;
            }

        }

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("server-session关闭连接断开");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("server-session创建，建立连接");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)throws Exception {
        logger.info("server-服务端进入空闲状态..");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("server-服务端与客户端连接打开...");
    }
}

