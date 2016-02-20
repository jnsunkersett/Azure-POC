package com.psl.sender;

import javax.jms.JMSException;

public interface Sender {

	 public void sendMessage(String stringToSend) throws JMSException;
}
