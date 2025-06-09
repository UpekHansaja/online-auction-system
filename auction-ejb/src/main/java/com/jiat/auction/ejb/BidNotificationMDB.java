package com.jiat.auction.ejb;

import com.jiat.auction.model.Bid;
import com.jiat.auction.websocket.AuctionEndpoint;
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

                System.out.println("Received bid: $" + bid.getAmount() +
                        " for auction: " + bid.getAuctionId() +
                        " from user: " + bid.getUserId());

                // Broadcast the bid to all connected WebSocket clients.
                AuctionEndpoint.broadcastBidUpdate(bid.getAuctionId(), bid);

                System.out.println("Broadcasted bid update to " +
                        AuctionEndpoint.getConnectedClientsCount(bid.getAuctionId()) +
                        " connected clients");
            }
        } catch (JMSException e) {
            System.err.println("Error processing bid notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}