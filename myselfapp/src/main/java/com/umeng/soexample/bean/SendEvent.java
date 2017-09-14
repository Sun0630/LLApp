package com.umeng.soexample.bean;

/**
 * 作者：刘磊 on 2015-12-16
 * 邮箱：18682176281@163.com
 * EventBus实体类
 */
public class SendEvent {
    //需要发送的消息类型,可以是任何类型
    private CircleVO circleVO;

    public SendEvent(CircleVO event){
        circleVO = event;
    }
    public CircleVO getMsg(){
        return circleVO;
    }
}
