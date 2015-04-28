package org.whut.platform.fundamental.websocket;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created with IntelliJ IDEA.
 * User: tgq
 * Date: 14-9-3
 * Time: 上午8:54
 * To change this template use File | Settings | File Templates.
 */
public class WsStartListener implements ServletContextListener {

    private WebApplicationContext springContext;

    private WebsocketMessageListener websocketMessageListener;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        springContext =  WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        websocketMessageListener = (WebsocketMessageListener) springContext.getBean("websocketMessageListener");
        websocketMessageListener.register(new ActiveMQQueue(Constants.WWBSOCKEY_QUEUE_DESTINATION));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
