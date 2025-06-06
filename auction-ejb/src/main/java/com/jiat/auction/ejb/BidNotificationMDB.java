package com.jiat.auction.ejb;

import com.jiat.auction.model.Bid;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/AuctionQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
        }
)
public class BidNotificationMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Bid bid = (Bid) objectMessage.getObject();
                System.out.println("Received bid: " + bid.getAmount() + " for auction: " + bid.getAuctionId());

                // Broadcast the bid to all clients using a WebSocket if needed.

            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}