package com.jiat.auction.ejb.impl;

import com.jiat.auction.ejb.AuctionService;
import com.jiat.auction.model.Auction;
import com.jiat.auction.model.Bid;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.jms.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(AuctionService.class)
public class AuctionServiceImpl implements AuctionService {

    private final ConcurrentMap<String, Auction> auctions = new ConcurrentHashMap<>();

    @Resource(lookup = "jms/AuctionConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/AuctionQueue")
    private Queue auctionQueue;

    @Override
    public void createAuction(Auction auction) {
        auctions.put(auction.getId(), auction);
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return new ArrayList<>(auctions.values());
    }

    @Override
    public synchronized void placeBid(Bid bid) {
        Auction auction = auctions.get(bid.getAuctionId());
        if (auction != null && auction.isActive()) {
            if (bid.getAmount() > auction.getCurrentBid()) {
                auction.setCurrentBid(bid.getAmount());
                sendBidNotification(bid);
            }
        }
    }

    @Override
    public Auction getAuctionById(String auctionId) {
        return auctions.get(auctionId);
    }

    @Override
    public List<Bid> getBidsForAuction(String auctionId) {
        return List.of();
    }

    @Override
    public Bid getHighestBidForAuction(String auctionId) {
        return null;
    }

    @Override
    public Map<String, Object> getTimeRemaining(String auctionId) {
        Auction auction = auctions.get(auctionId);
        Map<String, Object> result = new HashMap<>();
        if (auction != null) {
            long seconds = ChronoUnit.SECONDS.between(
                    LocalDateTime.now(),
                    auction.getEndTime()
            );
            // Format seconds into days/ hours/ minutes/ seconds
            long days = seconds / 86400;
            seconds %= 86400;
            long hours = seconds / 3600;
            seconds %= 3600;
            long minutes = seconds / 60;
            seconds %= 60;
            result.put("days", days);
            result.put("hours", hours);
            result.put("minutes", minutes);
            result.put("seconds", seconds);
        }
        return result;
    }

    private void sendBidNotification(Bid bid) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageProducer producer = session.createProducer(auctionQueue)) {

            ObjectMessage message = session.createObjectMessage(bid);
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}